package org.battleships;

import org.jspace.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class GameController {
    static int BOARD_SIZE = 5;
    static int NUMBER_OF_SHIPS = 3;
    static int player; // Variable that store whether you are player 1 or 2
    static int opponent, winner;
    static int targetX, targetY;
    static boolean firstMove;
    static boolean gameNotFound = true;
    static int host = 1;
    static String opponentIp,opponentPort;
    static String serverIp = "192.168.1.2";
    static String playerIp = "192.168.1.2";
    static String serverPort = "3456";
    static String playerPort;
    static RemoteSpace server;
    static Space messages;
    static RemoteSpace opponentBoard;
    static Space myBoard;
    static SpaceRepository repository;
    static GameView view;
    static GameModel model;
    static Ship[] ships;

    public static void main(String[] args) {
        try {
            setDetails();
            findGame();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void newGameCycle() throws InterruptedException, IOException {
        System.out.println("Starting the game...");
        setup();
        createShips();
        placeShips();
        readyUp();
        view.updateBoard();
        // Game loop
        while (true) {
            waitForTurn();
            updatePlayerBoard();

            if(hasWon(myBoard,opponent)) break;

            getShotTarget();
            shootAtTarget();
            endTurn();
            if(hasWon(opponentBoard,player)) break;

            view.updateBoard();
        }
        endGame();
    }

    private static void readyUp() throws InterruptedException {
        myBoard.put("ready");
        System.out.println("Waiting for the other player to finish placing their ships...");
        if(player == host){
            myBoard.get(new ActualField("ready"));
            opponentBoard.get(new ActualField("ready"));
            myBoard.put("token");
        }
    }

    private static void setup() throws IOException {
        view = new GameView(BOARD_SIZE,BOARD_SIZE);
        model = new GameModel(BOARD_SIZE, BOARD_SIZE);
        ships = new Ship[NUMBER_OF_SHIPS];
        firstMove = true;
        repository.remove("board");
        myBoard = new PileSpace();
        opponentBoard = new RemoteSpace("tcp://" + opponentIp + ":" + opponentPort + "/board?keep");
        repository.add("board", myBoard);
    }

    private static void findGame() throws InterruptedException, IOException {
        playerPort = generatePort();
        server = new RemoteSpace("tcp://" + serverIp + ":" + serverPort + "/server?keep");
        messages = new SequentialSpace();
        repository = new SpaceRepository();
        repository.add("response", messages);
        repository.addGate("tcp://" + playerIp + ":" + playerPort + "/?keep");
        gameNotFound = true;
        System.out.println("Welcome to Battleships!");
        do{
            String[] options = {"create","join"};
            String input = getLegalInput(options);
            if(input.equalsIgnoreCase("create")) createGame();
            else if (input.equalsIgnoreCase("join")) joinGame();
        }while(gameNotFound);
        newGameCycle();
    }

    private static void createGame() throws InterruptedException{
        server.put("create", playerIp, playerPort, "");
        Object[] response = messages.get(new FormalField(String.class));
        String gameId = response[0].toString();
        System.out.println("The game id is: " + gameId);
        System.out.println("Waiting for another player to join...");
        server.put(gameId, "ok");
        response = messages.get(new FormalField(String.class), new FormalField(String.class));
        System.out.println("A player has joined the game!");
        opponentIp = response[0].toString();
        opponentPort = response[1].toString();
        assignPlayerWithNumber(1);
        gameNotFound = false;
    }

    private static void joinGame() throws InterruptedException {
        Scanner scan = new Scanner(System.in);
        while (gameNotFound) {
            System.out.println("Enter the game id:");
            String input = scan.nextLine().trim();
            System.out.println("Searching...");
            server.put("join", playerIp, playerPort, input);
            Object[] response = messages.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
            if (response[0].toString().equals("ok")) {
                System.out.println("Joining...");
                opponentIp = response[1].toString();
                opponentPort = response[2].toString();
                assignPlayerWithNumber(2);
                gameNotFound = false;
            } else {
                System.out.println("Could not find game with id: " + input);
            }
        }
    }

    private static String generatePort() {
        int minPort = 50000, maxPort = 65000;
        int port = (int) (Math.random() * (maxPort - minPort) + minPort);
        return Integer.toString(port);
    }

    private static void assignPlayerWithNumber(int number) {
        player = number;
        opponent = player == 1 ? 2 : 1;
    }

    public static void endGame() throws InterruptedException, IOException {
        System.out.println("Game over!");
        System.out.println(winner == player ? "You won!" : "You lost.");
        Continue();
    }

    public static void endTurn() throws InterruptedException {
        opponentBoard.put("token");
    }

    public static Point getInputPoint(){
        Scanner scan = new Scanner(System.in);
        Point p = new Point();
        System.out.println("Give the point where you want to place the ship in the format: x y");
        p.x = scan.nextInt();
        p.y = scan.nextInt();
        return p;
    }

    public static void placeShip(Ship ship, int id) throws InterruptedException {
        System.out.println("You are about to place the following ship.");
        view.preview(ship.getPreviewPoints(BOARD_SIZE));
        doRotations(ship);
        boolean illegal;
        do{
            Point p = getInputPoint();
            ship.setCenter(p);
            illegal = !model.isValidShipPlacement(ship,myBoard);
            if(illegal) System.out.println("You can't place the ship there.");
        }while(illegal);
        model.placeShip(ship,id,myBoard);
        System.out.println("Successfully placed the ship!");
    }

    public static void doRotations(Ship ship){
        String[] options = {"rotate","done"};
        while(true){
            System.out.println("You can rotate the ship 90 degrees clockwise or go on and place the ship.");
            String input = getLegalInput(options);
            if(input.equalsIgnoreCase("done")) break;
            else {
                ship.rotate();
                System.out.println("Ship rotated successfully!");
                view.preview(ship.getPreviewPoints(BOARD_SIZE));
            }
        }
    }

    public static void setCompleteShip(int id){
        for(Point p: ships[id-1].getActualPoints()){
            view.setShipYou(p.x,p.y);
        }
    }

    public static ArrayList<Point> getShip(int i){
        switch (i){
            case 0:
                return model.makeNbyM(2,1); // 2 x 1
            case 1:
                return model.makeL(); // L = (1 x 3 + 1 x 2)
            case 2:
                return model.makeNbyM(1,3); // 1 x 3
            default:
                return model.makeNbyM(1,1); // 1 x 1
        }
    }

    public static void createShips(){
        for(int i = 0; i < NUMBER_OF_SHIPS; i++){
            Ship ship = new Ship();
            ship.setTemplatePoints(getShip(i));
            ships[i] = ship;
        }
    }

    public static void placeShips() throws InterruptedException {
        for(int i = 0; i < NUMBER_OF_SHIPS; i++){
            placeShip(ships[i],i+1);
            setCompleteShip(i+1);
        }
    }

    public static void waitForTurn() throws InterruptedException, IOException {
        System.out.println("Waiting for the opponent to shoot...");

        //Timeout Implementation starts
        int timeout = 30;//how many seconds for a timout
        boolean breakout = false;

        while(timeout > 0){//runs timeout while checking if the token have been added to the board.
            if(myBoard.queryp(new ActualField("token")) != null){
                breakout = true;
                break;
            }
            if(myBoard.queryp(new ActualField("quit")) != null){
                receiveGracefulQuit();
            }
            if(timeout == 60 || timeout == 30 || timeout == 10 ||timeout < 6){
                System.out.println(timeout);
            }


            timeout--;
            TimeUnit.SECONDS.sleep(1);
        }

        if(!breakout){//time out action
            System.out.println("Your opponent took too long and got timed out!");
            initGracefulQuit();
        }
        //timeout implementation ends: if we get to here, turn is ready to be passed, or graceful should have fired
        myBoard.get(new ActualField("token"));
        System.out.println("Your turn");
    }

    public static boolean hasWon(Space board, int player) throws InterruptedException {
        boolean over = !model.containsAnyShip(1, NUMBER_OF_SHIPS, board);
        if (over){
            winner = player;
        }
        return over;
    }

    public static void getShotTarget() throws InterruptedException{
        // Input scanner
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Read input
            try {
                System.out.println("Enter coordinates of the target square");
                while(!br.ready()){
                    if(myBoard.queryp(new ActualField("quit")) != null){
                        receiveGracefulQuit();
                    }
                }
                int[] input = Arrays.stream(br.readLine().split("\\s+")).mapToInt(Integer::parseInt).toArray();
                targetX = input[0];
                targetY = input[1];
            } catch (Exception e) {
                System.out.println("Invalid input. Try again!");
                continue;
            }

            // Check input is in valid range
            if (!model.isInsideBoard(targetX, targetY)) {
                System.out.println("The point lies outside the board. Try again!");
                continue;
            }

            // Check square hasn't already been hit
            if (!model.hasShotAt(targetX, targetY, opponentBoard)) {
                break;
            }
            else System.out.println("You have already shot there. Try again!");
        }
    }

    public static void shootAtTarget() throws InterruptedException {
        System.out.println("Shooting...");
        int shipId = model.shootAt(targetX, targetY, opponentBoard);
        if (shipId == 0) {
            System.out.println("You missed.");
            view.markMissEnemy(targetX, targetY);
        } else {
            System.out.println("You hit a ship!");
            view.markHitEnemy(targetX, targetY);
            if (!model.containsShipWithId(shipId, opponentBoard)) {
                System.out.println("You sunk a ship!");
            }
        }
    }

    private static void updatePlayerBoard() {
        try {
            // Update board from opponents last move
            Object[] opponentsMove = myBoard.queryp(new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class));
            if (!(firstMove && player == 1)) {
                targetX = Integer.parseInt(opponentsMove[0].toString());
                targetY = Integer.parseInt(opponentsMove[1].toString());
                if (Integer.parseInt(opponentsMove[2].toString()) == 0) {
                    view.markMissYou(targetX, targetY);
                } else {
                    view.markHitYou(targetX, targetY);
                }
            } else {
                firstMove = false;
            }
            // Display updated board
            view.updateBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLegalInput(String[] options) {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean legalInput;
        do {
            System.out.println("You must choose one of the following options:" + Arrays.toString(options));
            input = scanner.nextLine();
            input = input.trim().toLowerCase();
            legalInput = Arrays.asList(options).contains(input);
        } while (!legalInput);
        return input;
    }

    private static Boolean playAgain(Space space) throws InterruptedException {
        FormalField STRING = new FormalField(String.class);
        String dec = space.get(STRING)[0].toString();
        return dec.equalsIgnoreCase("again");
    }

    private static void Continue() throws InterruptedException, IOException {
        String[] options = {"again","quit"};
        String decision = getLegalInput(options);
        myBoard.put(decision);
        if(player == host){
            Boolean p1 = playAgain(myBoard);
            System.out.println("Communicating with the other player... ");
            Boolean p2 = playAgain(opponentBoard);
            if (p1 && p2){
                opponentBoard.put("host","again");
                newGameCycle();
            }else{
                opponentBoard.put("host","quit");
                initGracefulQuit();
            }
        }
        else{
            Object[] obj = myBoard.get(new ActualField("host"),new FormalField(String.class));
            boolean isAgain = obj[1].toString().equalsIgnoreCase("again");
            if(isAgain){
                newGameCycle();
            }
            else receiveGracefulQuit();
        }
    }

    private static void initGracefulQuit() throws InterruptedException, IOException {
        System.out.println("You will now quit the game!");
        //send quit message
        opponentBoard.put("quit");
        //Receive Ok a delay shortly
        myBoard.get(new ActualField("closing"));
        //close game
        System.out.println("Returning to the start screen...");
        findGame();
    }

    private static void receiveGracefulQuit() throws InterruptedException, IOException {
        System.out.println("Your opponent has left the game.");
        opponentBoard.put("closing");
        //close game
        System.out.println("Returning to the start screen...");
        findGame();
    }

    private static void setDetails(){
        Scanner scan = new Scanner(System.in);
        String input;
        System.out.println("What is the server ip? eg. 192.168.1.2");
        input = scan.nextLine().trim();
        if(!input.equalsIgnoreCase("no")) serverIp = input;
        System.out.println("What is the server port? eg. 3456");
        input = scan.nextLine().trim();
        if(!input.equalsIgnoreCase("no")) serverPort = input;
        System.out.println("What is your ip? eg. 192.168.1.2");
        input = scan.nextLine().trim();
        if(!input.equalsIgnoreCase("no")) playerIp = input;
    }
}

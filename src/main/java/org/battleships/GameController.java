package org.battleships;
import org.jspace.*;

import java.awt.*;
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

    static String serverIp = "192.168.1.9";
    static String playerIp = "192.168.1.9";
    static String serverPort = "3333";
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
            findGame();
            newGameCycle();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void newGameCycle() throws InterruptedException, IOException {
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
        System.out.println("Waiting for the other player to place their ships...");
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

        Scanner scan = new Scanner(System.in);
        while (gameNotFound) {
            System.out.println("Type create or join");
            String input = scan.nextLine().trim();
            if (input.equalsIgnoreCase("create")) {
                createGame();
            } else if (input.equalsIgnoreCase("join")) {
                joinGame();
            }
        }
        System.out.println("Starting game...");
    }

    private static void createGame() throws InterruptedException, IOException{
        server.put("create", playerIp, playerPort, "");
        Object[] response = messages.get(new FormalField(String.class));
        String gameId = response[0].toString();
        System.out.println("Game id: " + gameId);
        server.put(gameId, "ok");
        response = messages.get(new FormalField(String.class), new FormalField(String.class));
        opponentIp = response[0].toString();
        opponentPort = response[1].toString();
        assignPlayerWithNumber(1);
        gameNotFound = false;
    }

    private static void joinGame() throws InterruptedException, IOException {
        Scanner scan = new Scanner(System.in);
        while (gameNotFound) {
            System.out.println("Enter game id:");
            String input = scan.nextLine().trim();
            server.put("join", playerIp, playerPort, input);
            Object[] response = messages.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
            if (response[0].toString().equals("ok")) {
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
        System.out.println("Game over");
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
        System.out.println("You are about to place the following ship");
        view.preview(ship.getPreviewPoints(BOARD_SIZE));
        doRotations(ship);
        do{
            Point p = getInputPoint();
            ship.setCenter(p);
        }while(!model.isValidShipPlacement(ship,myBoard));
        model.placeShip(ship,id,myBoard);
        System.out.println("Successfully placed ship!");
    }

    public static void doRotations(Ship ship){
        Scanner scan = new Scanner(System.in);
        while(true){
            System.out.println("Do you want to rotate the ship 90 degrees clockwise?\nType rotate to rotate, type ok when done");
            String r = scan.next();
            if(r.equalsIgnoreCase("ok")) break;
            else if (r.equalsIgnoreCase("rotate")){
                ship.rotate();
                System.out.println("Ship successfully rotated");
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

    public static void waitForTurn() throws InterruptedException {
        System.out.println("Waiting for opponent...");

        //Timeout Impelmentation starts

        int timeout = 60;//how many seconds for a timout
        boolean breakout = false;

        while(timeout > 0){
            if(myBoard.queryp(new ActualField("token")) != null){
                System.out.println("breakout");
                breakout = true;
                break;
            }
            System.out.println(timeout);
            timeout--;
            TimeUnit.SECONDS.sleep(1);
        }

        if(!breakout){
            System.out.println("timeout");
            //run graceful quit
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

    public static void getShotTarget() throws InterruptedException {
        // Input scanner
        Scanner scan = new Scanner(System.in);

        while (true) {
            // Read input
            try {
                System.out.println("Enter coordinates of target square");
                targetX = scan.nextInt();
                targetY = scan.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input");
                continue;
            }

            // Check input is in valid range
            if (!model.isInsideBoard(targetX, targetY)) {
                continue;
            }

            // Check square hasn't already been hit
            if (!model.hasShotAt(targetX, targetY, opponentBoard)) {
                break;
            }
        }
    }

    public static void shootAtTarget() throws InterruptedException {
        System.out.println("Shooting");
        int shipId = model.shootAt(targetX, targetY, opponentBoard);
        if (shipId == 0) {
            System.out.println("Miss");
            view.markMissEnemy(targetX, targetY);
        } else {
            System.out.println("Hit");
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
            System.out.println("Select one of the following options:" + Arrays.toString(options));
            input = scanner.nextLine();
            input.trim().toLowerCase();
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
        System.out.println("Waiting for the other player...");

        if(player == host){
            Boolean p1 = playAgain(myBoard);
            Boolean p2 = playAgain(opponentBoard);
            if (p1 && p2 ){
                opponentBoard.put("host","again");
                System.out.println("starting new game...");
                newGameCycle();
            }else{
                opponentBoard.put("host","quit");
                System.out.println("quitting...");
                //TODO: GRACEFULLY QUIT
            }
        }
        else{
            Object[] obj = myBoard.get(new ActualField("host"),new FormalField(String.class));
            Boolean isAgain = obj[1].toString().equalsIgnoreCase("again");
            if(isAgain){
                System.out.println("starting new game...");
                newGameCycle();
            }
            else{
                System.out.println("quitting...");
                //TODO: GRACEFULLY QUIT
            }
        }
    }










}

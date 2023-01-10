package org.battleships;

import org.jspace.*;

import java.awt.*;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Scanner;


public class GameController {

    static int BOARD_SIZE = 5;
    static int NUMBER_OF_SHIPS = 3;

    // Variable that store whether you are player 1 or 2
    static int player;
    static int opponent;
    static int winner;
    static int targetX;
    static int targetY;
    static boolean firstMove = true;
    static boolean gameNotFound = true;

    static String serverIp = "10.209.126.122";
    static String playerIp = "10.209.126.122";
    static String opponentIp = "";
    static String serverPort = "1000";
    static String playerPort;
    static String opponentPort;
    static String playerId;

    static RemoteSpace lobby;
    static RemoteSpace opponentBoard;
    static Space myBoard;
    static SpaceRepository repository;

    static GameView view = new GameView(BOARD_SIZE, BOARD_SIZE);
    static GameModel model = new GameModel(BOARD_SIZE, BOARD_SIZE);
    static Ship[] ships = new Ship[NUMBER_OF_SHIPS];

    public static void main(String[] args) {
        try {
            findGame();
            createShips();
            placeShips();

            if (player == 2) opponentBoard.put("token");
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

        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void findGame() throws IOException, InterruptedException {

        playerId = generatePlayerId();
        playerPort = generatePort();
        lobby = new RemoteSpace("tcp://" + serverIp + ":" + serverPort + "/lobby?keep");

        // Create space for own board
        myBoard = new PileSpace();
        repository = new SpaceRepository();
        repository.add(playerId, myBoard);
        repository.addGate("tcp://" + playerIp + ":" + playerPort + "/?keep");

        Scanner scan = new Scanner(System.in);
        while (gameNotFound) {
            System.out.println("Type create or join");
            String choice = scan.nextLine().trim();
            if (choice.equalsIgnoreCase("create")) {
                createGame();
            } else if (choice.equalsIgnoreCase("join")) {
                joinGame();
            }
        }
        System.out.println("Game start");
    }

    private static void joinGame() throws InterruptedException, IOException {
        Scanner scan = new Scanner(System.in);
        while (gameNotFound) {
            System.out.println("Enter id:"); // Consider adding option to go back to select join or create
            String input = scan.nextLine().trim();

            Object[] tuple = lobby.getp(new ActualField(input), new FormalField(String.class), new FormalField(String.class));
            if (tuple != null) {
                String opponentId = tuple[0].toString();
                opponentIp = tuple[1].toString();
                opponentPort = tuple[2].toString();
                opponentBoard = new RemoteSpace("tcp://" + opponentIp + ":" + opponentPort + "/" + opponentId + "?keep");
                opponentBoard.put(playerId, playerIp, playerPort);
                assignPlayerNumber(2);
                gameNotFound = false;
            } else {
                System.out.println("Could not find player with id: " + input);
            }
        }
    }

    private static void createGame() throws InterruptedException, IOException {
        System.out.println("Your player id: " + playerId);
        lobby.put(playerId, playerIp, playerPort);

        // Wait for other player to connect
        Object[] tuple = myBoard.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
        String opponentId = tuple[0].toString();
        opponentIp = tuple[1].toString();
        opponentPort = tuple[2].toString();

        opponentBoard = new RemoteSpace("tcp://" + opponentIp + ":" + opponentPort + "/" + opponentId + "?keep");

        // Start game
        assignPlayerNumber(1);
        System.out.println("assigned player number: " + player);
        gameNotFound = false;
    }

    private static String generatePlayerId() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (true) {
            String id = "";
            for (int i = 0; i < 5; i++) {
                int index = (int) (alphabet.length() * Math.random());
                id = id + alphabet.charAt(index);
            }
            return id;
        }
    }

    private static String generatePort() {
        String alphabet = "0123456789";
        while (true) {
            String port = "";
            for (int i = 0; i < 4; i++) {
                int index = (int) (alphabet.length() * Math.random());
                port = port + alphabet.charAt(index);
            }
            return port;
        }
    }

    private static void assignPlayerNumber(int number) {
        player = number;
        opponent = player == 1 ? 2 : 1;
    }

    public static void endGame(){
        System.out.println("Game over");
        System.out.println(winner == player ? "You won!" : "You lost.");
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
}

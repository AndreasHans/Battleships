package org.battleships;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.awt.*;
import java.util.ArrayList;
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

    static String boardA = "tcp://82.211.207.77:1000/boardA?keep";
    static String boardB = "tcp://82.211.207.77:1000/boardB?keep";

    static RemoteSpace myBoard;
    static RemoteSpace opponentBoard;

    static GameView view = new GameView(BOARD_SIZE, BOARD_SIZE);
    static GameModel model = new GameModel(BOARD_SIZE, BOARD_SIZE);



    public static void main(String[] args) {
        try {
            getPlayerNumber();
            placeShips();

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

    public static void endGame(){
        System.out.println("Game over");
        System.out.println(winner == player ? "You won!" : "You lost.");
    }

    public static void endTurn() throws InterruptedException {
        opponentBoard.put("token");
    }


    public static Point getInputPoint(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Type coordinates");
        Point p = new Point();
        p.x = scan.nextInt();
        p.y = scan.nextInt();
        return p;
    }

    public static void placeShip(int id) throws InterruptedException {
        Point p;
        do{
            System.out.println("Give the starting point for a 1 x 3 ship");
            p = getInputPoint();
            model.tryGenerateNbyM(p.x, p.y, 1, 3, id, myBoard);
        }while(!model.containsShipWithId(id,myBoard));
        System.out.println("Successfully placed ship!");
    }

    public static void showShip(int id) throws InterruptedException {
        ArrayList<Point> points = model.getShipPointsById(id,myBoard);
        for(Point p: points){
            view.setShipYou(p.x,p.y);
        }
        view.updateBoard();
    }

    public static void placeShips() throws InterruptedException {
        for(int i = 1; i <= NUMBER_OF_SHIPS; i++){
            placeShip(i);
            showShip(i);
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




    private static void getPlayerNumber() {
        try {
            RemoteSpace space = new RemoteSpace(boardA);
            Object[] o = space.get(new FormalField(Integer.class));
            player = Integer.parseInt(o[0].toString());
            System.out.println("You are player " + player);
            if (player == 1) {
                opponent = 2;
                myBoard = new RemoteSpace(boardA);
                opponentBoard = new RemoteSpace(boardB);
            } else if (player == 2) {
                opponent = 1;
                myBoard = new RemoteSpace(boardB);
                opponentBoard = new RemoteSpace(boardA);
            }

        } catch (Exception e) {
            e.printStackTrace();
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

package org.battleships;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

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
    static String boardA = "tcp://192.168.1.9:1000/boardA?keep";
    static String boardB = "tcp://192.168.1.9:1000/boardB?keep";

    static RemoteSpace myBoard;
    static RemoteSpace opponentBoard;

    static GameView view = new GameView(BOARD_SIZE, BOARD_SIZE);
    static GameModel model = new GameModel(BOARD_SIZE, BOARD_SIZE);


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
        view.BoardBuilder();
    }

    public static void placeShips() throws InterruptedException {
        for(int i = 1; i <= NUMBER_OF_SHIPS; i++){
            placeShip(i);
            showShip(i);
        }
    }

    public static void main(String[] args) {
        try {

            // Input scanner
            Scanner scan = new Scanner(System.in);

            // Get player number
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
            } else {
                throw new Exception("Invalid player number");
            }

            placeShips();

            // Game loop
            while (true) {

                // Wait for turn
                System.out.println("Waiting for turn...");
                myBoard.get(new ActualField("token"));
                System.out.println("Your turn");

                // Update board from opponents last move
                Object[] opponentsMove = myBoard.queryp(new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class));
                if (!firstMove || player == 2) {
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
                view.BoardBuilder();

                // Check if player won

                if (!model.containsAnyShip(1, NUMBER_OF_SHIPS, myBoard)) {
                    winner = opponent;
                    break;
                }

                // Get target square
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

                // Fire at square
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

                // Display updated board
                view.BoardBuilder();

                opponentBoard.put("token");

                // Check if player won
                if (!model.containsAnyShip(1, NUMBER_OF_SHIPS, opponentBoard)) {
                    winner = player;
                    break;
                }

                // End turn and wait for opponent
                System.out.println("Opponents turn");

            }
            System.out.println("Game over");
            System.out.println(winner == player ? "You won!" : "You lost.");

        } catch (Exception e) { e.printStackTrace(); }


    }
}

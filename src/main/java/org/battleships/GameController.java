package org.battleships;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.Scanner;



public class GameController {

    static int BOARD_SIZE = 5;

    // Variable that store whether you are player 1 or 2
    static int player;

    static String boardA = "tcp://82.211.207.77:1000/boardA?keep";
    static String boardB = "tcp://82.211.207.77:1000/boardB?keep";

    static RemoteSpace myBoard;
    static RemoteSpace opponentBoard;

    static GameView view = new GameView(BOARD_SIZE, BOARD_SIZE);
    static GameModel model = new GameModel(BOARD_SIZE, BOARD_SIZE);

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
                myBoard = new RemoteSpace(boardA);
                opponentBoard = new RemoteSpace(boardB);
            } else if (player == 2) {
                myBoard = new RemoteSpace(boardB);
                opponentBoard = new RemoteSpace(boardA);
            } else {
                throw new Exception("Invalid player number");
            }

            // TODO: Setup board by placing ships

            // Game loop
            while (true) {

                // Wait for turn
                System.out.println("Waiting for turn...");
                myBoard.get(new ActualField("token"));
                System.out.println("Your turn");

                // Display updated board
                view.BoardBuilder();

                // TODO: Check if opponent won

                // Get target square
                int targetX = 0, targetY = 0;
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
                    if (!(targetX >= 0 && targetX < BOARD_SIZE && targetY >= 0 && targetY < BOARD_SIZE)) {
                        continue;
                    }

                    // Check square hasn't already been hit
                    if (!model.hasShotAt(targetX, targetY, opponentBoard)) {
                        break;
                    }
                }

                // TODO: Fire at square
                System.out.println("Shooting");
                model.shootAt(targetX, targetY, opponentBoard);

                // Display updated board
                view.BoardBuilder();

                // TODO: Check if player won

                // End turn and wait for opponent
                System.out.println("Opponents turn");
                opponentBoard.put("token");
            }

        } catch (Exception e) { e.printStackTrace(); }


    }
}

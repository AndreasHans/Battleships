package org.battleships;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.Scanner;



public class Controller {

    public static int BOARD_SIZE = 5;

    public static void main(String[] args) {
        try {

            // Input scanner
            Scanner scan = new Scanner(System.in);

            // Set the URI of remote tuple spaces
            String myURI = "tcp://0.0.0.0:0000/board1?keep";
            String otherURI = "tcp://0.0.0.0:0000/board2?keep";

            // Connect to remote spaces
            RemoteSpace myBoard = new RemoteSpace(myURI);
            RemoteSpace otherBoard = new RemoteSpace(otherURI);

            // Setup board

            // Game loop
            while (true) {

                // Get target square
                int targetX = 0, targetY = 0;
                while (true) {

                    // Read input
                    try {
                        targetX = scan.nextInt();
                        targetY = scan.nextInt();
                    } catch (Exception e) {
                        // Input format is invalid
                        continue;
                    }

                    // Check input is in valid range
                    if (!(targetX >= 0 && targetX < BOARD_SIZE && targetY >= 0 && targetY < BOARD_SIZE)) {
                        continue;
                    }

                    // Check square hasn't already been hit
                    Object[] tuple = otherBoard.queryp(new ActualField(targetX), new ActualField(targetY), new FormalField(Integer.class));
                    if (tuple == null || Integer.parseInt(tuple[2].toString()) > 0) {
                        break;
                    }
                }

                // Fire at square


                // Update UI
                // GameView.updateGraphics;

                // Check win
                

                // End turn and wait for opponent
                otherBoard.put("token");
                myBoard.get(new ActualField("token"));

                // Update UI
                // GameView.updateGraphics;

                // Check win

            }

        } catch (Exception e) { e.printStackTrace(); }


    }
}

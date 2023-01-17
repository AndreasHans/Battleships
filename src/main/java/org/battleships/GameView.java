package org.battleships;

import java.awt.*;
import java.util.ArrayList;

public class GameView {
    public int n;//how wide the board is, or in other words, how many x positions
    public int m;//how tall the board is, or in other words, how many y positions
    private char[][] boardEnemy;
    private char[][] boardYou;
    private char[][] boardPreview;
    private final String fieldBottom = "___|";
    private final String fieldTop = "   |";
    private final String fieldLeft = " ";
    private final String fieldRight = " |";
    private final String spacing = "      |";
    private final String tagLineSpacing = "       You ";
    private final String tagLineField = "    ";
    private StringBuilder bottomFieldLine = new StringBuilder();
    private StringBuilder topFieldLine = new StringBuilder();
    private StringBuilder topLine = new StringBuilder("   |");
    private StringBuilder EnemyTagLine = new StringBuilder("Enemy   ");
    private StringBuilder PreviewTagLine = new StringBuilder("Preview ");

/*
USER MANUAL: first, create a gameView object with an n times m board
    GameView = new GameView(n,m);
Then, use the following to change the board
    gameView.setShipYou(x,y);
    gameView.markMissEnemy(x,y);
    gameView.markHitEnemy(x,y);
    gameView.markHitYou(x,y);
    gameView.markMissYou(x,y);
with you being the right board, where your ships are located, and enemy being the left, where you shoot
lastly, print the board using
    gameView.BoardBuilder();

    preview requires only step 1, and to call preview with an arraylist of points you want previewed.
    */


    public GameView(int x, int y){
        n = x;
        m = y;
        boardEnemy = new char[n][m];
        boardYou = new char[n][m];
        boardPreview = new char[n][m];

        //fills the board with empty spaces
        setUpEnemy();
        setUpYou();
        setUpPreview();
        //readies the top field line
        topFieldBuilder();
        topFieldLine.append(spacing);
        topFieldBuilder();
        //readies the bottom field line
        bottomFieldBuilder();
        bottomFieldLine.append(spacing);
        bottomFieldBuilder();
        //readies the top line
        topLineBuilder();
        //readies the tagline
        tagLineBuilder();

    }



    public void updateBoard(){
        System.out.println();
        System.out.println(EnemyTagLine);
        System.out.println(topLine);
        for(int i = 0; i < m; i++){//prints most of the board, each cycle printing the bottom of the field before, the top of this field, and the middle of this field
            System.out.println(bottomFieldLine);
            System.out.println(topFieldLine);

            //build the middle of the field start
            StringBuilder currentLine = new StringBuilder(fieldLeft);
            currentLine.append(i).append(fieldRight);
            for(int j = 0; j < n; j++){//inputs the right chars in enemy board
                currentLine.append(fieldLeft).append(boardEnemy[j][i]).append(fieldRight);
            }
            currentLine.append(spacing).append(fieldLeft).append(i).append(fieldRight);
            for(int j= 0; j < n; j++){
                currentLine.append(fieldLeft).append(boardYou[j][i]).append(fieldRight);
            }//build the middle of the field end

            System.out.println(currentLine);
        }
        System.out.println(bottomFieldLine);
    }

    public void preview(ArrayList<Point> coordinates){
        setUpPreview();
        boardPreview[coordinates.get(0).x][coordinates.get(0).y] = 'H';
        for(int i = 1; i < coordinates.size(); i++){
            boardPreview[coordinates.get(i).x][coordinates.get(i).y] = 'x';
        }

        System.out.println();
        System.out.println(PreviewTagLine);
        System.out.println(topLine);
        for(int i = 0; i < m; i++){//prints most of the board, each cycle printing the bottom of the field before, the top of this field, and the middle of this field
            System.out.println(bottomFieldLine);
            System.out.println(topFieldLine);

            //build the middle of the field start
            StringBuilder currentLine = new StringBuilder(fieldLeft);
            currentLine.append(i).append(fieldRight);
            for(int j = 0; j < n; j++){//inputs the right chars in enemy board
                currentLine.append(fieldLeft).append(boardPreview[j][i]).append(fieldRight);
            }
            currentLine.append(spacing).append(fieldLeft).append(i).append(fieldRight);
            for(int j= 0; j < n; j++){
                currentLine.append(fieldLeft).append(boardYou[j][i]).append(fieldRight);
            }//build the middle of the field end

            System.out.println(currentLine);
        }
        System.out.println(bottomFieldLine);
    }

    public void setShipYou(int x, int y){//for placing ships
        boardYou[x][y] = 'X';
    }

    public  void markMissYou(int x, int y){//for when they miss you
        boardYou[x][y] = 'O';
    }

    public void markHitYou(int x, int y){//for when they hit you
        boardYou[x][y] = '%';
    }

    public void markMissEnemy(int x, int y){//for when you miss
        boardEnemy[x][y] = 'O';
    }

    public void markHitEnemy(int x, int y){//for when you hit
        boardEnemy[x][y] = 'X';
    }


    private void topLineBuilder(){//builds the line of the board that has the x coordinates, and prints it.
        for(int i = 0; i < n;i++){
            topLine.append(fieldLeft).append(i).append(fieldRight);
        }
        topLine.append("      |   |");
        for(int i = 0; i < n;i++){
            topLine.append(fieldLeft).append(i).append(fieldRight);
        }
    }

    private void topFieldBuilder(){//builds the topmost line of a field, the one that looks a bit like this:    |   |   |   |
        for(int i = 0; i < n+1; i++){//prints the rest of the array
            topFieldLine.append(fieldTop);
        }
    }

    private void bottomFieldBuilder(){//builds the bottom most line of a field, the one that looks a bit like this: ___|___|___|___|
        for(int i = 0; i < n+1; i++){//prints the rest of the array
            bottomFieldLine.append(fieldBottom);
        }
    }

    private void tagLineBuilder() {
        for(int i = 0; i < n-1; i++){
            PreviewTagLine.append(tagLineField);
            EnemyTagLine.append(tagLineField);
        }
        PreviewTagLine.append(tagLineSpacing);
        EnemyTagLine.append(tagLineSpacing);
        for(int i = 0; i < n; i++){
            PreviewTagLine.append(tagLineField);
            EnemyTagLine.append(tagLineField);
        }
    }



    private void setUpEnemy(){//ensure the enemy board is empty
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                boardEnemy[i][j] = ' ';
            }
        }
    }

    private void setUpYou(){//ensures your board is empty
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                boardYou[i][j] = ' ';
            }
        }
    }

    private void setUpPreview() {
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                boardPreview[i][j] = ' ';
            }
        }
    }
}

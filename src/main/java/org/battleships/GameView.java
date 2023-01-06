package org.battleships;

public class GameView {
    public int n = 5;//how wide the board is, or in other words, how many x positions
    public int m = 5;//how tall the board is, or in other words, how many y positions
    private char boardEnemy[][] = new char[n][m];
    private char boardYou[][] = new char[n][m];

    private String fieldBottom = "___|";
    private String fieldTop = "   |";
    private final String fieldLeft = " ";
    private final String fieldRight = " |";
    private final String spacing = "      |";
    private StringBuilder bottomFieldLine = new StringBuilder("");
    private StringBuilder topFieldLine = new StringBuilder("");

    public void setUp(){
        //fills the board with empty spaces
        setUpEnemy();
        setUpYou();
        //readies the top field line
        topFieldBuilder();
        topFieldLine.append(spacing);
        topFieldBuilder();
        //readies the bottom field line
        bottomFieldBuilder();
        bottomFieldLine.append(spacing);
        bottomFieldBuilder();



        preset();//must be deleted before game starts - for testing. Should also stay in the bottom of setup
    }


    public GameView(){
        topLine();//prints the top most line


        for(int i = 0; i < m; i++){//prints most of the board, each cycle printing the bottom of the field before, the top of this field, and the middle of this field
            System.out.println(bottomFieldLine);
            System.out.println(topFieldLine);

            //build the middle of the field start
            StringBuilder currentLine = new StringBuilder(fieldLeft);
            currentLine.append(m).append(fieldRight);
            for(int j = 0; j < n; j++){//intputs the right chars in enemy board
                currentLine.append(fieldLeft).append(boardEnemy[j][i]).append(fieldRight);
            }
            currentLine.append(spacing).append(fieldLeft).append(m).append(fieldRight);
            for(int j= 0; j < n; j++){
                currentLine.append(fieldLeft).append(boardYou[j][i]).append(fieldRight);
            }
            //build the middle of the field end

            System.out.println(currentLine);

        }


        System.out.println(bottomFieldLine);


    }

    public void setBoardYou(int x, int y){//for placing ships
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


    private void topLine(){//builds the line of the board that has the x coordinates, and prints it.
        StringBuilder topLine = new StringBuilder("   |");

        for(int i = 0; i < n;i++){
            topLine.append(fieldLeft).append(i).append(fieldRight);
        }
        topLine.append("      |   |");
        for(int i = 0; i < n;i++){
            topLine.append(fieldLeft).append(i).append(fieldRight);
        }
        System.out.println(topLine);
        //TODO: refactor this so it just builds the line. should save a bit of work
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

    private void preset(){//for testing purposes
        setBoardYou(0,2);
        setBoardYou(0,3);
        setBoardYou(0,4);
        setBoardYou(1,4);
        setBoardYou(3,3);
    }

}

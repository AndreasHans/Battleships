package org.battleships;

import org.jspace.SequentialSpace;
import org.jspace.Space;

public class main {

    public static void main(String[] argv) throws InterruptedException {
        Space ships = new SequentialSpace();
        GameModel gameModel = new GameModel(5,5);



    }
}

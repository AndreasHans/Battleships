package org.battleships;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import java.util.Arrays;
import java.util.List;

public class main {

    public static void main(String[] argv) throws InterruptedException {
        Space ships = new SequentialSpace();
        GameActions gameActions = new GameActions(5,5);

        GameView gameView = new GameView(6,6);
        gameView.preset();
        gameView.setShipYou(0,5);
        gameView.markMissEnemy(2,2);
        gameView.markHitEnemy(1,1);
        gameView.markHitYou(0,3);
        gameView.markMissYou(4,2);
        gameView.BoardBuilder();






    }
}

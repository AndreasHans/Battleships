package org.battleships.tests;

import org.battleships.GameActions;
import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestGameActions {

    private Space space;
    private GameActions gameActions;

    public TestGameActions(){
        this.space = new SequentialSpace();
        this.gameActions = new GameActions(5,5);
    }

    @Test //Test valid 1 x 1 ship located on (0,0) in 5 x 5 grid
    public void test1x1num1() throws InterruptedException {
        gameActions.tryGenerateNbyN(0,0,1,1,space);
        assertTrue(gameActions.containsShipWithId(1,space));
    }

    @Test //Test valid 1 x 1 ship located on (4,4) in 5 x 5 grid
    public void test1x1num2() throws InterruptedException {
        gameActions.tryGenerateNbyN(4,4,1,1,space);
        assertTrue(gameActions.containsShipWithId(1,space));
    }

    @Test //Test not valid 1 x 1 ship located on (5,5) in 5 x 5 grid
    public void test1x1num3() throws InterruptedException {
        gameActions.tryGenerateNbyN(5,5,1,1,space);
        assertTrue(!gameActions.containsShipWithId(1,space));
    }

    @Test //Test not valid 1 x 1 ship located on (-1,3) in 5 x 5 grid
    public void test1x1num4() throws InterruptedException {
        gameActions.tryGenerateNbyN(-1,3,1,1,space);
        assertTrue(!gameActions.containsShipWithId(1,space));
    }

    @Test
    public void test2x2num1() throws InterruptedException {
        gameActions.tryGenerateNbyN(1,1,2,1,space);
        assertTrue(gameActions.containsShipWithId(1,space));
    }

    @Test
    public void test2x2num2() throws InterruptedException {
        gameActions.tryGenerateNbyN(4,4,2,1,space);
        assertTrue(!gameActions.containsShipWithId(1,space));
    }

    @Test //Generates ship with id 0 and checks a ship with id 0 doesn't exist
    public void testGenerateAndRemove1() throws InterruptedException {
        gameActions.tryGenerateNbyN(0,0,1,1,space);
        gameActions.removeShipById(0,space);
        assertTrue(!gameActions.containsShipWithId(0,space));
    }

    @Test //Generates ship with id 0 and checks a ship with id 0 doesn't exist
    public void testGenerateAndRemove2() throws InterruptedException {
        gameActions.tryGenerateNbyN(0,0,1,1,space);
        gameActions.tryGenerateNbyN(3,3,1,2,space);
        gameActions.removeShipById(0,space);
        assertTrue(!gameActions.containsShipWithId(0,space) && gameActions.containsShipWithId(1,space));
    }

    @Test //Generates ship with L shape at (0,0)
    public void testGenerateL1() throws InterruptedException {
        gameActions.tryGenerateL(0,0,1,space);
        assertTrue(gameActions.containsShipWithId(1,space));
    }

    @Test //Generates ship with L shape at (1,2)
    public void testGenerateL2() throws InterruptedException {
        gameActions.tryGenerateL(1,2,1,space);
        assertTrue(gameActions.containsShipWithId(1,space));
    }

    @Test //Generates ship with L shape at (4,0) and fails
    public void testGenerateL3() throws InterruptedException {
        gameActions.tryGenerateL(4,0,1,space);
        assertTrue(!gameActions.containsShipWithId(1,space));
    }

}

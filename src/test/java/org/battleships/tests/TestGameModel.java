package org.battleships.tests;

import org.battleships.GameModel;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestGameModel {

    private Space space;
    private GameModel gameModel;
    private FormalField INT = new FormalField(Integer.class);

    public TestGameModel(){
        this.space = new SequentialSpace();
        this.gameModel = new GameModel(5,5);
    }

    @Test //Test valid 1 x 1 ship located on (0,0) in 5 x 5 grid
    public void test1x1num1() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,1,1,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //Test valid 1 x 1 ship located on (4,4) in 5 x 5 grid
    public void test1x1num2() throws InterruptedException {
        gameModel.tryGenerateNbyM(4,4,1,1,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //Test not valid 1 x 1 ship located on (5,5) in 5 x 5 grid
    public void test1x1num3() throws InterruptedException {
        gameModel.tryGenerateNbyM(5,5,1,1,1,space);
        assertTrue(!gameModel.containsShipWithId(1,space));
    }

    @Test //Test not valid 1 x 1 ship located on (-1,3) in 5 x 5 grid
    public void test1x1num4() throws InterruptedException {
        gameModel.tryGenerateNbyM(-1,3,1,1,1,space);
        assertTrue(!gameModel.containsShipWithId(1,space));
    }

    @Test
    public void test2x2num1() throws InterruptedException {
        gameModel.tryGenerateNbyM(1,1,2,2,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test
    public void test2x2num2() throws InterruptedException {
        gameModel.tryGenerateNbyM(4,4,2,2,1,space);
        assertTrue(!gameModel.containsShipWithId(1,space));
    }

    @Test
    public void testMultipleShips1() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,2,2,1,space);
        gameModel.tryGenerateNbyM(4,4,1,1,2,space);
        boolean s = gameModel.containsShipWithId(1,space) && gameModel.containsShipWithId(2,space);
        assertTrue(s);
    }

    @Test
    public void testMultipleShips2() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,2,2,1,space);
        gameModel.tryGenerateNbyM(1,1,3,1,2,space);
        boolean s = gameModel.containsShipWithId(1,space) && !gameModel.containsShipWithId(2,space);
        assertTrue(s);
    }

    @Test
    public void testMultipleShips3() throws InterruptedException {
        gameModel.tryGenerateNbyM(-1,0,2,2,1,space);
        gameModel.tryGenerateNbyM(1,1,3,1,2,space);
        boolean s = !gameModel.containsShipWithId(1,space) && gameModel.containsShipWithId(2,space);
        assertTrue(s);
    }

    @Test //Generates ship with id 0 and checks a ship with id 0 doesn't exist
    public void testGenerateAndRemove1() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,1,1,1,space);
        gameModel.removeShipById(0,space);
        assertTrue(!gameModel.containsShipWithId(0,space));
    }

    @Test //Generates ship with id 0 and checks a ship with id 0 doesn't exist
    public void testGenerateAndRemove2() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,1,1,1,space);
        gameModel.tryGenerateNbyM(3,3,1,1,2,space);
        gameModel.removeShipById(0,space);
        assertTrue(!gameModel.containsShipWithId(0,space) && gameModel.containsShipWithId(1,space));
    }

    @Test //Generates ship with L shape at (0,0)
    public void testGenerateL1() throws InterruptedException {
        gameModel.tryGenerateL(0,0,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //Generates ship with L shape at (1,2)
    public void testGenerateL2() throws InterruptedException {
        gameModel.tryGenerateL(1,2,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //Generates ship with L shape at (4,0) and fails
    public void testGenerateL3() throws InterruptedException {
        gameModel.tryGenerateL(4,0,1,space);
        assertTrue(!gameModel.containsShipWithId(1,space));
    }

    @Test //shoot at water
    public void testShoot1() throws InterruptedException {
        gameModel.tryShootAt(0,0,space);
        assertTrue(gameModel.hasShotAt(0,0,space));
    }

    @Test //check you havent shot when you havent shot
    public void testShoot2() throws InterruptedException {
        assertTrue(!gameModel.hasShotAt(0,0,space));
    }

    @Test //shoot at a 1x1 ship
    public void testShoot3() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,1,1,1,space);
        gameModel.tryShootAt(0,0,space);
        assertTrue(!gameModel.containsShipWithId(1,space));
    }

    @Test //miss a shot at a 1x1 ship
    public void testShoot4() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,1,1,1,space);
        gameModel.tryShootAt(1,0,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //shoot at 2x2 ship
    public void testShoot5() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,2,2,1,space);
        gameModel.tryShootAt(0,0,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //shoot 4 shots at a 2x2 ship
    public void testShoot6() throws InterruptedException {
        gameModel.tryGenerateNbyM(0,0,2,2,1,space);
        gameModel.tryShootAt(0,0,space);
        gameModel.tryShootAt(1,0,space);
        gameModel.tryShootAt(0,1,space);
        gameModel.tryShootAt(1,1,space);
        assertTrue(!gameModel.containsShipWithId(1,space));
    }

    @Test //shoot same place multiple times
    public void testShoot7() throws InterruptedException {
        gameModel.tryShootAt(0,0,space);
        gameModel.tryShootAt(0,0,space);
        gameModel.tryShootAt(0,0,space);
        gameModel.tryShootAt(0,0,space);
        List<Object[]> ls = space.queryAll(new ActualField(0),new ActualField(0),INT);
        assertTrue(ls.size() == 1);
    }
}

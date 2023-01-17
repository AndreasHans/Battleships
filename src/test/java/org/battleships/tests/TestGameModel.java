package org.battleships.tests;
import org.battleships.GameModel;
import org.battleships.Ship;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestGameModel {

    private final Space space;
    private final GameModel gameModel;

    public TestGameModel(){
        this.space = new SequentialSpace();
        this.gameModel = new GameModel(5,5);
    }

    @Test //Test valid 1 x 1 ship located on (0,0) in 5 x 5 grid
    public void test1x1num1() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //Test valid 1 x 1 ship located on (4,4) in 5 x 5 grid
    public void test1x1num2() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(4,4));
        gameModel.placeShip(ship,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //Test not valid 1 x 1 ship located on (5,5) in 5 x 5 grid
    public void test1x1num3() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(5,5));
        assertFalse(gameModel.isValidShipPlacement(ship,space));
    }

    @Test //Test not valid 1 x 1 ship located on (-1,3) in 5 x 5 grid
    public void test1x1num4() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(-1,3));
        gameModel.placeShip(ship,1,space);
        assertFalse(gameModel.isValidShipPlacement(ship,space));
    }

    @Test
    public void test2x2num1() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(2,2));
        ship.setCenter(new Point(1,1));
        gameModel.placeShip(ship,1,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test
    public void test2x2num2() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(2,2));
        ship.setCenter(new Point(4,4));
        gameModel.placeShip(ship,1,space);
        assertFalse(gameModel.isValidShipPlacement(ship,space));
    }

    @Test
    public void testMultipleShips1() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(2,2));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);

        Ship ship2 = new Ship();
        ship2.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship2.setCenter(new Point(4,4));
        gameModel.placeShip(ship2,2,space);

        boolean s = gameModel.containsShipWithId(1,space) && gameModel.containsShipWithId(2,space);
        assertTrue(s);
    }

    @Test
    public void testMultipleShips2() throws InterruptedException {

        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(2,2));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);

        Ship ship2 = new Ship();
        ship2.setTemplatePoints(gameModel.makeNbyM(3,1));
        ship2.setCenter(new Point(1,1));

        boolean s = gameModel.containsShipWithId(1,space) && !gameModel.isValidShipPlacement(ship2,space);
        assertTrue(s);
    }

    @Test
    public void testMultipleShips3() throws InterruptedException {

        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(2,2));
        ship.setCenter(new Point(-1,0));

        Ship ship2 = new Ship();
        ship2.setTemplatePoints(gameModel.makeNbyM(3,1));
        ship2.setCenter(new Point(1,1));
        gameModel.placeShip(ship2,2,space);

        boolean s = !gameModel.isValidShipPlacement(ship,space) && gameModel.containsShipWithId(2,space);
        assertTrue(s);
    }

    @Test //shoot at water
    public void testShoot1() throws InterruptedException {
        gameModel.shootAt(0,0,space);
        assertTrue(gameModel.hasShotAt(0,0,space));
    }

    @Test //check you haven't shot when you haven't shot
    public void testShoot2() throws InterruptedException {
        assertFalse(gameModel.hasShotAt(0,0,space));
    }

    @Test //shoot at a 1x1 ship
    public void testShoot3() throws InterruptedException {

        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        gameModel.shootAt(0,0,space);
        assertFalse(gameModel.containsShipWithId(1,space));
    }

    @Test //miss a shot at a 1x1 ship
    public void testShoot4() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        gameModel.shootAt(1,0,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //shoot at 2x2 ship
    public void testShoot5() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(2,2));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        gameModel.shootAt(0,0,space);
        assertTrue(gameModel.containsShipWithId(1,space));
    }

    @Test //shoot 4 shots at a 2x2 ship
    public void testShoot6() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(2,2));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        gameModel.shootAt(0,0,space);
        gameModel.shootAt(1,0,space);
        gameModel.shootAt(0,1,space);
        gameModel.shootAt(1,1,space);
        assertFalse(gameModel.containsShipWithId(1,space));
    }

    @Test //shoot at 1x1 ship
    public void testShoot7() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        int id = gameModel.shootAt(0,0,space);
        assertEquals(id,1);
    }

    @Test //shoot at ship
    public void testShoot8() throws InterruptedException {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        gameModel.shootAt(0,0,space);
        assertTrue(gameModel.hasShotAt(0,0,space));
    }

    @Test //no ships left 1
    public void testNoShips1() throws InterruptedException {
        assertFalse(gameModel.containsAnyShip(1,5,space));
    }

    @Test //no ships left 2
    public void testNoShips2() throws InterruptedException {
        Ship ship = new Ship();

        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.setCenter(new Point(0,0));
        gameModel.placeShip(ship,1,space);
        gameModel.shootAt(0,0,space);
        assertFalse(gameModel.containsAnyShip(1,5,space));
    }

    @Test //rotate 1x1
    public void testRotate1() {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,1));
        ship.rotate();
        ArrayList<Point> points = ship.getTemplatePoints();
        Point p = points.get(0);
        assertTrue(p.x==0 && p.y == 0);
    }

    @Test //rotate 1x2 once
    public void testRotate2() {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,2));
        ship.rotate();
        ArrayList<Point> points = ship.getTemplatePoints();
        Point p0 = points.get(0);
        Point p1 = points.get(1);
        assertTrue(p0.x == 0 && p0.y == 0 && p1.x == -1 && p1.y == 0);
    }

    @Test //rotate 1x2 twice
    public void testRotate3() {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,2));
        ship.rotate();
        ship.rotate();
        ArrayList<Point> points = ship.getTemplatePoints();
        Point p0 = points.get(0);
        Point p1 = points.get(1);
        assertTrue(p0.x == 0 && p0.y == 0 && p1.x == 0 && p1.y == -1);
    }

    @Test //rotate 1x2 three times
    public void testRotate4() {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,2));
        ship.rotate();
        ship.rotate();
        ship.rotate();
        ArrayList<Point> points = ship.getTemplatePoints();
        Point p0 = points.get(0);
        Point p1 = points.get(1);
        assertTrue(p0.x == 0 && p0.y == 0 && p1.x == 1 && p1.y == 0);
    }

    @Test //rotate 1x2 four times
    public void testRotate5() {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,2));
        ship.rotate();
        ship.rotate();
        ship.rotate();
        ship.rotate();
        ArrayList<Point> points = ship.getTemplatePoints();
        Point p0 = points.get(0);
        Point p1 = points.get(1);
        assertTrue(p0.x == 0 && p0.y == 0 && p1.x == 0 && p1.y == 1);
    }

    @Test //rotate 1x2 three times and place at (2,2)
    public void testRotateAndPlace1() {
        Ship ship = new Ship();
        ship.setTemplatePoints(gameModel.makeNbyM(1,2));
        ship.setCenter(new Point(2,2));
        ship.rotate();
        ship.rotate();
        ship.rotate();
        ArrayList<Point> points = ship.getActualPoints();
        Point p0 = points.get(0);
        Point p1 = points.get(1);
        assertTrue(p0.x == 2 && p0.y == 2 && p1.x == 3 && p1.y == 2);
    }
}

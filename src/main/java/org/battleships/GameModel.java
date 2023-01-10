package org.battleships;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import java.awt.Point;
import java.util.ArrayList;

public class GameModel {
    private final int HEIGHT,WIDTH;
    private final FormalField INT = new FormalField(Integer.class);

    public GameModel(int width, int height){
        this.HEIGHT = height;
        this.WIDTH = width;
    }

    //shoot at (x,y) in space and return the id of the ship you hit, id = 0 when no shit is hit
    public int shootAt(int x,int y, Space space) throws InterruptedException {
        Object[] obj = space.getp(new ActualField(x),new ActualField(y),INT);
        int id = 0;
        if (obj != null) id = Integer.parseInt(obj[2].toString());
        space.put(x,y,-id);
        return id;
    }

    //check is (x,y) is inside the board
    public boolean isInsideBoard(int x,int y){
        return !(x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT);
    }

    public boolean hasShotAt(int x, int y, Space space) throws InterruptedException {
        Object[] obj = space.queryp(new ActualField(x),new ActualField(y),INT);
        return obj != null && Integer.parseInt(obj[2].toString()) <= 0;
    }

    public ArrayList<Point> makeNbyM(int n, int m) {
        ArrayList<Point> points = new ArrayList<>();
        for(int x = 0; x < n; x++){
            for(int y = 0; y < m; y++){
                points.add(new Point(x,y));
            }
        }
        return points;
    }

    public ArrayList<Point> makeL(){

        ArrayList<Point> points = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            points.add(new Point(0,i));
        }
        for(int i = 1; i < 2; i++){
            points.add(new Point(i,2));
        }
        return points;
    }

    public boolean containsShipWithId(int id, Space space) throws InterruptedException {
        return space.queryp(INT,INT,new ActualField(id)) != null;
    }

    public boolean containsAnyShip(int minId, int maxId, Space space) throws InterruptedException {
        for(int i = minId; i <= maxId; i++){
            if (containsShipWithId(i,space)) return true;
        }
        return false;
    }

    public boolean isValidShipPlacement(Ship ship, Space space) throws InterruptedException {
        for(Point p: ship.getActualPoints()){
            if (!isValidFieldPlacement(p.x,p.y,space)) return false;
        }
        return true;
    }

    public void placeShip(Ship ship, int id, Space myBoard) throws InterruptedException {
        for(Point p: ship.getActualPoints()){
            myBoard.put(p.x,p.y,id);
        }
    }

    private boolean isEmptyField(int x, int y, Space space) throws InterruptedException {
        Object[] obj = space.queryp(new ActualField(x),new ActualField(y),INT);
        return obj == null;
    }

    private boolean isValidFieldPlacement(int x, int y, Space space) throws InterruptedException {
        return isEmptyField(x,y,space) && isInsideBoard(x,y);
    }

}


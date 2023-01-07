package org.battleships;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameModel {
    private int HEIGHT,WIDTH;
    FormalField INT = new FormalField(Integer.class);

    public GameModel(int width, int height){
        this.HEIGHT = height;
        this.WIDTH = width;
    };

    public void tryGenerateNbyN(int x0, int y0, int n, int id, Space space) throws InterruptedException {
        ArrayList<Point> points = new ArrayList<Point>();
        for(int x = x0; x < x0 + n; x++){
            for(int y = y0; y < y0 + n; y++){
                points.add(new Point(x,y));
            }
        }
        tryGenerateShip(points,id,space);
    }

    public void tryGenerateL(int x0, int y0, int id, Space space) throws InterruptedException {
        ArrayList<Point> points = new ArrayList<Point>();
        int h = 3;
        int b = 2;
        for(int i = 0; i < h; i++){
            points.add(new Point(x0,y0+i));
        }
        for(int i = 1; i < b; i++){
            points.add(new Point(x0+i,y0));
        }
        tryGenerateShip(points,id,space);
    }

    public void tryGenerateShip(ArrayList<Point> points, int id, Space space) throws InterruptedException {
        if(isValidShipPlacement(points,space)){
            generateShip(points,id,space);
        }
        else{
            //System.out.println("Not valid ship placement!");
        }
    }

    public void generateShip(ArrayList<Point> list, int id, Space space) throws InterruptedException {
        for(Point p: list){
            generateFieldAt(p.x,p.y,id,space);
        }
    }

    public void generateFieldAt(int x, int y, int id, Space space) throws InterruptedException {
        if (isValidField(x,y,space)){
            space.put(x,y,id);
        }
    }

    public void removeShipById(int id, Space space) throws InterruptedException {
        space.getAll(INT, INT, new ActualField(id));
    }

    public boolean containsShipWithId(int id, Space space) throws InterruptedException {
        return space.queryp(INT,INT,new ActualField(id)) != null;
    }

    public boolean isEmptyField(int x, int y, Space space) throws InterruptedException {
        Object[] obj = space.queryp(new ActualField(x),new ActualField(y),INT);
        return obj == null;
    }

    public boolean isInsideBoard(int x,int y){
        return !(x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT);
    }

    public boolean isValidField(int x,int y,Space space) throws InterruptedException {
        return isEmptyField(x,y,space) && isInsideBoard(x,y);
    }
    public boolean isValidShipPlacement(ArrayList<Point> points, Space space) throws InterruptedException {
        for(Point p: points){
            if (!isValidField(p.x,p.y,space)) return false;
        }
        return true;
    }

    public void printElemsInSpaceById(Space space, int id) throws InterruptedException {
        FormalField INT = new FormalField(Integer.class);
        List<Object[]> parts = space.queryAll(INT, INT, new ActualField(id));
        for(Object[] obj: parts){
            System.out.println(Arrays.toString(obj)); //DBG
        }
        System.out.println("End");
    }


}

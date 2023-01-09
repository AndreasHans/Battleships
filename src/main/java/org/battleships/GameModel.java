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
    private FormalField INT = new FormalField(Integer.class);

    public GameModel(int width, int height){
        this.HEIGHT = height;
        this.WIDTH = width;
    };
    //PUBLIC
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

    public void tryGenerateNbyM(int x0, int y0, int n, int m, int id, Space space) throws InterruptedException {
        ArrayList<Point> points = new ArrayList<>();
        int x1 = x0 + n;
        int y1 = y0 + m;

        for(int x = x0; x < x1; x++){
            for(int y = y0; y < y1; y++){
                points.add(new Point(x,y));
            }
        }
        tryGenerateShipFromList(points,id,space);
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
        tryGenerateShipFromList(points,id,space);
    }

    public void removeShipById(int id, Space space) throws InterruptedException {
        space.getAll(INT, INT, new ActualField(id));
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

    public Point[] getShipPointsById(int id, Space space) throws InterruptedException {
        ArrayList<Point> points = new ArrayList<>();
        List<Object[]> parts = space.queryAll(INT, INT, new ActualField(id));

        for(Object[] obj: parts){
            int x = Integer.parseInt(obj[0].toString());
            int y = Integer.parseInt(obj[1].toString());
            points.add(new Point(x,y));
        }
        return (Point[]) points.toArray();
    }

    public void printElemsInSpaceById(Space space, int id) throws InterruptedException {
        FormalField INT = new FormalField(Integer.class);
        List<Object[]> parts = space.queryAll(INT, INT, new ActualField(id));
        for(Object[] obj: parts){
            System.out.println(Arrays.toString(obj)); //DBG
        }
        System.out.println("End");
    }

    //PRIVATE
    private void tryGenerateShipFromList(ArrayList<Point> points, int id, Space space) throws InterruptedException {
        if(isValidShipPlacement(points,space)){
            generateShipFromList(points,id,space);
        }
        else{
            //System.out.println("Not valid ship placement!");
        }
    }

    private void generateShipFromList(ArrayList<Point> list, int id, Space space) throws InterruptedException {
        for(Point p: list){
            generateFieldAt(p.x,p.y,id,space);
        }
    }

    private void generateFieldAt(int x, int y, int id, Space space) throws InterruptedException {
        if (isValidFieldPlacement(x,y,space)){
            space.put(x,y,id);
        }
    }

    private boolean isEmptyField(int x, int y, Space space) throws InterruptedException {
        Object[] obj = space.queryp(new ActualField(x),new ActualField(y),INT);
        return obj == null;
    }

    private boolean isValidFieldPlacement(int x, int y, Space space) throws InterruptedException {
        return isEmptyField(x,y,space) && isInsideBoard(x,y);
    }

    private boolean isValidShipPlacement(ArrayList<Point> points, Space space) throws InterruptedException {
        for(Point p: points){
            if (!isValidFieldPlacement(p.x,p.y,space)) return false;
        }
        return true;
    }
}

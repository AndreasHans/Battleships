package org.battleships;
import java.awt.Point;
import java.util.ArrayList;

public class Ship {
    private ArrayList<Point> points;
    private Point c;

    public Ship(){
        this.c = new Point(0,0);
        this.points = new ArrayList<>();
    }

    public void setCenter(Point c2){
        int dx = c2.x - c.x;
        int dy = c2.y - c.y;
        for(Point p: this.points){
            p.x += dx;
            p.y += dy;
        }
        this.c = c2;
    }

    //does not fully work
    /*
    public void rotate(){
        for(Point p: this.points){
            int tmp = p.x;
            p.x = c.x+c.y-p.y;
            p.y = tmp+c.y-c.x;
        }
    }

     */

    public void setPoints(ArrayList<Point> points){
        this.points = points;
    }

    public ArrayList<Point> getPoints(){
        return this.points;
    }

}

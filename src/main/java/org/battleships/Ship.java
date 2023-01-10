package org.battleships;
import java.awt.Point;
import java.util.ArrayList;

public class Ship {
    private ArrayList<Point> points;
    private Point head;

    public Ship(){
        this.head = new Point(0,0);
        this.points = new ArrayList<>();
    }

    public void setCenter(Point newHead){
        this.head = newHead;
    }

    public void setTemplatePoints(ArrayList<Point> points){
        this.points = points;
    }

    public ArrayList<Point> getTemplatePoints(){
        return this.points;
    }

    public ArrayList<Point> getActualPoints(){
        ArrayList<Point> points = new ArrayList<>();
        for(Point p: this.points){
            points.add(new Point(p.x+ head.x,p.y+ head.y));
        }
        return points;
    }

    public ArrayList<Point> getPreviewPoints(int SIZE){
        Point h = this.head;
        this.head = new Point(SIZE/2,SIZE/2);
        ArrayList<Point> previewPoints = getActualPoints();
        this.head = h;
        return previewPoints;
    }

    public void rotate(){
        for(Point p: this.points){
            int tmp = p.x;
            p.x = -p.y;
            p.y = tmp;
        }
    }
}

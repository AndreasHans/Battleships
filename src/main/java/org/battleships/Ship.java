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
        this.c = c2;
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
            points.add(new Point(p.x+c.x,p.y+c.y));
        }
        return points;
    }

    public void rotate(){
        for(Point p: this.points){
            int tmp = p.x;
            p.x = -p.y;
            p.y = tmp;
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("a ship with template points");
        for(Point p: points){
            sb.append(" (").append(p.x).append(",").append(p.y).append(")");
        }
        return sb.toString();
    }

}

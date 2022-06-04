package sample;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.w3c.dom.css.Rect;

import java.awt.*;

public class Obstacle {

    Pane pane;
    Rectangle rect;
    double angle;
    double centerX;
    double centerY;
    Point a;
    Point b;
    Point c;
    Point d;
    Edge ab;
    Edge bc;
    Edge cd;
    Edge da;
    Circle cA;
    Circle cB;
    Circle cC;
    Circle cD;
    Circle[] circles;
    boolean hilfslinien;

    public Obstacle(Pane canvas){
        this.rect = new Rectangle();
        rect.setFill(Color.TRANSPARENT);
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStrokeWidth(10.0);
        rect.setStroke(Color.BLACK);
        this.ab = new Edge();
        this.bc = new Edge();
        this.cd = new Edge();
        this.da = new Edge();
        this.cA = new Circle();
        this.cB = new Circle();
        this.cC = new Circle();
        this.cD = new Circle();
        circles = new Circle[]{cA, cB, cC, cD};
        hilfslinien = false;

        calcCorners(canvas);
    }

    public void drawCircles(Pane pane, Point a, Point b, Point c, Point d){
        //Farbige Eckpunkte
        //A = ROT, B = BLAU,
        cA.setRadius(5);
        cB.setRadius(5);
        cC.setRadius(5);
        cD.setRadius(5);
        cA.setFill(Color.RED);
        cB.setFill(Color.BLUE);
        cC.setFill(Color.GREEN);
        cD.setFill(Color.YELLOW);
        cA.setCenterX(a.x);
        cA.setCenterY(a.y);
        cB.setCenterX(b.x);
        cB.setCenterY(b.y);
        cC.setCenterX(c.x);
        cC.setCenterY(c.y);
        cD.setCenterX(d.x);
        cD.setCenterY(d.y);
        pane.getChildren().addAll(cA, cB, cC, cD);
    }

    public Circle[] getCircles() {
        return circles;
    }

    public void updateCircles(){
        cA.setCenterX(a.x);
        cA.setCenterY(a.y);
        cB.setCenterX(b.x);
        cB.setCenterY(b.y);
        cC.setCenterX(c.x);
        cC.setCenterY(c.y);
        cD.setCenterX(d.x);
        cD.setCenterY(d.y);
    }

    //Berechnen der Eckpunkte
    public void calcCorners(Pane canvas){
        rect.setRotate(0);

        this.centerX = rect.getX()+rect.getWidth()/2;
        this.centerY = rect.getY()+rect.getHeight()/2;
        this.a = getCornerPoints(rect.getX(), rect.getY(), centerX, centerY, angle);
        this.b = getCornerPoints(rect.getX()+rect.getWidth(), rect.getY(), centerX, centerY, angle);
        this.c = getCornerPoints(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight(), centerX, centerY, angle);
        this.d = getCornerPoints(rect.getX(), rect.getY()+rect.getHeight(), centerX, centerY, angle);

        ab.calcEdges(a,b);
        bc.calcEdges(b,c);
        cd.calcEdges(c,d);
        da.calcEdges(d,a);

        rect.setRotate(angle);
        updateCircles();

        System.out.println("A: "+a.x+"/"+a.y);
        System.out.println("B: "+b.x+"/"+b.y);
        System.out.println("C: "+c.x+"/"+c.y);
        System.out.println("D: "+d.x+"/"+d.y);

    }

    //Berechnet Eckpunkte mit Rotation
    public Point getCornerPoints(double x, double y, double cx, double cy, double angle){

        angle = Math.toRadians(angle);

        //Verschiebung in Ursprung
        double tempX = x -cx;
        double tempY = y -cy;

        //Rotation des Punktes
        double rotatedX = tempX*Math.cos(angle) - tempY*Math.sin(angle);
        double rotatedY = tempX*Math.sin(angle) + tempY*Math.cos(angle);

        //Zurückverschiebung an Richtige Stelle
        int xNew=(int) (rotatedX + cx);
        int yNew=(int) (rotatedY + cy);

        return new Point(xNew,yNew);

    }



    public Rectangle getRect(){
        return rect;
    }

    public void setRectX(double x){
        this.rect.setX(x);
    }

    public void setRectY(double y){
        this.rect.setY(y);
    }

    public void setAngle(double angle){
        this.angle=angle;
    }

    public double getAngle(){
        return angle;
    }

    public double getCenterX(){
        return centerX;
    }

    public double getCenterY(){
        return centerY;
    }

    public int getAX()
    {
        return a.x;
    }

    public int getAY()
    {
        return a.y;
    }

    public int getBX()
    {
        return b.x;
    }

    public int getBY()
    {
        return b.y;
    }

    public int getCX()
    {
        return c.x;
    }

    public int getCY()
    {
        return c.y;
    }

    public int getDX()
    {
        return d.x;
    }

    public int getDY()
    {
        return d.y;
    }

    public Point getA()
    {
        return a;
    }

    public Point getB()
    {
        return b;
    }

    public Point getC()
    {
        return c;
    }

    public Point getD()
    {
        return d;
    }

    public Edge getEAB()
    {
        return ab;
    }

    public Edge getEBC()
    {
        return bc;
    }

    public Edge getECD()
    {
        return cd;
    }

    public Edge getEDA()
    {
        return da;
    }

    public void setHilfslinien(boolean hilfslinien) {
        this.hilfslinien = hilfslinien;
    }

    public boolean isHilfslinien() {
        return hilfslinien;
    }




}

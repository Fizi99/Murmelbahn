package sample;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.awt.*;

public class Aufzug {

    Rectangle rect;
    Line line;
    double angle = 0;
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
    boolean start=false;
    double vY;
    double vYOld;
    double aY = 0;
    double width = 15; //breite der Feder(mm)
    double thickness = 2; //dicke des Federmaterials(mm)
    double eModul = 206000; //Elastizitätsmodul hier: Federstahl(N/mm^2)
    double radius = 15; //Rollradius (Krümmungsradius der freien Feder)(mm)
    double force;
    double m = 120;
    double mDefault = 120; //Gewicht der Feder(g)

    public Aufzug(){
        this.rect = new Rectangle();
        rect.setHeight(20);
        rect.setWidth(20);
        rect.setFill(Color.BLACK);
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStrokeWidth(10.0);
        rect.setStroke(Color.BLACK);

        this.line = new Line();

        this.ab = new Edge();
        this.bc = new Edge();
        this.cd = new Edge();
        this.da = new Edge();

        this.vY=0;

        calcCorners();
        calcForce();
    }

    //Kraft der Feder wird berechent (F=(eModul*breite*(dicke^3))/(24*Rollradius))
    public void calcForce(){

        this.force=(eModul*width*(Math.pow(thickness,3)))/(24*radius);


    }

    //Prüft ob der Aufzug das Ende der Schiene erreicht hat und bewegt den Aufzug entsprechend
    public void moveAlongLine(){


        double vXdistance= line.getEndX()-centerX;
        double vYdistance= line.getEndY()-centerY;

        double vDistanceLength = Math.sqrt(Math.pow(vXdistance,2)+Math.pow(vYdistance,2));

        this.aY=(-1)*(force/m);


        if(vDistanceLength<=((-1)*vY)/100){

            this.vY=0;
            this.aY=0;
            this.start=false;

        }else{

            moveAufzug();
            this.vYOld=vY;
        }

        resetM();

    }

    //bewegt den Aufzug
    public void moveAufzug(){

        this.centerY=(centerY + vYOld * Konstante.DELTAT + 0.5 * aY * Math.pow(Konstante.DELTAT, 2));

        this.vY=(vYOld + aY * Konstante.DELTAT);

        rect.setY(centerY-rect.getHeight()/2);



        calcCorners();
        calcForce();

    }

    //Ecken werden wie beim Obstacle bestimmt
    public void calcCorners(){
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

    }

    //Endpunkte der Schiene werden kalkuliert und gesetzt
    public void calcLine(){
        double vX = line.getEndX()-line.getStartX();
        double vY = line.getEndY()-line.getStartY();

        double vLength = Math.sqrt(Math.pow(vX,2)+Math.pow(vY,2));

        double vXnew= Math.cos(Math.toRadians(angle-90))*vLength;
        double vYnew= Math.sin(Math.toRadians(angle-90))*vLength;

        line.setEndX(line.getStartX()+vXnew);
        line.setEndY(line.getStartY()+vYnew);
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

    public void setLineEnd(double y){
        this.line.setEndX(rect.getX()+rect.getWidth()/2);
        this.line.setEndY(y);
    }


    public void setStart(boolean start){
        this.start=start;
    }

    public Rectangle getRect(){
        return rect;
    }

    public Line getLine(){
        return line;
    }

    public double getvYOld(){
        return vYOld;
    }

    public void setvYOld(double vYOld){
        this.vYOld=vYOld;
    }

    public boolean getStart(){
        return start;
    }

    public void setCenter(double x, double y){
        rect.setX(x-rect.getWidth()/2);
        rect.setY(y-rect.getHeight()/2);

        calcCorners();
    }

    //Masse wird auf den Defaultwert zurückgesetzt
    public void resetM(){
        m=mDefault;
        calcForce();
        this.aY=(-1)*(force/m);
    }


    public void setvY(double y){
        this.vY=y;
    }

    //Masse wird hinzuaddiert
    public void addM(double m){
        this.m=this.m+m;
        calcForce();
        this.aY=(-1)*(force/this.m);
        this.vY=(vYOld + aY * Konstante.DELTAT);
    }

    public void setmDefault(double m){
        this.mDefault=m;
    }

    public void setM(double m){
        this.m=m;
    }

    public void setRadius(double r){
        this.radius=r;
    }

    public void setWidth(double w){
        this.width=w;
    }

    public void setThickness(double t){
        this.thickness=t;
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

    public double getVY(){
        return vY;
    }

    public double getM(){
        return m;
    }
}

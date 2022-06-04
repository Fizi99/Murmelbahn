package sample;

import javafx.scene.shape.Line;

import java.awt.*;

public class Edge {

    double vabX;
    double vabY;

    double vorthX;
    double vorthY;

    double vNormX;
    double vNormY;

    double vEinX;
    double vEinY;

    double lengthVOrth;
    double lengthVAB;

    Point a;
    Point b;

    double sPX;
    double sPY;


//    Line orth;



    public Edge(){


    }

    public void calcEdges(Point a, Point b){

        this.a=a;
        this.b=b;

        //Vektor der Kante
        this.vabX= b.x - a.x;
        this.vabY= b.y - a.y;

        //Vektor der Orthogonalen
        this.vorthX= vabY;
        this.vorthY= (-1)*vabX;

        this.lengthVOrth = Math.sqrt(Math.pow(vorthX,2)+Math.pow(vorthY,2));
        this.lengthVAB = Math.sqrt(Math.pow(vabX,2)+Math.pow(vabY,2));

        this.vEinX = vabX/lengthVAB;
        this.vEinY = vabY/lengthVAB;

        this.vNormX = vorthX/lengthVOrth;
        this.vNormY = vorthY/lengthVOrth;

//        this.orth.setStartX(a.x);
//        this.orth.setStartY(a.y);
//        this.orth.setEndX(a.x+vNormX);
//        this.orth.setEndY(a.y+vNormY);



    }

//    public void setNorm(double spx, double spy){
//        this.orth.setStartX(spx);
//        this.orth.setStartY(spy);
//        this.orth.setEndX(spx+vorthX);
//        this.orth.setEndY(spy+vorthY);
//    }



    public double getVabX(){
        return vabX;
    }

    public double getVabY(){
        return vabY;
    }

    public double getVNormX(){
        return vNormX;
    }

    public double getVNormY(){
        return vNormY;
    }

    public double getVEinX(){
        return vEinX;
    }

    public double getVEinY(){
        return vEinY;
    }

    public Point getA(){
        return a;
    }

    public Point getB(){
        return b;
    }

    public double getsPX() {
        return sPX;
    }

    public double getsPY() {
        return sPY;
    }

    public void setsPX(double sPX) {
        this.sPX = sPX;
    }

    public void setsPY(double sPY) {
        this.sPY = sPY;
    }

    public double getLengthVOrth(){
        return lengthVOrth;
    }


}

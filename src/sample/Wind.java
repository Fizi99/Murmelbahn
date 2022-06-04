package sample;

public class Wind {

    double angle;
    double a;
    double aX;
    double aY;
    double aXOld;
    double aYOld;

    public Wind(double angle, double a){

        this.angle = Math.toRadians(angle);
        this.a = a;

        calcA();
    }

    //Vektor wird aus sin; cos * Windstärke berechnet (sin=y; cos=x)
    public void calcA(){
        this.aXOld=aX;
        this.aYOld=aY;
        this.aX=Math.cos(angle)*a;
        this.aY=(-1)*Math.sin(angle)*a;
    }

    public double getAX() {
        return aX;
    }

    public double getAY() {
        return aY;
    }

    public double getAXOld() {
        return aXOld;
    }

    public double getAYOld() {
        return aYOld;
    }

    public void setA(double a){
        this.a = a;
        calcA();
    }

    public void setAngle(double angle){
        this.angle = Math.toRadians(angle);
        calcA();
    }
}

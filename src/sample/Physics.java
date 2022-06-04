package sample;

public class Physics {

    double schiefeA;
    double aX;
    double s0X;
    double v0X;
    double aY;
    double s0Y;
    double v0Y;
    double v;
    double angle;
    double m;
    double rollX;
    double rollY;



    public Physics(double s0X, double s0Y){

        this.s0X = s0X;
        this.s0Y = s0Y;
        this.aX = 0;
        this.aY = 0;
        this.v0X = 0;
        this.v0Y = 0;
        this.angle = 0;
        this.m = 10;

    }


    //Beschleunigungen werden hinzuaddiert
    public void updateAX(double a, double aOld){
        this.aX=aX-aOld;
        this.aX=aX+a;
    }

    public void updateAY(double a, double aOld){
        this.aY=aY-aOld;
        this.aY=aY+a;
    }

    public void addA(double x, double y){
        this.aX=aX+x;
        this.aY=aY+y;
    }

    public void calcV(){
        this.angle = Math.toRadians(angle);
        //Vektor wird aus sin; cos * Geschwindigkeit berechnet (sin=y; cos=x)
        this.v0X=Math.cos(angle)*v;
        this.v0Y=(-1)*Math.sin(angle)*v;
    }

    public void calcSpeed(){
        this.v =(Math.sqrt(Math.pow(v0X,2)+Math.pow(v0Y,2)));
    }

    public void resetA(){
        this.aX=0;
        this.aY=0;
    }

    public void addV(double x, double y){
        this.v0X=v0X+x;
        this.v0Y=v0Y+y;

        calcSpeed();
    }



    public void setAngle(double a){
        this.angle = a;
    }

    public void setV(double v){
        this.v = v;
    }

    public void setAccelerationX(double a){
        this.aX = a;
    }

    public double getAccelerationX() {
        return aX;
    }

    public double getS0X() {
        return s0X;
    }

    public double getV0X() {
        return v0X;
    }

    public void setS0X(double s0) {
        this.s0X = s0;
    }

    public void setV0X(double v0) {
        this.v0X = v0;
        calcSpeed();
    }

    public void setAccelerationY(double a){
        this.aY = a;
    }

    public double getAccelerationY() {
        return aY;
    }

    public double getS0Y() {
        return s0Y;
    }

    public double getV0Y() {
        return v0Y;

    }

    public double getV() {
        return v;
    }

    public void setS0Y(double s0) {
        this.s0Y = s0;
    }

    public void setV0Y(double v0) {
        this.v0Y = v0;
        calcSpeed();
    }

    public double getM(){
        return m;
    }

    public void setM(double m){
        this.m = m;
    }

    public void setSchiefeA(double aSchief) {
        this.schiefeA = aSchief;
    }

    public double getSchiefeA() {
        return schiefeA;
    }

    public double getRollX() {
        return rollX;
    }

    public double getRollY() {
        return rollY;
    }

    public void setRollX(double rollX) {
        this.rollX = rollX;
    }

    public void setRollY(double rollY) {
        this.rollY = rollY;
    }
}

package sample;

import javafx.scene.shape.Circle;

public class Ball {

    double s0YOld;

    private double x;
    private double y;
    private double r;
    private Physics physics;
    private Circle ball;
    private StateE state;

    public Ball(double r, Physics physics){
        this.r = r;
        this.physics = physics;
        this.ball = new Circle();
        this.ball.setCenterX(physics.getS0X());
        this.ball.setCenterY(physics.getS0Y());
        this.ball.setRadius(r);
        this.state=StateE.ISFALLING;

    }

    //Ball wird in X Richtung bewegt
    public void moveBallX() {


        this.ball.setCenterX(physics.getS0X() + physics.getV0X() * Konstante.DELTAT + 0.5 * physics.getAccelerationX() * Math.pow(Konstante.DELTAT, 2));
        this.physics.setS0X(ball.getCenterX());
        this.physics.setV0X(physics.getV0X() + physics.getAccelerationX() * Konstante.DELTAT);

    }

    //Ball wird in Y Richtung bewegt
    public void moveBallY(){


        this.s0YOld = physics.getS0Y();
        this.ball.setCenterY(physics.getS0Y() + physics.getV0Y() * Konstante.DELTAT + 0.5 * physics.getAccelerationY() * Math.pow(Konstante.DELTAT, 2));
        this.physics.setS0Y(ball.getCenterY());
        this.physics.setV0Y(physics.getV0Y() + physics.getAccelerationY() * Konstante.DELTAT);


    }

    public void calcSpeed(){
        this.physics.setV(Math.sqrt(Math.pow(physics.getV0X(),2)+Math.pow(physics.getV0Y(),2)));
    }



    public Circle getBall(){
        return ball;
    }

    public Physics getPhysics(){
        return physics;
    }

    public double getRadius(){
        return ball.getRadius();
    }

    public void setState(StateE state) {
        this.state = state;
    }

    public StateE getState() {
        return state;
    }
}

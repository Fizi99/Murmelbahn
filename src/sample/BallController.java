package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;

public class BallController {

    Ball ball;
    Line direction;
    Label labelV = new Label("Velocity [m/s]: ");
    Label labelAngle = new Label("Angle: ");
    Label labelMass = new Label("Masse: [g]");
    Label labelTacho = new Label("Speed [m/s]: ");
    Label labelTachoDisplay = new Label("0.0");
    Slider sliderV;
    Slider sliderAngle;
    Slider sliderMass;
    Button togglePauseBtn = new Button("Start/Stop");
    GridPane pane = new GridPane();
    boolean togglePause = false;




    //Pane zur Kontrolle des Balls werden erstellt
    public BallController(double s0X, double s0Y){
        Physics physics= new Physics(s0X, s0Y);
        this.ball = new Ball(10, physics);
        this.direction = new Line(ball.getBall().getCenterX(), ball.getBall().getCenterY(), ball.getBall().getCenterX()+ball.getPhysics().getV0X(), ball.getBall().getCenterY()+ball.getPhysics().getV0Y());
        this.sliderV = initSlider(-10, 10, 0, 2);
        this.sliderAngle = initSlider(0, 360, 0, 45);
        this.sliderMass = initSlider(5, 50, 10, 10);
        pane.setPadding(new Insets(10, 10, 10, 30));
        pane.setHgap(40);
        pane.setVgap(10);

        pane.add(labelTacho, 0,0);
        pane.add(labelAngle, 0,1);
        pane.add(labelV, 0,2);
        pane.add(labelMass, 0,3);
        pane.add(labelTachoDisplay, 1,0);
        pane.add(sliderAngle, 1,1);
        pane.add(sliderV, 1,2);
        pane.add(sliderMass, 1,3);
        pane.add(togglePauseBtn, 0,4);



        //Listener für Richtung
        sliderAngle.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                ball.getPhysics().setAngle(sliderAngle.getValue());
                ball.getPhysics().calcV();
                updateDirection();

            }
        });

        //Listener für Geschwindigkeit
        sliderV.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                ball.getPhysics().setV(sliderV.getValue()*Konstante.DISTANCEFACTOR);
                ball.getPhysics().calcV();
                updateDirection();

            }
        });

        //Listener für Masse
        sliderMass.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                ball.getPhysics().setM(sliderMass.getValue());


            }
        });

        //Listener für Pause Button
        togglePauseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(togglePause){
                    togglePause=false;

                }else{
                    togglePause=true;
                }

            }
        });

    }


    //Slider wird initialisiert
    Slider initSlider(double min, double max, double value, double ticks){
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(ticks);
        return slider;
    }



    GridPane getPane(){
        return pane;
    }

    Ball getBall(){
        return ball;
    }

    public Line getDirection(){
        return direction;
    }
    boolean getStartStop() {
        return togglePause;
    }

    public void setStartStop(boolean b){
        this.togglePause = b;
    }

    public void updateDirection(){
        this.direction.setStartX(ball.getBall().getCenterX());
        this.direction.setStartY(ball.getBall().getCenterY());
        this.direction.setEndX(ball.getBall().getCenterX()+ball.getPhysics().getV0X());
        this.direction.setEndY(ball.getBall().getCenterY()+ball.getPhysics().getV0Y());
    }

    public void setTacho(){
        ball.getPhysics().calcSpeed();
        this.labelTachoDisplay.setText(Double.toString(ball.getPhysics().getV()/Konstante.DISTANCEFACTOR));
    }

    public Label getLabelTachoDisplay() {
        return labelTachoDisplay;
    }
}

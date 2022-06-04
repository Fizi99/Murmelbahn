package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class ObstacleController {

    Obstacle obs;
    Label labelAngle = new Label("Angle: ");
    Slider sliderAngle;
    GridPane pane = new GridPane();
    Pane canvas;

    public ObstacleController(Pane canvas){
        this.obs = new Obstacle(canvas);
        this.sliderAngle = initSlider(0,360,0);
        pane.setPadding(new Insets(10, 10, 10, 30));

        pane.setHgap(40);
        pane.setVgap(10);

        pane.add(labelAngle, 0,0);
        pane.add(sliderAngle, 1,0);

        this.canvas=canvas;


        sliderAngle.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                obs.setAngle(sliderAngle.getValue()*(-1));
                obs.calcCorners(canvas);


            }
        });
    }

    Slider initSlider(double min, double max, double value){
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(45);
        return slider;
    }



    public Obstacle getObs(){
        return obs;
    }

    public GridPane getPane(){
        return pane;
    }

}

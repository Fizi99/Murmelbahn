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
import javafx.scene.layout.Pane;

public class AufzugController {

    Aufzug aufzug;

    GridPane pane = new GridPane();
    Button startAufzugBtn = new Button("Start");
    Button resetAufzugBtn = new Button("Reset");
    Label labelMass = new Label("Masse: [g]");
    Slider sliderMass;
    Label labelRadius = new Label("Rollradius: [mm]");
    Slider sliderRadius;
    Label labelWidth = new Label("Breite: [mm]");
    Slider sliderWidth;
    Label labelThickness = new Label("Dicke: [mm]");
    Slider sliderThickness;


    public AufzugController() {
        this.aufzug = new Aufzug();
        pane.setPadding(new Insets(10, 10, 10, 30));

        this.sliderMass = initSlider(100, 500, 120, 100);
        this.sliderRadius = initSlider(5, 30, 15, 5);
        this.sliderWidth = initSlider(1, 30, 15, 5);
        this.sliderThickness = initSlider(0.1, 5, 2, 1);

        pane.setHgap(40);
        pane.setVgap(10);

        pane.add(labelMass, 0, 0);
        pane.add(sliderMass, 1,0);
        pane.add(labelRadius, 0, 1);
        pane.add(sliderRadius, 1,1);
        pane.add(labelWidth, 0, 2);
        pane.add(sliderWidth, 1,2);
        pane.add(labelThickness, 0, 3);
        pane.add(sliderThickness, 1,3);
        pane.add(startAufzugBtn, 0,4);
        pane.add(resetAufzugBtn, 1,4);


        //Listener für Start Aufzug Button
        startAufzugBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(aufzug.getStart()){
                    aufzug.setStart(false);

                }else{
                    aufzug.setStart(true);
                }

            }
        });

        //Listener für Reset Aufzug Button
        resetAufzugBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {

                aufzug.setCenter(aufzug.getLine().getStartX(), aufzug.getLine().getStartY());
                aufzug.setvY(0);
                aufzug.calcForce();

            }
        });

        //Listener für Masse
        sliderMass.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                aufzug.setmDefault(sliderMass.getValue());
                aufzug.setM(sliderMass.getValue());
                aufzug.calcForce();
            }
        });

        //Listener für Radius
        sliderRadius.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {

                aufzug.setRadius(sliderRadius.getValue());
                aufzug.calcForce();
            }
        });

        //Listener für Breite
        sliderWidth.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {

                aufzug.setWidth(sliderWidth.getValue());
                aufzug.calcForce();
            }
        });

        //Listener für Dicke
        sliderThickness.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {

                aufzug.setThickness(sliderThickness.getValue());
                aufzug.calcForce();
            }
        });
    }

    Slider initSlider(double min, double max, double value, double tick){
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(tick);
        return slider;
    }

    public Aufzug getAufzug(){
        return aufzug;
    }

    public Pane getPane(){
        return pane;
    }

}


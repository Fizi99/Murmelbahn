package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.css.Rect;

import java.sql.Time;
import java.util.ArrayList;

public class Main extends Application {

    //Murmel und Physics Deklaration
    double tempDragX;
    double tempDragY;
    boolean mReleased;
    boolean paused = true;
    boolean pauseAllAufzug=true;
    static float t = 0;
    static double sGes = 0;
    static Gravitation gravity = new Gravitation(9.81);
    static Wind wind = new Wind(0,0);
    static ArrayList<BallController> ballCtrlArray = new ArrayList<BallController>();
    static ArrayList<ObstacleController> obstacleArrayList = new ArrayList<ObstacleController>();
    static ArrayList<AufzugController> aufzugArrayList = new ArrayList<AufzugController>();
    Pane canvas = new Pane();
    Kollision kollision = new Kollision(ballCtrlArray, obstacleArrayList,aufzugArrayList, canvas);
    BallController tempB;
    static int clickcount = 0;
    AufzugController currentAufzug;


    @Override
    public void start(Stage primaryStage) throws Exception {



        //Scene auf Pane
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 700);

        //Bildbereiche werden definiert
        TabPane tabPane = new TabPane();
        VBox controlM = new VBox();
        VBox controlO = new VBox();
        VBox controlG = new VBox();
        VBox controlA = new VBox();
        ScrollPane scrollM = new ScrollPane();
        ScrollPane scrollO = new ScrollPane();
        ScrollPane scrollA = new ScrollPane();
        Button pauseSimBtn = new Button("Start/Stop Simulation");
        Button resetBtn = new Button("Reset");
        Button startAufzugBtn = new Button("Start/Stop Aufzug");


        //Elemente werden auf panes platziert
        //Control für Globalen Tab initialisiert
        Slider sliderGrav = initSlider(0,15, 9.81, 1);
        Label labelGrav = new Label("Gravity: ");

        Slider sliderWindAngle = initSlider(0,360, 0, 45);
        Label labelWindAngle = new Label("Wind direction: ");

        Slider sliderWindAcceleration = initSlider(0,15, 0, 1);
        Label labelWindAcceleration = new Label("Wind acceleration: ");

        controlG.getChildren().addAll(labelGrav, sliderGrav, labelWindAngle, sliderWindAngle, labelWindAcceleration, sliderWindAcceleration);


        //grid wird platziert
        int x = 0;
        int y = 0;
        while (x <= scene.getWidth()) {
            canvas.getChildren().add(initGridV(x, scene));
            x = x + Konstante.DISTANCEFACTOR;
        }
        while (y <= scene.getHeight()) {
            canvas.getChildren().add(initGridH(y, scene));
            y = y + Konstante.DISTANCEFACTOR;
        }

        canvas.getChildren().add(pauseSimBtn);
        canvas.getChildren().add(resetBtn);
        canvas.getChildren().add(startAufzugBtn);
        resetBtn.relocate(135,0);
        startAufzugBtn.relocate(180,0);

        //Szene wird gezeichnet
        controlM.setPadding(new Insets(15, 12, 15, 12));
        controlM.setSpacing(10);
        controlG.setPadding(new Insets(15, 12, 15, 12));
        controlG.setSpacing(10);
        controlO.setPadding(new Insets(15, 12, 15, 12));
        controlO.setSpacing(10);
        controlA.setPadding(new Insets(15, 12, 15, 12));
        controlA.setSpacing(10);

        scrollM.setContent(controlM);
        scrollO.setContent(controlO);
        scrollA.setContent(controlA);

        Tab tab1 = new Tab("Murmeln", scrollM);
        Tab tab2 = new Tab("Global"  , controlG);
        Tab tab3 = new Tab("Obstacles"  , scrollO);
        Tab tab4 = new Tab("Aufzug"  , scrollA);

        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab3);
        tabPane.getTabs().add(tab4);
        tabPane.getTabs().add(tab2);
        tabPane.setPrefWidth(400);

        root.setCenter(canvas);
        root.setLeft(tabPane);
        primaryStage.setTitle("Murmelbahn");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.getStylesheets().add("file:src/sample/stylesheet.css");

        update();

        //Listener für Pause Button
        pauseSimBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(paused){
                    paused=false;
                    for(BallController ballCtrl : ballCtrlArray){
                        ballCtrl.setStartStop(true);
                    }
                }else{
                    paused=true;
                    for(BallController ballCtrl : ballCtrlArray){
                        ballCtrl.setStartStop(false);
                    }
                }

            }
        });

        //Listener für Aufzug Button
        startAufzugBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(pauseAllAufzug){
                    pauseAllAufzug=false;
                    for(AufzugController aufzugCtrl : aufzugArrayList){
                        aufzugCtrl.getAufzug().setStart(true);
                    }
                }else{
                    pauseAllAufzug=true;
                    for(AufzugController aufzugCtrl : aufzugArrayList){
                        aufzugCtrl.getAufzug().setStart(false);
                    }
                }

            }
        });

        //Listener für Reset Button
        resetBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {

                paused=true;

                //leert Kugelarray
                for(int i = 0; i<ballCtrlArray.size(); i++){
                    canvas.getChildren().remove(ballCtrlArray.get(i).getBall().getBall());
                    canvas.getChildren().remove(ballCtrlArray.get(i).getDirection());
                    controlM.getChildren().removeAll(ballCtrlArray.get(i).getPane());
                }
                ballCtrlArray.clear();

                //leert Obstaclearray
                for(int i = 0; i<obstacleArrayList.size(); i++){
                    canvas.getChildren().remove(obstacleArrayList.get(i).getObs().getRect());
                    canvas.getChildren().removeAll(obstacleArrayList.get(i).getObs().getCircles());
                    controlO.getChildren().removeAll(obstacleArrayList.get(i).getPane());
                }
                obstacleArrayList.clear();

                for(int i = 0; i<aufzugArrayList.size(); i++){
                    canvas.getChildren().remove(aufzugArrayList.get(i).getAufzug().getRect());
                    canvas.getChildren().removeAll(aufzugArrayList.get(i).getAufzug().getLine());
                    controlA.getChildren().removeAll(aufzugArrayList.get(i).getPane());
                }
                aufzugArrayList.clear();


            }
        });


        //Listener zum hinzufügen von Bällen
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                //Funktioniert nur im ersten Tab
                //Ball
                if (tabPane.getSelectionModel().getSelectedIndex() == 0) {

                    tempB = new BallController(event.getX(), event.getY());
                    tempB.getBall().getPhysics().addA(0, gravity.getGrav());
                    tempB.getBall().getPhysics().addA(wind.getAX(), wind.getAY());
                    ballCtrlArray.add(tempB);

                    canvas.getChildren().addAll(tempB.getBall().getBall(), tempB.getDirection());
                    controlM.getChildren().add(tempB.getPane());

                }


                //Obstacles im zweiten Tab
                if (tabPane.getSelectionModel().getSelectedIndex() == 1) {
                    mReleased = false;

                    //Es wird ein temporäres Obstacle geschaffen (im Moment nur Rechtecke)

                    ObstacleController obsCtrl = new ObstacleController(canvas);
                    obstacleArrayList.add(obsCtrl);
                    tempDragX = event.getX();
                    tempDragY = event.getY();
                    obsCtrl.getObs().setRectX(tempDragX);
                    obsCtrl.getObs().setRectY(tempDragY);


                    canvas.getChildren().add(obsCtrl.getObs().getRect());
                    controlO.getChildren().add(obsCtrl.getPane());

                }

                //Aufzug im dritten Tab
                if (tabPane.getSelectionModel().getSelectedIndex() == 2) {

                    if (clickcount==0){
                        AufzugController aufzugCtrl = new AufzugController();
                        aufzugArrayList.add(aufzugCtrl);
                        aufzugCtrl.getAufzug().getRect().setX(event.getX()-10);
                        aufzugCtrl.getAufzug().getRect().setY(event.getY()-10);

                        aufzugCtrl.getAufzug().getLine().setStartX(event.getX());
                        aufzugCtrl.getAufzug().getLine().setStartY(event.getY());
                        aufzugCtrl.getAufzug().getLine().setEndX(event.getX());
                        aufzugCtrl.getAufzug().getLine().setEndY(event.getY());

                        canvas.getChildren().add(aufzugCtrl.getAufzug().getRect());
                        canvas.getChildren().add(aufzugCtrl.getAufzug().getLine());

                        currentAufzug=aufzugCtrl;

                        aufzugCtrl.getAufzug().calcCorners();

                        controlA.getChildren().add(aufzugCtrl.getPane());
                        clickcount=1;


                    }else if(clickcount==1){

                        //Linie wird geupdatet
                        currentAufzug.getAufzug().setLineEnd(event.getY());
                        currentAufzug.getAufzug().calcLine();
                        currentAufzug.getAufzug().calcForce();


                        clickcount=0;
                    }


                }

            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                //Hinzufügen von Bällen; Funktioniert nur im ersten Tab
                if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
                    if (tempB != null) {
                        tempB.getBall().getPhysics().setS0X(event.getX());
                        tempB.getBall().getPhysics().setS0Y(event.getY());
                        tempB = null;
                    }
                }

                //Funktioniert nur im ersten Tab
                if (tabPane.getSelectionModel().getSelectedIndex() == 1) {
                    Obstacle recent = obstacleArrayList.get(obstacleArrayList.size()-1).getObs();
                    obstacleArrayList.get(obstacleArrayList.size()-1).getObs().getRect().setFill(Color.BLACK);
                    obstacleArrayList.get(obstacleArrayList.size()-1).getObs().getRect().setStrokeWidth(0);
                    obstacleArrayList.get(obstacleArrayList.size()-1).getObs().calcCorners(canvas);
                    obstacleArrayList.get(obstacleArrayList.size()-1).getObs().drawCircles(canvas, recent.getA(), recent.getB(), recent.getC(), recent.getD());
                    mReleased = true;
                }

            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {


                //Position vom Ball
                if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
                    if (tempB != null) {
                        tempB.getBall().getBall().setCenterX(event.getX());
                        tempB.getBall().getBall().setCenterY(event.getY());
                    }
                }

                //Funktioniert nur im ersten Tab

                if (tabPane.getSelectionModel().getSelectedIndex() == 1) {

                    //Bedinungen für das Zeichnen eines Rechtecks

                    //Nach Unten Rechts ziehen
                    if((event.getX() > tempDragX) && (event.getY() > tempDragY)) {
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setWidth(event.getX() - tempDragX);
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setHeight(event.getY() - tempDragY);
                    }

                    //Nach Unten Links ziehen
                    if((event.getX() < tempDragX) && (event.getY() > tempDragY)){
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setX(event.getX());
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setWidth( tempDragX - event.getX());
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setHeight(event.getY() - tempDragY);
                    }

                    //Nach Oben Rechts ziehen
                    if((event.getX() > tempDragX) && (event.getY() < tempDragY)){
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setY(event.getY());
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setWidth( event.getX() - tempDragX);
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setHeight(tempDragY - event.getY());
                    }

                    //Nach Oben Links ziehen
                    if((event.getX() < tempDragX) && (event.getY() < tempDragY)){
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setX(event.getX());
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setY(event.getY());
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setWidth( tempDragX - event.getX());
                        obstacleArrayList.get(obstacleArrayList.size() - 1).getObs().getRect().setHeight(tempDragY - event.getY());
                    }



                }

            }
        });



        //Listener für Gravitaionsslider
        sliderGrav.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                gravity.setGrav(sliderGrav.getValue());
                for(BallController ballCtrl : ballCtrlArray){
                    ballCtrl.getBall().getPhysics().updateAY(gravity.getGrav(), gravity.getGravOld());
                }

            }
        });

        //Listener für Windslider Winkel
        sliderWindAngle.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                wind.setAngle(sliderWindAngle.getValue());
                for(BallController ballCtrl : ballCtrlArray){
                    ballCtrl.getBall().getPhysics().updateAX(wind.getAX(), wind.getAXOld());
                    ballCtrl.getBall().getPhysics().updateAY(wind.getAY(), wind.getAYOld());
                }

            }
        });

        //Listener für Windslider Stärke
        sliderWindAcceleration.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                wind.setA(sliderWindAcceleration.getValue()*Konstante.DISTANCEFACTOR);
                for(BallController ballCtrl : ballCtrlArray){
                    ballCtrl.getBall().getPhysics().updateAX(wind.getAX(), wind.getAXOld());
                    ballCtrl.getBall().getPhysics().updateAY(wind.getAY(), wind.getAYOld());
                }

            }
        });

    }

    //Initialisierung grid
    Line initGridV(int x, Scene scene){
        Line line = new Line(x, 0, x, scene.getHeight());
        line.setStroke(Color.GRAY);
        return line;
    }
    Line initGridH(int y, Scene scene){
        Line line = new Line(0, y, scene.getWidth(), y);
        line.setStroke(Color.GRAY);
        return line;
    }

    Slider initSlider(double min, double max, double value, int tick){
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(tick);
        return slider;
    }

    //Update Function
    void update(){

        Timeline timeline = new Timeline(
                //60 frames pro sekunde
                new KeyFrame(Duration.millis(Konstante.DELTAT*1000), e -> {
                    //Kollision wird überprüft


                    kollision.checkDistanceBall();
                    kollision.checkDistanceObs(wind);
                    kollision.checkDistanceAufzug();


                    for(AufzugController aufzugCtrl : aufzugArrayList){

                        if(aufzugCtrl.getAufzug().getStart()){
                            aufzugCtrl.getAufzug().moveAlongLine();
                        }
                    }



                    //Bälle werden bewegt falls Pause nicht aktiviert ist
                    for(BallController ballCtrl : ballCtrlArray){

                        if(ballCtrl.getStartStop()){

                            //Ball wird bewegt und Anzeigen werden aktualisiert
                            ballCtrl.getBall().moveBallX();
                            ballCtrl.getBall().moveBallY();

                            ballCtrl.setTacho();
                            ballCtrl.updateDirection();


                            //Beschleunigung wird für das nächste frame zurückgesetzt und wieder addiert, da beim Rollen Beschleunigungen verändert werden
                            ballCtrl.getBall().getPhysics().resetA();

                            ballCtrl.getBall().getPhysics().updateAY(gravity.getGrav(), 0);
                            ballCtrl.getBall().getPhysics().updateAX(wind.getAX(), 0);
                            ballCtrl.getBall().getPhysics().updateAY(wind.getAY(), 0);

                        }
                    }




                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

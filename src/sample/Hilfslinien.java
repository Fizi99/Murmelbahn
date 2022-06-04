package sample;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.ArrayList;


public class Hilfslinien {

    public static ArrayList<Shape> shapes = new ArrayList<>();

    //Methode um Steigungsdreieck anzuzeigen
    public void drawTriangle(Pane canvas, Edge edge, EdgesE edgeE) {

        //Counter ist für AB, BC, CD, DA
        Line ankathete;
        Line gegenkathete;
        Line hypotenuse;

        //Steigungsabfall nach links
        if (edge.a.y > edge.b.y) {
            //Hypotenuse
            hypotenuse = new Line(edge.a.x, edge.a.y, edge.b.x, edge.b.y);
            System.out.println("Edge: (" + edge.a.x + " , " + edge.a.y + ")   und  " + edge.b.x + " , " + edge.b.y + ")");
            hypotenuse.setStrokeWidth(1);

            //AnKathete u. Gegenkathete
            ankathete = new Line(edge.a.x, edge.a.y, edge.b.x, edge.a.y);
            gegenkathete = new Line(edge.b.x, edge.b.y, edge.b.x, edge.a.y);

            //Farben
            switch (edgeE) {
                case AB:
                    hypotenuse.setStroke(Color.PURPLE);
                    break;
                case BC:
                    hypotenuse.setStroke(Color.CYAN);
                    break;
                case CD:
                    hypotenuse.setStroke(Color.LIGHTYELLOW);
                    break;
                case DA:
                    hypotenuse.setStroke(Color.LIGHTPINK);
                    break;
            }

            ankathete.setStroke(Color.LIGHTGRAY);
            gegenkathete.setStroke(Color.LIGHTGRAY);

            shapes.add(ankathete);
            shapes.add(gegenkathete);
            shapes.add(hypotenuse);
            canvas.getChildren().addAll(hypotenuse, ankathete, gegenkathete);

        }


        //Steigung nach rechts unten
        else if (edge.a.y < edge.b.y) {
            //Hypotenuse
            hypotenuse = new Line(edge.a.x, edge.a.y, edge.b.x, edge.b.y);
            System.out.println("Edge: (" + edge.a.x + " , " + edge.a.y + ")   und  " + edge.b.x + " , " + edge.b.y + ")");
            hypotenuse.setStrokeWidth(1);

            //AnKatheten
            ankathete = new Line(edge.b.x, edge.b.y, edge.a.x, edge.b.y);
            gegenkathete = new Line(edge.a.x, edge.a.y, edge.a.x, edge.b.y);

            //Farben
            switch (edgeE) {
                case AB:
                    hypotenuse.setStroke(Color.PURPLE);
                    break;
                case BC:
                    hypotenuse.setStroke(Color.CYAN);
                    break;
                case CD:
                    hypotenuse.setStroke(Color.LIGHTYELLOW);
                    break;
                case DA:
                    hypotenuse.setStroke(Color.LIGHTPINK);
                    break;
            }
            ankathete.setStroke(Color.LIGHTGRAY);
            gegenkathete.setStroke(Color.LIGHTGRAY);
            canvas.getChildren().addAll(hypotenuse, ankathete, gegenkathete);
            shapes.add(ankathete);
            shapes.add(gegenkathete);
            shapes.add(hypotenuse);

        }





    }
}

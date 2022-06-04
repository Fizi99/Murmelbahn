package sample;

import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.ArrayList;

public class Kollision {

    ArrayList<ObstacleController> obstacleList;
    ArrayList<BallController> ballList;
    ArrayList<AufzugController> aufzugList;
    Hilfslinien linien;
    Edge collisionEdge;
    EdgesE edgeE;
    Pane canvas;
    Wind wind;


    public Kollision(ArrayList<BallController> ballList, ArrayList<ObstacleController> obstacleList,ArrayList<AufzugController> aufzugList, Pane canvas) {
        this.ballList = ballList;
        this.obstacleList = obstacleList;
        this.aufzugList = aufzugList;
        linien = new Hilfslinien();
        this.canvas = canvas;
        this.collisionEdge = new Edge();
    }

    //Überprüft, ob Kollision zwischen zwei Kuglen stattfindet
    public void checkDistanceBall() {

        if (ballList.size() >= 2) {
            for (int i = 0; i < ballList.size(); i++) {
                for (int j = i + 1; j < ballList.size(); j++) {

                    double distance = distanceBall(ballList.get(i).getBall(), ballList.get(j).getBall());

                    if(testDirectionBall(ballList.get(i).getBall(),ballList.get(j).getBall())){
                        if (testThreshholdBall(ballList.get(i).getBall(),ballList.get(j).getBall(), distance)) {
                            kollisionBall(ballList.get(i).getBall(),ballList.get(j).getBall());

                        }
                    }
                }
            }
        }
    }


    //Prüft ob Kollision zwischen Kugel und Obstacel stattfindet
    public void checkDistanceObs(Wind wind) {

        this.wind = wind;

        if (ballList.size() >= 1 && obstacleList.size() >= 1) {
            for (int i = 0; i < ballList.size(); i++) {
                for (int j = 0; j < obstacleList.size(); j++) {

                    double distance = distanceObstacle(ballList.get(i).getBall(), obstacleList.get(j).getObs());

                    //check ob Kollision oder nicht unter Betrachtung der Geschwindigkeit der Bälle (ges/35)

                    //prüft Flugrichtung
                    if(testDirectionObstacle(ballList.get(i).getBall(), collisionEdge)){
                        if(testThreshholdObstacle(ballList.get(i).getBall(), collisionEdge, distance)){
                            kollisionObstacle(ballList.get(i).getBall(), collisionEdge);

                        }
                    }

                }
            }
        }
    }

    //Prüft ob Kollision zwischen Aufzug und Kugel stattfindet
    public void checkDistanceAufzug() {

        if (ballList.size() >= 1 && aufzugList.size() >= 1) {
            for (int i = 0; i < ballList.size(); i++) {
                for (int j = 0; j < aufzugList.size(); j++) {

                    double distance = distanceAufzug(ballList.get(i).getBall(), aufzugList.get(j).getAufzug());

                    //check ob Kollision stattfindet in den Fällen:
                    //1. Kugel rollte bereits vorher auf dem Aufzug (hierbei wird die Richtung nicht beachtet)
                    //2. Kugel springt auf dem sich bewegenden Aufzug (Aufzugsbewegung wird in die Richtungsbestimmung mit einbezogen)
                    //3. Der Aufzug ewegt sich nicht und fungiert als Obstacle

                    if(ballList.get(i).getBall().getState()==StateE.ISAUFZUG||(ballList.get(i).getBall().getState()==StateE.ISROLLING&&aufzugList.get(j).getAufzug().getStart())){
                        if (testThreshholdAufzug(ballList.get(i).getBall(), collisionEdge, distance)) {

                            kollisionAufzug(ballList.get(i).getBall(), collisionEdge, aufzugList.get(j).getAufzug(), distance);

                        }
                    }else if(ballList.get(i).getBall().getState()==StateE.ISFALLING&&aufzugList.get(j).getAufzug().getStart()){
                        if(testDirectionAufzug(ballList.get(i).getBall(), aufzugList.get(j).getAufzug())) {
                            if (testThreshholdObstacle(ballList.get(i).getBall(), collisionEdge, distance)) {

                                kollisionAufzug(ballList.get(i).getBall(), collisionEdge, aufzugList.get(j).getAufzug(), distance);

                            }
                        }
                    }else{
                        if(testDirectionObstacle(ballList.get(i).getBall(), collisionEdge)) {
                            if (testThreshholdObstacle(ballList.get(i).getBall(), collisionEdge, distance)) {

                                kollisionAufzug(ballList.get(i).getBall(), collisionEdge, aufzugList.get(j).getAufzug(), distance);

                            }
                        }
                    }

                }
            }
        }
    }


    //Berechnung des Abstandes zwischen zwei Kugeln
    public double distanceBall(Ball ball1, Ball ball2) {

        double vektorX = ball1.getPhysics().getS0X() - ball2.getPhysics().getS0X();
        double vektorY = ball1.getPhysics().getS0Y() - ball2.getPhysics().getS0Y();

        return ((Math.sqrt(Math.pow(vektorX, 2) + Math.pow(vektorY, 2)))- (ball1.getRadius() + ball2.getRadius()));

    }

    public double distanceObstacle(Ball ball, Obstacle obs) {

        double distance;

        //Berechnet Distanz für jede Kante
        double distanceAB = distanceEdge(ball, obs.getEAB());
        double distanceBC = distanceEdge(ball, obs.getEBC());
        double distanceCD = distanceEdge(ball, obs.getECD());
        double distanceDA = distanceEdge(ball, obs.getEDA());

        //schaut welche Kante die nächste ist
        if (distanceAB <= distanceBC && distanceAB <= distanceCD && distanceAB <= distanceDA) {
            distance = distanceAB;
            edgeE = EdgesE.AB;
            this.collisionEdge = obs.getEAB();
        } else if (distanceBC <= distanceCD && distanceBC <= distanceDA) {
            distance = distanceBC;
            edgeE = EdgesE.BC;
            this.collisionEdge = obs.getEBC();
        } else if (distanceCD <= distanceDA) {
            distance = distanceCD;
            edgeE = EdgesE.CD;
            this.collisionEdge = obs.getECD();
        } else {
            distance = distanceDA;
            edgeE = EdgesE.DA;
            this.collisionEdge = obs.getEDA();
        }

        return distance;
    }

    public double distanceAufzug(Ball ball, Aufzug aufzug) {

        double distance;

        //Berechnet Distanz für jede Kante
        double distanceAB = distanceEdge(ball, aufzug.getEAB());
        double distanceBC = distanceEdge(ball, aufzug.getEBC());
        double distanceCD = distanceEdge(ball, aufzug.getECD());
        double distanceDA = distanceEdge(ball, aufzug.getEDA());

        //schaut welche Kante die nächste ist
        if (distanceAB <= distanceBC && distanceAB <= distanceCD && distanceAB <= distanceDA) {
            distance = distanceAB;
            edgeE = EdgesE.AB;
            this.collisionEdge = aufzug.getEAB();
        } else if (distanceBC <= distanceCD && distanceBC <= distanceDA) {
            distance = distanceBC;
            edgeE = EdgesE.BC;
            this.collisionEdge = aufzug.getEBC();
        } else if (distanceCD <= distanceDA) {
            distance = distanceCD;
            edgeE = EdgesE.CD;
            this.collisionEdge = aufzug.getECD();
        } else {
            distance = distanceDA;
            edgeE = EdgesE.DA;
            this.collisionEdge = aufzug.getEDA();
        }

        return distance;
    }

    //Abstand Punkt zur Kante berechnet
    public double distanceEdge(Ball ball, Edge ab) {

        //g: A+t*vektor AB
        //punkt = circle center
        //orthogonalvektor=vektorAB*vektor=(0/0)===> Bsp: vektorAB: (5/6) ==> (6/-5)
        //lotgerade: punkt + r * orthvektor
        //lotgerade=g ==> A+t*vektorAB = punkt + r*orthvektor =========> t*vektorABX -r*orthvektorX = pX-aX
        //AX+t*vektorABX = punktX+r*orthvektorX ==> (AX+t*vektorABX-punktX)/orthvektorX = r
        //(AX+t*vektorABX-punktX)/orthvektorX = r
        //r in lotgerade einsetzen
        //punkt-schnittpunkt
        //länge vektor

        //DETERMINANTE:|a b|
        //             |c d| = ad-cb


        //Vektor der Kante
        double vabX = ab.getVabX();
        double vabY = ab.getVabY();

        //Punkte des Balls
        double pX = ball.getBall().getCenterX();
        double pY = ball.getBall().getCenterY();

        //Vektor der Orthogonalen
        double vorthX = vabY;
        double vorthY = (-1) * vabX;

        //Determinanten für Cramers Regel werden errechnet
        //Cramers regel:x=|px-ax -orthvektorx| / |vektorABX -orthvektorx|   y=|vektorABX px-ax| /|vektorABX -orthvektorx|
        //                |py-ay -orthvektory|/  |vektorABY -orthvektory|     |vektorABY py-ay|/ |vektorABY -orthvektory|
        double detA = (vabX * (pY - ab.getA().y)) - (vabY * (pX - ab.getA().x));
        double detB = (vabX * ((-1) * vorthY)) - (vabY * ((-1) * vorthX));

        double r = detA / detB;

        //Schnittpunkt
        double x = pX + r * vorthX;
        double y = pY + r * vorthY;

        ab.setsPX(x);
        ab.setsPY(y);

        //Vektor Schnittpunkt und Kugel
        double vX = x - pX;
        double vY = y - pY;

        //falls punkt über den Kanten gibt distanz zurück
        if (checkSP(ab.getA(), x, y, vabX, vabY)) {

            return (Math.sqrt(Math.pow(vX, 2) + Math.pow(vY, 2))) - ball.getBall().getRadius();

        } else {

            return distancePoint(ball.getBall().getCenterX(), ball.getBall().getCenterY(), ab.getA()) - ball.getBall().getRadius();
        }

    }

    //checkt ob schnittpunkt zwischen den Eckpunkten liegt
    public boolean checkSP(Point a, double spX, double spY, double vabX, double vabY) {


        double tX;
        double tY;

        //setzt Schnittpunkt ein um t zu berechnen
        if (vabX != 0) {
            tX = (spX - a.x) / vabX;
        } else {
            tX = 0;
        }
        if (vabY != 0) {
            tY = (spY - a.y) / vabY;
        } else {
            tY = 0;
        }

        //Prüft ob t zwischen 0 und 1
        if (tX <= 1 && tX >= 0 && tY <= 1 && tY >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void kollisionBall(Ball ball1, Ball ball2) {

        ball1.getPhysics().calcSpeed();
        ball2.getPhysics().calcSpeed();

        if(ball1.getPhysics().getV()<=0.1&&ball2.getPhysics().getV()<=0.1){

            ball1.getPhysics().setV0X(0);
            ball1.getPhysics().setV0Y(0);
            ball2.getPhysics().setV0X(0);
            ball2.getPhysics().setV0Y(0);
        }else {

            //Punkte der Kugeln
            double p1X = ball1.getBall().getCenterX();
            double p1Y = ball1.getBall().getCenterY();

            double p2X = ball2.getBall().getCenterX();
            double p2Y = ball2.getBall().getCenterY();

            //Vektor zwischen punkten
            double vx = p2X - p1X;
            double vy = p2Y - p1Y;

            //Länge Vektor v
            double vLength = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));

            //Einheitsvektor v
            double vEinX = vx / vLength;
            double vEinY = vy / vLength;

            //NormalenVektor
            double nx = vEinY * (-1);
            double ny = vEinX;

            //Einfallsvektor ball1
            double e1X = ball1.getPhysics().getV0X();
            double e1Y = ball1.getPhysics().getV0Y();

            //Einfallsvektor ball2
            double e2X = ball2.getPhysics().getV0X();
            double e2Y = ball2.getPhysics().getV0Y();

            //Anteile Einfallsvektor ball1 (r=Anteil in Normalenrichtung (Berührungsebene), u=Anteil in Stoßnormalenrichtung)
            double r1 = (e1X * nx) + (e1Y * ny);
            double u1 = (e1X * vEinX) + (e1Y * vEinY);

            //Anteile Einfallsvektor ball2
            double r2 = (e2X * nx) + (e2Y * ny);
            double u2 = (e2X * vEinX) + (e2Y * vEinY);

            //Massen der Kugeln
            double m1 = ball1.getPhysics().getM();
            double m2 = ball2.getPhysics().getM();

            //Anwenden des Elastischen Stoßes
            double u1New = (m1 * u1 + m2 * (2 * u2 - u1)) / (m2 + m1);
            double u2New = (m2 * u2 + m1 * (2 * u1 - u2)) / (m2 + m1);

            //Energieverlust
            u1New = calcEnergyLossBall(u1New, ball1);
            u2New = calcEnergyLossBall(u2New, ball2);

            //Geschwindigkeit wird in Anteile in x und y Richtung zerlegt
            double u1X = u1New * vEinX;
            double u1Y = u1New * vEinY;

            double u2X = u2New * vEinX;
            double u2Y = u2New * vEinY;

            //neuen Ausfallsvektoren werden berechnet
            double a1x = u1X + r1 * nx;
            double a1y = u1Y + r1 * ny;

            double a2x = u2X + r2 * nx;
            double a2y = u2Y + r2 * ny;

            ball1.getPhysics().setV0X(a1x);
            ball1.getPhysics().setV0Y(a1y);
            ball2.getPhysics().setV0X(a2x);
            ball2.getPhysics().setV0Y(a2y);
        }

    }

    public void kollisionObstacle(Ball ball, Edge ab){


        //t auch als einheitsvektor
        //n= normale
        //t= einheitsvektor tangente
        //e=einfallsvektor
        //a=ausfallsvektor
        //e= r*n+u*t
        //a= -r*n+u*t
        //r=v*n
        //u=v*t

        //Tangente
        double tx=ab.getVEinX();
        double ty=ab.getVEinY();

        //Normale
        double nx=ab.getVNormX();
        double ny=ab.getVNormY();

        //Einfallsvektor
        double ex=ball.getPhysics().getV0X();
        double ey=ball.getPhysics().getV0Y();

        //Anteile des Einfallsvektors
        double r=(ex*nx)+(ey*ny);
        double u=(ex*tx)+(ey*ty);

        boolean testRoll;

        double rBetrag=Math.abs(r);

        System.out.println("R: "+r);



        //Prüft ob Rollthreshhold unterschritten
        if(rBetrag<=30){
            testRoll= false;
            r=0;
        }else{
            testRoll= true;
        }


        if(testRoll){

            //elastischer Stoß
            ball.setState(StateE.ISFALLING);

            //Energieverlust berechnet
            double vx=calcEnergyLossObstacle(r,nx,ball);
            double vy=calcEnergyLossObstacle(r,ny,ball);

            //Ausfallsvektor
            double ax=(-1)*vx+u*tx;
            double ay=(-1)*vy+u*ty;

            ball.getPhysics().setV0X(ax);
            ball.getPhysics().setV0Y(ay);

        }else{


            ball.setState(StateE.ISROLLING);
            //aG = Beschleunigung der Kugel
            //aN = anteil der gRavitation in normalrichtung
            //aH = anteil Gravitation in Hangabtriebsrichtung
            //aR = aN*faktor

            double aGx=ball.getPhysics().getAccelerationX();
            double aGy=ball.getPhysics().getAccelerationY();

            double aN=(aGx*nx)+(aGy*ny);
            double aH=(aGx*tx)+(aGy*ty);

            //muss gegen bewegungsrichtung
            //wenn a gesamt 0 ist ausschalten
            double aR=aN*0.02;

            //Anteil der Hangabtriebsgeschwindigkeit
            double vx=u*tx;
            double vy=u*ty;

            double bewegungsrichtung=vx*tx+vy*ty;
            ball.getPhysics().calcSpeed();

            if(ball.getPhysics().getV() <= 0.1&&aH==0) {
                aR = 0;
                vx = 0;
                vy = 0;

            }else if(bewegungsrichtung<0){
                aR=aR*(-1);

            }


            double ax=(aH*tx+aR*tx);
            double ay=(aH*ty+aR*ty);

            ball.getPhysics().setAccelerationX(ax);
            ball.getPhysics().setAccelerationY(ay);

            ball.getPhysics().setV0X(vx);
            ball.getPhysics().setV0Y(vy);

        }

    }


    public void kollisionAufzug(Ball ball, Edge ab, Aufzug aufzug, double distance){


        //t auch als einheitsvektor
        //n= normale
        //t= einheitsvektor tangente
        //e=einfallsvektor
        //a=ausfallsvektor
        //e= r*n+u*t
        //a= -r*n+u*t
        //r=v*n
        //u=v*t

        //Tangente
        double tx=ab.getVEinX();
        double ty=ab.getVEinY();

        //Normale
        double nx=ab.getVNormX();
        double ny=ab.getVNormY();

        //Einfallsvektor
        double ex=ball.getPhysics().getV0X();
        double ey=ball.getPhysics().getV0Y();

        //Anteile des Einfallsvektors
        double r=(ex*nx)+(ey*ny);
        double u=(ex*tx)+(ey*ty);



        double rBetrag=Math.abs(r);


        //Es wird überprüft, ob die Kollisionskante die Obere des Aufzugs ist
        if(ab==aufzug.getEAB()){

            int check=0;

            boolean testV=false;

            //Anhand eines Threshholds wird geprüft ob der Ball vom Aufzug hochgehoben wird
            if((aufzug.getStart()&&(aufzug.getvYOld()+5>=ball.getPhysics().getV0Y())&&(aufzug.getvYOld()-5<=ball.getPhysics().getV0Y()))||(aufzug.getStart()&&Math.abs(ball.getPhysics().getV0Y())<=30)){
                testV=true;
            }

            //möglichen Kollisionsfälle werden abgefragt
            if(testV){
                check=1;
                r=0;
            }else if(aufzug.getStart()){
                check=2;
            }else if(rBetrag<=30){
                check=3;
                r=0;
            }else{
                check=4;
            }

            switch(check){
                case 1:  //KUGEL ROLLT AUF BEWEGENDEM AUFZUG

                    //Übernimmt Teile aus dem Code der Obstacle Rollkollision

                    //aG = Beschleunigung der Kugel
                    //aN = anteil der gRavitation in normalrichtung
                    //aH = anteil Gravitation in Hangabtriebsrichtung
                    //aR = aN*faktor

                    aufzug.addM(ball.getPhysics().getM());

                    ball.getBall().setCenterY(ab.getA().y-ball.getRadius());

                    double aGx=ball.getPhysics().getAccelerationX();
                    double aGy=ball.getPhysics().getAccelerationY();

                    double aN=(aGx*nx)+(aGy*ny);
                    double aH=(aGx*tx)+(aGy*ty);

                    //muss gegen bewegungsrichtung
                    //wenn a gesamt 0 ist ausschalten
                    double aR=aN*0.02;

                    //Anteil der Hangabtriebsgeschwindigkeit
                    double vx=u*tx;
                    double vy=u*ty;

                    double bewegungsrichtung=vx*tx+vy*ty;
                    ball.getPhysics().calcSpeed();

                    if(ball.getPhysics().getV() <= 0.1&&aH==0) {
                        aR = 0;
                        vx = 0;
                        vy = 0;

                    }else if(bewegungsrichtung<0){
                        aR=aR*(-1);

                    }


                    double ax=(aH*tx+aR*tx);
                    double ay=(aH*ty+aR*ty);

                    ball.getPhysics().setAccelerationX(ax);
                    ball.getPhysics().setAccelerationY(ay);

                    ball.getPhysics().setV0X(vx);
                    ball.getPhysics().setV0Y(vy);

                    //Abschließend wird die Bewegung des Aufzugs zum Ball hinzugefügt
                    ball.getPhysics().setV0Y(aufzug.getVY());

                    ball.setState(StateE.ISAUFZUG);


                    break;


                case 2:  //KUGEL FÄLLT AUF BEWEGENDEN AUFZUG

                    //Übernimmt Teile aus dem Code der Obstacle Stoßkollision und der Ballkollision

                    //mBall Masse der Kugel
                    //mAufzug Masse des Aufzugs
                    //rAufzug Geschwindigkeit des Aufzugs
                    //r geschwindigkeit der Murmel in y richtung


                    double mBall = ball.getPhysics().getM();
                    double mAufzug = aufzug.getM();

                    double rAufzug = aufzug.getVY();
                    double rBall = ball.getPhysics().getV0Y();

                    //Formel für den elastischen Stoß
                    double rBallNew = ((mBall * rBall + mAufzug * (2 * rAufzug - rBall)) / (mAufzug + mBall));
                    double rAufzugNew = ((mAufzug * rAufzug + mBall * (2 * rBall - rAufzug)) / (mAufzug + mBall));

                    rBallNew = calcEnergyLossBall(rBallNew, ball);
                    rAufzugNew = calcEnergyLossAufzug(rAufzugNew, aufzug);

                    aufzug.setvYOld(rAufzugNew);

                    ball.getPhysics().setV0Y(rBallNew);

                    ball.setState(StateE.ISFALLING);


                    break;


                case 3:  //KUGEL ROLLT AUF STEHENDEM AUFZUG

                    //Aufzug wird als Obstacle behandelt

                    //aG = Beschleunigung der Kugel
                    //aN = anteil der gRavitation in normalrichtung
                    //aH = anteil Gravitation in Hangabtriebsrichtung
                    //aR = aN*faktor

                    double aGx3=ball.getPhysics().getAccelerationX();
                    double aGy3=ball.getPhysics().getAccelerationY();

                    double aN3=(aGx3*nx)+(aGy3*ny);
                    double aH3=(aGx3*tx)+(aGy3*ty);


                    //muss gegen bewegungsrichtung
                    //wenn a gesamt 0 ist ausschalten
                    double aR3=aN3*0.02;




                    //Anteil der Hangabtriebsgeschwindigkeit
                    double vx3=u*tx;
                    double vy3=u*ty;

                    double bewegungsrichtung3=vx3*tx+vy3*ty;
                    ball.getPhysics().calcSpeed();

                    if(ball.getPhysics().getV() <= 0.1&&aH3==0) {
                        aR3 = 0;
                        vx3 = 0;
                        vy3 = 0;

                    }else if(bewegungsrichtung3<0){
                        aR3=aR3*(-1);

                    }

                    double ax3=(aH3*tx+aR3*tx);
                    double ay3=(aH3*ty+aR3*ty);

                    ball.getPhysics().setAccelerationX(ax3);
                    ball.getPhysics().setAccelerationY(ay3);

                    ball.getPhysics().setV0X(vx3);
                    ball.getPhysics().setV0Y(vy3);

                    ball.setState(StateE.ISROLLING);

                    break;


                case 4:  //KUGEL FÄLLT AUF STEHENDEN AUFZUG

                    //Aufzug wird als Obstacle behandelt


                    //elastischer Stoß

                    //Energieverlust berechnet
                    double vx4=calcEnergyLossObstacle(r,nx,ball);
                    double vy4=calcEnergyLossObstacle(r,ny,ball);

                    //Ausfallsvektor
                    double ax4=(-1)*vx4+u*tx;
                    double ay4=(-1)*vy4+u*ty;

                    ball.getPhysics().setV0X(ax4);
                    ball.getPhysics().setV0Y(ay4);

                    ball.setState(StateE.ISFALLING);

                    break;
                default:
                    System.out.println("ERROR");
                    break;
            }

        }else{

            //Kollision für übrigen drei Kanten des Aufzugs werden wie Obstacle Kollisionen gehandelt

            //elastischer Stoß
            ball.setState(StateE.ISFALLING);

            //Energieverlust berechnet
            double vx=calcEnergyLossObstacle(r,nx,ball);
            double vy=calcEnergyLossObstacle(r,ny,ball);

            //Ausfallsvektor
            double ax=(-1)*vx+u*tx;
            double ay=(-1)*vy+u*ty;

            ball.getPhysics().setV0X(ax);
            ball.getPhysics().setV0Y(ay);

        }


    }

    //berechnet Energieverlust bei Kugelkollision
    public double calcEnergyLossBall(double v, Ball ball){

        boolean negative = true;

        //vorzeichen von v wird gespeichert
        if((v)>=0){
            negative=false;
        }

        //Energie wird berechnet und 30% werden abgezogen
        double ekin=(0.5*ball.getPhysics().getM()*Math.pow(v,2))*0.7;

        //v wird erneut berechnet
        v= Math.sqrt((ekin)/(0.5*ball.getPhysics().getM()));

        //vorzeichen wird ggf angewendet
        if(negative){
            v = v*(-1);
        }

        return v;
    }

    public double calcEnergyLossAufzug(double v, Aufzug aufzug){
        boolean negative = true;

        //vorzeichen von v wird gespeichert
        if((v)>=0){
            negative=false;
        }

        //Energie wird berechnet und 30% werden abgezogen
        double ekin=(0.5*aufzug.getM()*Math.pow(v,2))*0.7;

        //v wird erneut berechnet
        v= Math.sqrt((ekin)/(0.5*aufzug.getM()));

        //vorzeichen wird ggf angewendet
        if(negative){
            v = v*(-1);
        }

        return v;
    }

    //berechnet Energieverlust bei Wandkollision
    public double calcEnergyLossObstacle(double r, double n, Ball ball){

        boolean negative = true;
        double v=n*r;

        //vorzeichen von v wird gespeichert
        if((v)>=0){
            negative=false;
        }

        //Energie wird berechnet und 30% werden abgezogen
        double ekin=(0.5*ball.getPhysics().getM()*Math.pow(v,2))*0.7;

        //v wird erneut berechnet
        v= Math.sqrt((ekin)/(0.5*ball.getPhysics().getM()));

        //vorzeichen wird ggf angewendet
        if(negative){
            v = v*(-1);
        }

        return v;
    }

    //scheuen ob normalenanteil positiv ist
    //wenn ja fliegt von kante weg
    //retruns true bei kollisionskurs
    public boolean testDirectionObstacle(Ball ball, Edge ab){

        double vx=ball.getPhysics().getV0X();
        double vy=ball.getPhysics().getV0Y();

        double nx=ab.getVNormX();
        double ny=ab.getVNormY();

        double r=(vx*nx)+(vy*ny);

        if(r<=0.01){
            return true;
        }else{
            return false;
        }

    }

    //Für die Richtungsbestimmung beim Aufzug wird Geschwindigkeit und Richtung des Aufzugs mitbeachtet
    public boolean testDirectionAufzug(Ball ball, Aufzug aufzug){


        double v1y=ball.getPhysics().getV0Y();

        double v2y=aufzug.getVY();

        if((v1y>=0&&v2y<=0)||(v1y>0&&v2y>0&&Math.abs(v1y)>Math.abs(v2y))||(v1y<0&&v2y<0&&Math.abs((v1y))<Math.abs(v2y))){
            return true;
        }else{
            return false;
        }

    }

    public boolean testDirectionBall(Ball ball1, Ball ball2){

        //Richtungsvektoren der Kugeln
        double v1x=ball1.getPhysics().getV0X();
        double v1y=ball1.getPhysics().getV0Y();

        double v2x=ball2.getPhysics().getV0X();
        double v2y=ball2.getPhysics().getV0Y();

        //Vektor zwischen mittelpunkten
        double dx=ball2.getBall().getCenterX()-ball1.getBall().getCenterX();
        double dy=ball2.getBall().getCenterY()-ball1.getBall().getCenterY();

        double dLength=Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));

        //Einheitsvektor zwischen Kugeln
        double dEinX=dx/dLength;
        double dEinY=dy/dLength;

        double r1=(dEinX*v1x)+(dEinY*v1y);
        double r2=(dEinX*v2x)+(dEinY*v2y);

        //Prüft ob Murmeln aufeinenader zu fliegen, oder sich verfolgen
        if((r1>=0&&r2<=0)||(r1>0&&r2>0&&Math.abs(r1)>Math.abs(r2))||(r1<0&&r2<0&&Math.abs(r1)<Math.abs(r2))){
            return true;
        }else{
            return false;
        }

    }

    public boolean testThreshholdBall(Ball ball1, Ball ball2, double distance){

        //Richtungsvektoren der Kugeln
        double v1x=ball1.getPhysics().getV0X();
        double v1y=ball1.getPhysics().getV0Y();

        double v2x=ball2.getPhysics().getV0X();
        double v2y=ball2.getPhysics().getV0Y();

        //Vektor zwischen mittelpunkten
        double dx=ball2.getBall().getCenterX()-ball1.getBall().getCenterX();
        double dy=ball2.getBall().getCenterX()-ball1.getBall().getCenterX();

        double dLength=Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));

        //Einheitsvektor zwischen Kugeln
        double dEinX=dx/dLength;
        double dEinY=dy/dLength;

        double r1=(dEinX*v1x)+(dEinY*v1y);
        double r2=(dEinX*v2x)+(dEinY*v2y);

        if(distance<=Math.abs(r1)/150||distance<=Math.abs(r2)/150){
            return true;
        }else{
            return false;
        }
    }

    //Überprüft Abstandsthreshhold in Abhängigkeitvon Normalanteil des Geschwindigkeitsvektors in bezug zur Ebene
    public boolean testThreshholdObstacle(Ball ball, Edge ab, double distance){

        double vx=ball.getPhysics().getV0X();
        double vy=ball.getPhysics().getV0Y();

        double nx=ab.getVNormX();
        double ny=ab.getVNormY();

        double r=(vx*nx)+(vy*ny);

        if(distance<=r/150){

            return true;
        }else{

            return false;
        }

    }

    //Überprüft Abstandsthreshhold in Abhängigkeitvon Normalanteil des Geschwindigkeitsvektors in bezug zur Ebene
    public boolean testThreshholdAufzug(Ball ball, Edge ab, double distance){

        double vx=ball.getPhysics().getV0X();
        double vy=ball.getPhysics().getV0Y();

        double nx=ab.getVNormX();
        double ny=ab.getVNormY();

        double r=(vx*nx)+(vy*ny);

        if(distance<=r/150){

            return true;
        }else{

            return false;
        }

    }



    public double distancePoint(double x, double y, Point p2) {


        double vX = x-p2.x;
        double vY = y-p2.y;
        return Math.sqrt(Math.pow(vX, 2) + Math.pow(vY, 2));

    }

}

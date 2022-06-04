package sample;

public class Gravitation {

    double grav;
    double gravOld;


    public Gravitation(double grav){
        this.grav = grav*Konstante.DISTANCEFACTOR;
        this.gravOld=0;
    }

    public void setGrav(double grav){
        this.gravOld=this.grav;
        this.grav=grav*Konstante.DISTANCEFACTOR;
    }

    public double getGrav(){
        return grav;
    }

    public double getGravOld(){
        return gravOld;
    }

}

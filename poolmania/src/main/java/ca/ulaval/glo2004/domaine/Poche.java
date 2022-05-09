package ca.ulaval.glo2004.domaine;

import java.awt.*;
import java.awt.geom.Point2D;

public class Poche extends ObjetTable {

    public Poche(Point2D position, double rayon) {
        this.position = position;
        this.boundingBox = new Cercle(position, rayon);
    }

    public Cercle getBoundingBox(){
        return (Cercle) this.boundingBox;
    }
}

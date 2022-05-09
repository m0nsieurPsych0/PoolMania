package ca.ulaval.glo2004.domaine.DTO;

import java.awt.geom.Point2D;

public class PocheDTO {

    private Point2D position;
    private double rayon;

    public PocheDTO(Point2D position, double rayon) {
        this.position = position;
        this.rayon = rayon;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public double getRayon() {
        return rayon;
    }

    public void setRayon(double rayon) {
        this.rayon = rayon;
    }
}

package ca.ulaval.glo2004.domaine.DTO;

import java.awt.geom.Point2D;

public class MurDTO {

    private Point2D position;
    private double angle;
    private double longueur;
    private double epaisseur;
    private boolean murContour;

    public MurDTO(Point2D position, double angle, double longueur, double epaisseur, boolean murContour) {
        this.position = position;
        this.angle = angle;
        this.longueur = longueur;
        this.epaisseur = epaisseur;
        this.murContour = murContour;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public double getEpaisseur() {
        return epaisseur;
    }

    public void setEpaisseur(double epaisseur) {
        this.epaisseur = epaisseur;
    }

    public boolean isMurContour() {
        return murContour;
    }

    public void setMurContour(boolean murContour) {
        this.murContour = murContour;
    }

    public Point2D getPositionCentre() {
        double positionCentreX = position.getX()+(longueur/2)*Math.cos(angle);
        double positionCentreY = position.getY()+(longueur/2)*Math.sin(angle);

        return new Point2D.Double(positionCentreX, positionCentreY);
    }
}

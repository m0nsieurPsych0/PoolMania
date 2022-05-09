package ca.ulaval.glo2004.domaine.DTO;

import java.awt.geom.Point2D;

public class PortailDTO {

    private Point2D position;
    private double angle;
    private double longueur;
    private boolean couleurBleu;

    public PortailDTO(Point2D position, double angle, double longueur, boolean couleur) {
        this.position = position;
        this.angle = angle;
        this.longueur = longueur;
        this.couleurBleu = couleur;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public double getAngle() { return angle; }

    public void setAngle(double angle) { this.angle = angle; }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public boolean getCouleurPortail() { return couleurBleu; }

    public void setCouleurPortail(boolean couleur) { this.couleurBleu = couleur; }
}

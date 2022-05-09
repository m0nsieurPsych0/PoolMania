package ca.ulaval.glo2004.domaine;

import java.awt.geom.Point2D;

public class Portail extends ObjetTable {

    double angle;
    double longueur;

    boolean couleurBleu;

    public Portail(Point2D position, double angle, double longueur, boolean couleurBleu) {
        this.position = position;
        this.angle = angle;
        this.longueur = longueur;
        this.couleurBleu = couleurBleu;
        this.boundingBox = new Cercle(position, longueur);
    }

    public double getAngle() { return angle; }

    public void setAngle(double angle) { this.angle = angle; }

    public double getLongueur() { return longueur; }

    public void setLongueur(double longueur) { this.longueur = longueur; }

    public boolean getCouleurPortail() { return couleurBleu; }

    public void setCouleurPortail(boolean couleur) { this.couleurBleu = couleur; }

    public Cercle getBoundingBox(){
        return (Cercle) this.boundingBox;
    }
}

package ca.ulaval.glo2004.domaine.DTO;

import java.awt.*;
import java.awt.geom.Point2D;

public class BouleDTO {

    private Point2D position;
    private double rayon;
    private Color couleur;
    private int numero;

    private boolean bouleBlancheOriginale;

    public BouleDTO(Point2D position, double rayon, Color couleur, int numero, boolean bouleBlancheOriginale) {
        this.position = position;
        this.rayon = rayon;
        this.couleur = couleur;
        this.numero = numero;
        this.bouleBlancheOriginale = bouleBlancheOriginale;
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

    public void setNumero(int numero){
        this.numero = numero;
    }

    public int getNumero(){
        return numero;
    }

    public void setRayon(double rayon) {
        this.rayon = rayon;
    }

    public Color getCouleur() {
        return couleur;
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }

    public boolean isBouleBlancheOriginale() {
        return bouleBlancheOriginale;
    }

    public void setBouleBlancheOriginale(boolean bouleBlancheOriginale) {
        this.bouleBlancheOriginale = bouleBlancheOriginale;
    }
}

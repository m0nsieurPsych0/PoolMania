package ca.ulaval.glo2004.domaine;

import java.awt.*;
import java.awt.geom.Point2D;

public class BouleColoree extends Boule {

    private Color couleur;
    private int numero;

    public BouleColoree(Point2D position, double rayon, Color couleur, int numero) {
        super(position, rayon);
        this.couleur = couleur;
        this.numero = numero;
    }

    @Override
    public Color getCouleur(){
        return couleur;
    }

    public void setCouleur(Color couleur) { this.couleur = couleur; }

    public int getNumero(){
        return numero;
    }

    public void setNumero(int numero){
        this.numero = numero;
    }
}

package ca.ulaval.glo2004.domaine;

import java.awt.*;
import java.awt.geom.Point2D;

public class Boule extends ObjetTable {

    private double vitesseX;
    private double vitesseY;
    private Portail dernierPortailSorti;

    public Boule(Point2D position, double rayon) {
        this.position = position;
        this.boundingBox = new Cercle(position, rayon);
        vitesseX = 0;
        vitesseY = 0;
    }

    public Cercle getBoundingBox(){
        return (Cercle) boundingBox;
    }

    public Color getCouleur(){
        return Color.white;
    }

    public int getNumero(){
        return 0;
    }

    public double getVitesseX() { return vitesseX; }

    public double getVitesseY() { return vitesseY; }

    //la vitesse est en cm/s
    public void setVitesse(double nouvelleVitesseX, double nouvelleVitesseY) {
        if(vitesseX == 0 && vitesseY == 0) {
            this.vitesseX = nouvelleVitesseX;
            this.vitesseY = nouvelleVitesseY;
        }else{
            this.vitesseX = nouvelleVitesseX;
            this.vitesseY = nouvelleVitesseY;
        }
    }

    public double getTailleVitesse(){
        return Math.sqrt(Math.pow(vitesseY, 2) + Math.pow(vitesseX, 2));
    }
    public void translateVitesse(){
        //pour 1 ms
        Point2D vecteurV = new Point2D.Double(vitesseX/1000, vitesseY/1000);
        boundingBox.updatePosition(position, new Point2D.Double(position.getX() + vecteurV.getX(), position.getY() + vecteurV.getY()));
        position.setLocation(position.getX() + vecteurV.getX(), position.getY() + vecteurV.getY());
    }

    public void updateVitesse(double frictionCin, double poidsBoules) {
        if(vitesseX != 0 || vitesseY != 0) {
            double forceNormale = poidsBoules * 9.81;
            double forceFrottement = forceNormale * frictionCin;
            //en cm/s^2 mais /1000 pour calculer pour 1ms
            double acceleration = ((forceFrottement*100) / (poidsBoules))/1000;
            double angle = Math.atan2(vitesseY, vitesseX);
            if (angle < 0) {
                angle += Math.PI * 2;
            }
            if (vitesseX > 0) {
                if(vitesseX > acceleration * Math.cos(angle)) {
                    vitesseX -= acceleration * Math.cos(angle);
                }else{
                    vitesseX = 0;
                }
            }else if(vitesseX < 0){
                if(vitesseX < acceleration * Math.cos(angle)) {
                    vitesseX -= acceleration * Math.cos(angle);
                }else{
                    vitesseX = 0;
                }
            }


            if (vitesseY > 0) {
                if(vitesseY > acceleration * Math.sin(angle)) {
                    vitesseY -= acceleration * Math.sin(angle);
                }else{
                    vitesseY = 0;
                }
            }else if(vitesseY < 0){
                if(vitesseY < acceleration * Math.sin(angle)) {
                    vitesseY -= acceleration * Math.sin(angle);
                }else{
                    vitesseY = 0;
                }
            }
            
        }
    }

    public Portail getDernierPortailSorti() { return dernierPortailSorti; }

    public void setDernierPortailSorti(Portail portail) { dernierPortailSorti = portail; }
}

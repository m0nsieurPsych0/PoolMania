package ca.ulaval.glo2004.domaine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BrisureMur extends ObjetTable {

    private ArrayList<Mur> mursConnectes;

    public BrisureMur(Point2D position, Mur mur1, Mur mur2) {
        this.position = position;
        this.mursConnectes = new ArrayList<Mur>();
        mursConnectes.add(mur1);
        mursConnectes.add(mur2);
        this.boundingBox = new Cercle(position, mur1.getEpaisseur()/4);
    }

    public Mur getMur1(){
        return mursConnectes.get(1);
    }

    public Cercle getBoundingBox() {
        return (Cercle) boundingBox;
    }

    public ArrayList<Mur> getMursConnectes() {
        return mursConnectes;
    }
}

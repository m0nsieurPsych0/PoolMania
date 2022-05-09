package ca.ulaval.glo2004.domaine.dessinateur;

import ca.ulaval.glo2004.domaine.ControleurPoolMania;

import java.awt.*;

public class DessinateurMesure{
    private ControleurPoolMania controleur;
    private double scale;
    private Rectangle rectPan;
    private double width, height;
    private boolean metrique, grilleActive;
    private double tailleCase;
    private double facteur;
    private String mesurePourCase = "Une face de carré de la grille mesure: ";

    public DessinateurMesure(ControleurPoolMania controleur) {
        this.controleur = controleur;
        updateVariables();
    }


    public void dessinerCoin(Graphics g){

        updateVariables();
        if(metrique){
            g.drawString("cm",10, 20);
        }
        else{
            g.drawString("po",10, 20);
        }
    }
    public void dessinerGauche(Graphics g){
        updateVariables();
        if(metrique) {
            int modulo = (int) (facteur/(scale));
            if(modulo < 1){
                modulo = 1;
            }
            for (int i = 0 ; i < height; i += modulo *  tailleCase/facteur) {
                if(i != 0) {
                    g.drawLine(0, (int) ( i * scale), 3, (int) (i * scale));
                    g.drawString("" + (i), 6, (int) (i * scale) + 3);
                }
            }
        }
        else{
            int modulo = (int) (facteur*2.54/(scale));
            if(modulo < 1){
                modulo = 1;
            }
            for (int i = 0; i <  height; i += modulo *  tailleCase/(facteur *2.54)) {
                if(i != 0) {
                    g.drawLine(0, (int) (i * 2.54 * scale), 3, (int) (i * 2.54 * scale));
                    g.drawString("" +  (i), 6, (int) (i * scale *2.54) + 3);
                }
            }
        }
    }

    public void dessinerHaut(Graphics g){

        updateVariables();
        if(metrique) {

            int modulo = (int) (facteur/(scale));

            if(modulo < 1){
                modulo = 1;
            }
            for (int i = 0; i < width; i += modulo * tailleCase/facteur) {
                if(i != 0) {
                    g.drawLine((int) ((rectPan.x + i) * scale), 0, (int) ((rectPan.x + i) * scale), 3);
                    g.drawString("" + (rectPan.x + i), (int) ((rectPan.x + i) * scale) - 10, 16);
                }
            }


        }
        else{
            int modulo = (int) (facteur*2.54/(scale));

            if(modulo < 1){
                modulo = 1;
            }
            for (int i = 0; i < width;
                 i += modulo * tailleCase/(facteur*2.54)) {
                if(i != 0) {
                    g.drawLine(rectPan.x + (int) (i * 2.54 * scale), 0, rectPan.x + (int) (i * 2.54 * scale), 3);
                    g.drawString("" + i, (int) (i * 2.54 * scale) - 10, 16);
                }
            }
        }
    }

    private void updateVariables(){
        if(controleur.getMetrique()){
            facteur = 50/controleur.getTailleCase();
        }
        else{
            facteur = 50/controleur.getTailleCase() /2.54;
        }
        tailleCase = controleur.getTailleCase()*facteur;
        grilleActive = controleur.getBoolGrilleActive();
        metrique = controleur.getMetrique();
        scale = controleur.getScale();
        rectPan = controleur.getPanneauDessinBounds();
        width =  rectPan.getWidth() / scale;
        height = rectPan.getHeight() / scale;
    }

    public double getMesureCase(){
        return tailleCase/scale;
    }

}

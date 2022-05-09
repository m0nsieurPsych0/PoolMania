package ca.ulaval.glo2004.gui.EcouteurEvt.Simulation;

import ca.ulaval.glo2004.gui.EcouteurEvt.Edition.EditionPannDessEvn;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.gui.Panneau.PanneauDessin;
import ca.ulaval.glo2004.gui.Panneau.PanneauOutilsSimulation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


public class EditionPannDessSimEvn extends EditionPannDessEvn {


    private PanneauOutilsSimulation panneauOutilsSimulation;

    public EditionPannDessSimEvn(MainWindow fenetrePrincipale, PanneauDessin panneauDessin, PanneauOutilsSimulation panneauOutilsSimulation) {
        super(fenetrePrincipale, panneauDessin);
        this.panneauOutilsSimulation = panneauOutilsSimulation;
    }

    @Override
    public void PanneauDessinMouseClicked(MouseEvent evt) {
        if(fenetrePrincipale.getControleur().bouleBlanchePresente()){
            if(fenetrePrincipale.ballesImmobiles() && !fenetrePrincipale.getAnimationFrappe()) {
                fenetrePrincipale.setAnimationFrappe();
            }
        } else if(fenetrePrincipale.ballesImmobiles()){
            fenetrePrincipale.getControleur().ajouterBouleBlanche(evt.getPoint());
        }
    }


    @Override
    public void PanneauDessinMousePressed(MouseEvent evt){
        mousePoint = evt.getPoint();
    }

    @Override
    public void PanneauDessinMouseDragged(MouseEvent evt) {
        setCoordStatusBar(evt);
        bougerLeplan(evt);
    }

    @Override
    public void PanneauDessinMouseWheelMoved(MouseWheelEvent evt){
        zoomEvent(evt);
        if(!evt.isControlDown() && !evt.isShiftDown()) {
            if (evt.getWheelRotation() == 1) {
                fenetrePrincipale.setForceTir(fenetrePrincipale.getForce() * 1.05);
            } else if (evt.getWheelRotation() == -1) {
                fenetrePrincipale.setForceTir(fenetrePrincipale.getForce() / 1.05);
            }
        }
    }
}

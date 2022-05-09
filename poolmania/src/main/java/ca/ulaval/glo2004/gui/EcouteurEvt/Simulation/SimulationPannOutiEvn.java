package ca.ulaval.glo2004.gui.EcouteurEvt.Simulation;

import ca.ulaval.glo2004.gui.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SimulationPannOutiEvn extends AbstractAction {

    private MainWindow fenetrePrincipale;

    public SimulationPannOutiEvn(MainWindow fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
    }

    public void frapperManuelle(ActionEvent e){
        fenetrePrincipale.getPanneauOutilsSim().setAucunProps();
        fenetrePrincipale.setBaguetteSouris(false);
        fenetrePrincipale.getPanneauOutilsSim().setPropsSimulationModifier();
    }

    public void baguetteSouris(ActionEvent e){
        fenetrePrincipale.getPanneauOutilsSim().setAucunProps();
        fenetrePrincipale.setBaguetteSouris(true);
        fenetrePrincipale.getPanneauOutilsSim().setPropsSimulation();
    }

    public void undoActionPerformed(ActionEvent evt) {
        fenetrePrincipale.getControleur().undo();
    }

    public void redoActionPerformed(ActionEvent evt) {
        fenetrePrincipale.getControleur().redo();
    }

    public void changerModeActionPerformed(ActionEvent evt) {
        if(fenetrePrincipale.ballesImmobiles() && fenetrePrincipale.getControleur().bouleBlanchePresente()) {
            fenetrePrincipale.getControleur().addState();
            fenetrePrincipale.setModeEdition(!fenetrePrincipale.getModeEdition());
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
    }
}

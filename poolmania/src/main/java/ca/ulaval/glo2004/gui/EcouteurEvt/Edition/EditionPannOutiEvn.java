package ca.ulaval.glo2004.gui.EcouteurEvt.Edition;

import ca.ulaval.glo2004.gui.MainWindow;

import javax.swing.*;
import java.awt.event.*;

public class EditionPannOutiEvn extends AbstractAction {

    private MainWindow fenetrePrincipale;

    public EditionPannOutiEvn(MainWindow fenetrePrincipale) { this.fenetrePrincipale = fenetrePrincipale; }

    public void selectionnerActionPerformed(ActionEvent evt) {
        fenetrePrincipale.setBoolSelectionnerActive();
        fenetrePrincipale.getPanneauOutils().setAucunProps();
        fenetrePrincipale.getPanneauOutils().setTextBoutonOK("Modifier");
        fenetrePrincipale.getPanneauOutils().resetMessage();
    }

    public void ajouterActionPerformed(ActionEvent evt) {
        fenetrePrincipale.setBoolAjouterActive();
        fenetrePrincipale.getPanneauOutils().setIndexObjetAAjouter(0);
        fenetrePrincipale.getPanneauOutils().setPropsBouleAjouter();
        fenetrePrincipale.getPanneauOutils().resetMessage();
    }

    public void supprimerActionPerformed(ActionEvent evt)
    {
        fenetrePrincipale.setBoolSupprimerActive();
        fenetrePrincipale.getPanneauOutils().setAucunProps();
        fenetrePrincipale.getPanneauOutils().resetMessage();
    }

    public void briserActionPerformed(ActionEvent evt) {
        fenetrePrincipale.setBoolBriserActive();
        fenetrePrincipale.getPanneauOutils().setAucunProps();
        fenetrePrincipale.getPanneauOutils().resetMessage();
    }

    public void propPhysiquesActionPerformed(ActionEvent evt){
        fenetrePrincipale.setPropPhysiquesActive();
        fenetrePrincipale.getPanneauOutils().setPropsPhysique();
        fenetrePrincipale.getPanneauOutils().resetMessage();
    }


    public void undoActionPerformed(ActionEvent evt) {
        fenetrePrincipale.getControleur().undo();
    }

    public void redoActionPerformed(ActionEvent evt) {
        fenetrePrincipale.getControleur().redo();
    }

    public void changerModeActionPerformed(ActionEvent evt) {
        selectionnerActionPerformed(evt);
        fenetrePrincipale.setModeEdition(!fenetrePrincipale.getModeEdition());
        if(fenetrePrincipale.getBoolGrilleActive()){
            fenetrePrincipale.setBoolGrilleActive();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
    }
}

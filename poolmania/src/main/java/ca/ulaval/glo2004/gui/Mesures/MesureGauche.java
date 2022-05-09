package ca.ulaval.glo2004.gui.Mesures;

import ca.ulaval.glo2004.domaine.ControleurPoolMania;

import java.awt.*;
import javax.swing.*;

public class MesureGauche extends JLabel{
    private ControleurPoolMania controleur;
    private Dimension preferedSize = new Dimension(50, 600);

    public MesureGauche(ControleurPoolMania controleur){
        this.controleur = controleur;
        this.setBackground(Color.white);
        this.setOpaque(true);
        this.setPreferredSize(preferedSize);
        this.setMinimumSize(preferedSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        controleur.dessinerGauche(g);
    }
}

package ca.ulaval.glo2004.gui.Panneau;

import ca.ulaval.glo2004.gui.EcouteurEvt.Simulation.EditionPannDessSimEvn;
import ca.ulaval.glo2004.utils.DimensionDomaine;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.gui.EcouteurEvt.Edition.EditionPannDessEvn;

import java.awt.event.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PanneauDessin extends JPanel implements MouseMotionListener, Runnable {

    private MainWindow fenetrePrincipale;
    private Dimension initialDimensions;
    private EditionPannDessEvn editionPannDessEvn;
    private EditionPannDessSimEvn editionPannDessSimEvn;
    private Timer timer;

    public PanneauDessin(MainWindow fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        editionPannDessEvn = new EditionPannDessEvn(fenetrePrincipale, this, fenetrePrincipale.getPanneauOutils());
        editionPannDessSimEvn = new EditionPannDessSimEvn(fenetrePrincipale, this, fenetrePrincipale.getPanneauOutilsSim());
        setInitialDimensions(DimensionDomaine.getInstance().getLargeurEcran(), DimensionDomaine.getInstance().getHauteurEcran());
        buildUp();
        ecouteur();
    }



    public void run(){
        repaint();
    }

    private void buildUp() {

//        this.setMinimumSize(new java.awt.Dimension(800, 400));
        this.setPreferredSize(initialDimensions);
        this.setVisible(true);
        final ScheduledExecutorService schedulerRepaint = Executors.newScheduledThreadPool(1);
        schedulerRepaint.scheduleAtFixedRate(this::run,1000/120, 1000/120, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        fenetrePrincipale.getControleur().dessinPanneau(g);
    }

    public Dimension getInitialDimensions() {
        return initialDimensions;
    }
    public void setInitialDimensions(int largeurEcran, int hauteurEcran) {
        initialDimensions = new Dimension(largeurEcran, hauteurEcran);
    }


    private void ecouteur() {
        // Events


        this.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent evt) {
                if(fenetrePrincipale.getModeEdition()) {
                    editionPannDessEvn.PanneauDessinMouseWheelMoved(evt);
                }else{
                    editionPannDessSimEvn.PanneauDessinMouseWheelMoved(evt);
                }            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if(fenetrePrincipale.getModeEdition()) {
                    editionPannDessEvn.PanneauDessinMouseClicked(evt);
                }else{
                    editionPannDessSimEvn.PanneauDessinMouseClicked(evt);
                }
            }

            public void mousePressed(MouseEvent evt) {
                if(fenetrePrincipale.getModeEdition()) {
                    editionPannDessEvn.PanneauDessinMousePressed(evt);
                }else{
                    editionPannDessSimEvn.PanneauDessinMousePressed(evt);
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if(fenetrePrincipale.getModeEdition()) {
                    editionPannDessEvn.PanneauDessinMouseReleased(evt);
                }else{
                    editionPannDessSimEvn.PanneauDessinMouseReleased(evt);
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                editionPannDessEvn.PanneauDessinComponentResized(evt);
            }
        });

        this.addMouseMotionListener(this);

    }
    @Override
    public void mouseDragged(MouseEvent evt) {
        if(fenetrePrincipale.getModeEdition()) {
            editionPannDessEvn.PanneauDessinMouseDragged(evt);
        }else{
            editionPannDessSimEvn.PanneauDessinMouseDragged(evt);
        }
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        if(fenetrePrincipale.getModeEdition()) {
            editionPannDessEvn.PanneauDessinMouseMoved(evt);
        }else{
            editionPannDessSimEvn.PanneauDessinMouseMoved(evt);
        }
    }


}
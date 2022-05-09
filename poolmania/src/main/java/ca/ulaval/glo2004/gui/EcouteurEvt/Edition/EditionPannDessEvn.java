package ca.ulaval.glo2004.gui.EcouteurEvt.Edition;

import ca.ulaval.glo2004.domaine.DTO.BouleDTO;
import ca.ulaval.glo2004.domaine.DTO.MurDTO;
import ca.ulaval.glo2004.domaine.DTO.PocheDTO;
import ca.ulaval.glo2004.domaine.DTO.PortailDTO;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.gui.Panneau.ColorPicker;
import ca.ulaval.glo2004.gui.Panneau.PanneauDessin;
import ca.ulaval.glo2004.gui.Panneau.PanneauOutils;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public class EditionPannDessEvn {

    protected MainWindow fenetrePrincipale;
    protected PanneauDessin panneauDessin;
    protected Point2D mousePoint = new Point2D.Double(0, 0);
    private boolean movingObject = false;
    private boolean objectSelected = false;
    private MainWindow.TypeObjetTable dernierTypeObjetTableSelectionne;
    private PanneauOutils panneauOutils;
    private Point2D evtRelative;
    private boolean wallStateIsValid = true;
    private int wallState = 0;

    //Pour EditionPannDessSimEvn
    protected EditionPannDessEvn(MainWindow fenetrePrincipale, PanneauDessin panneauDessin){
        this.fenetrePrincipale = fenetrePrincipale;
        this.panneauDessin = panneauDessin;
    }

    public EditionPannDessEvn(MainWindow fenetrePrincipale, PanneauDessin panneauDessin, PanneauOutils panneauOutils) {
        this.fenetrePrincipale = fenetrePrincipale;
        this.panneauDessin = panneauDessin;
        this.panneauOutils = panneauOutils;
    }

    public void PanneauDessinMouseClicked(MouseEvent evt) {

    }

    public void PanneauDessinMousePressed(MouseEvent evt) {
        mousePoint = evt.getPoint();
        manipulationObjet(evt);
    }

    public void PanneauDessinMouseReleased(MouseEvent evt) {
        if(movingObject){
            if(MainWindow.TypeObjetTable.Mur == dernierTypeObjetTableSelectionne){
                removeInterMurState();
            }
            fenetrePrincipale.getControleur().setDernierObjetTableSelectionne(evt.getPoint());
            fenetrePrincipale.getControleur().addState();
        }
        movingObject = false;

    }

    public void PanneauDessinMouseDragged(MouseEvent evt) {
        setCoordStatusBar(evt);
        deplacerObjet(evt);
        bougerLeplan(evt);
    }

    public void PanneauDessinMouseMoved(MouseEvent evt) {
        if(!fenetrePrincipale.getAnimationFrappe()){
            setMousePoint(evt.getPoint());
        }
        setCoordStatusBar(evt);
    }

    public void PanneauDessinComponentResized(ComponentEvent evt) {

//        System.out.println("RESIZED!");
        //todo mettre à jour les tailles de préférence pour certains éléments qui en dépendent.
//        Component source = (Component) evt.getSource();
//        System.out.println(source.getParent().getBounds());
    }

    public void PanneauDessinMouseWheelMoved(MouseWheelEvent evt) {
        zoomEvent(evt);
    }

    private void manipulationObjet(MouseEvent evt) {

        if (fenetrePrincipale.getBoolSelectionnerActive()) {
            selectObjet(evt);
        } else if (fenetrePrincipale.getBoolAjouterActive()) {
            dernierTypeObjetTableSelectionne = null;
            ajoutObjet(evt);
        } else if (fenetrePrincipale.getBoolBriserActive()) {
            dernierTypeObjetTableSelectionne = null;
            briserMur(evt);
        } else if (fenetrePrincipale.getBoolSupprimerActive()) {
            dernierTypeObjetTableSelectionne = null;
            supprimerObjet(evt);
        }
        else if (fenetrePrincipale.getPropPhysiquesActive()){
            dernierTypeObjetTableSelectionne = null;
        }
    }

    private void selectObjet(MouseEvent evt) {
        if (!movingObject) {

            panneauOutils.resetMessage();
            fenetrePrincipale.getControleur().setDernierObjetTableSelectionne(evt.getPoint());

            dernierTypeObjetTableSelectionne = fenetrePrincipale.getControleur().getTypeDernierObjetTableSelectionne();

            if (dernierTypeObjetTableSelectionne != null) {
                objectSelected = true;
                switch (dernierTypeObjetTableSelectionne) {
                    case Boule -> panneauOutils.setPropsBouleModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBouleDTO());
                    case Poche -> panneauOutils.setPropsPocheModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePocheDTO());
                    case BrisureMur -> panneauOutils.setPropsBrisureMurModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBrisureMurDTO());
                    case Mur -> panneauOutils.setPropsMurModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneMurDTO());
                    case Portail -> panneauOutils.setPropsPortailModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePortailDTO());
                }
            } else {
                objectSelected = false;
            }
        }
    }

    private void deplacerObjet(MouseEvent evt) {
        evtRelative = fenetrePrincipale.getControleur().getRelativeMousePoint(new Point2D.Double(evt.getX(), evt.getY()));
        evtRelative = fenetrePrincipale.getControleur().getGrilleMagnet(evtRelative);
        if (dernierTypeObjetTableSelectionne != null) {
            movingObject = true;

            switch (dernierTypeObjetTableSelectionne) {
                case Boule:

                    fenetrePrincipale.getControleur().deplacerBouleSansUndo(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBouleDTO().getPosition(), evtRelative);
                    panneauOutils.setPropsBouleModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBouleDTO());
                    break;
                case Poche:
                    fenetrePrincipale.getControleur().deplacerPocheSansUndo(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePocheDTO().getPosition(), evtRelative);
                    panneauOutils.setPropsPocheModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePocheDTO());
                    break;
                case Mur:
                    MurDTO mur = fenetrePrincipale.getControleur().getDernierObjetTableSelectionneMurDTO();
                    if (!fenetrePrincipale.getControleur().getDernierObjetTableSelectionneMurDTO().isMurContour()) {
                        // Position au centre du mur
                        evtRelative.setLocation((evtRelative.getX()-(mur.getLongueur()/2)*Math.cos(mur.getAngle())), (evtRelative.getY()-(mur.getLongueur()/2)*Math.sin(mur.getAngle())));
                        if(fenetrePrincipale.getControleur().deplacerMurObstacle(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneMurDTO().getPositionCentre(), evtRelative)){
                            wallState++;
                            wallStateIsValid = true;
                            panneauOutils.setPropsMurModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneMurDTO());
                        }
                        else{
                            wallStateIsValid = false;
                        }
                    }
                    break;
                case BrisureMur:
                    fenetrePrincipale.getControleur().deplacerBrisureMurSansUndo(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBrisureMurDTO().getPosition(), evtRelative);
                    panneauOutils.setPropsBrisureMurModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBrisureMurDTO());
                    break;
                case Portail:
                    fenetrePrincipale.getControleur().deplacerPortailSansUndo(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePortailDTO().getPosition(), evtRelative);
                    panneauOutils.setPropsPortailModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePortailDTO());
                    break;
            }
        }
    }

    private void ajoutObjet(MouseEvent evt) {
        int indexObjetAAjouter = panneauOutils.getIndexObjetAAjouter();
        if (indexObjetAAjouter == 0) {
            double rayon = fenetrePrincipale.getControleur().getRayonBoules();
            Color couleur = ColorPicker.getInstance().getCouleur();
            int numero = panneauOutils.getInputNumero();
            BouleDTO boule = new BouleDTO(evt.getPoint(), rayon, couleur, numero, false);
            if (rayon > 0 && numero >= 0 && fenetrePrincipale.getControleur().ajouterBouleColoree(boule, true)) {
                panneauOutils.resetMessage();
            } else {
                panneauOutils.afficherMessage("Ajout de la boule impossible");
            }
        } else if (indexObjetAAjouter == 1) {
            double rayon = panneauOutils.getInputRayon();
            if (rayon > 0 && fenetrePrincipale.getControleur().ajouterPoche(new PocheDTO(evt.getPoint(), rayon), true)) {
                panneauOutils.resetMessage();
            } else {
                panneauOutils.afficherMessage("Ajout de la poche impossible");
            }
        } else if (indexObjetAAjouter == 2) {
            Point2D positionSouris = evt.getPoint();
            double angle = panneauOutils.getInputAngle();
            double longueur = panneauOutils.getInputLongueur();
            double epaisseur = panneauOutils.getInputEpaisseur();
            MurDTO mur = new MurDTO(positionSouris, angle, longueur, epaisseur, false);
            if (longueur > 0 && epaisseur > 0 && fenetrePrincipale.getControleur().ajouterMurObstacleSeul(mur, true)) {
                panneauOutils.resetMessage();
            } else {
                panneauOutils.afficherMessage("Ajout du mur impossible");
            }
        } else if (indexObjetAAjouter == 3) {
            Point2D positionSouris = evt.getPoint();
            double angle = panneauOutils.getInputAngle();
            if (fenetrePrincipale.getControleur().ajouterPaquetBoulesColorees(positionSouris, angle, true))
                panneauOutils.resetMessage();
            else
                panneauOutils.afficherMessage("Ajout du paquet de boules impossible");
        } else if (indexObjetAAjouter == 4) {
            Point2D positionSouris = evt.getPoint();
            double angle = panneauOutils.getInputAngle();
            double longueur = panneauOutils.getInputLongueur();
            boolean couleur = panneauOutils.getCouleurPortail();
            PortailDTO portail = new PortailDTO(positionSouris, angle, longueur, couleur);
            if (!fenetrePrincipale.getControleur().couleurPortailDejaAjoute(couleur)
                    && fenetrePrincipale.getControleur().ajouterPortail(portail, true))
                panneauOutils.resetMessage();
            else
                panneauOutils.afficherMessage("Ajout du portail impossible");
        }
        else throw new RuntimeException();
    }

    private void briserMur(MouseEvent evt) {
        if (fenetrePrincipale.getControleur().briserMur(evt.getPoint())) {
            panneauOutils.resetMessage();
        } else {
            panneauOutils.afficherMessage("Brisage du mur impossible");
        }
    }

    private void supprimerObjet(MouseEvent evt) {
        if (fenetrePrincipale.getControleur().supprimer(evt.getPoint())) {
            panneauOutils.resetMessage();
        } else {
            panneauOutils.afficherMessage("Suppression impossible");
        }

    }


    protected void setCoordStatusBar(MouseEvent evt) {
        //Position relative domaine
        fenetrePrincipale.getStatusBar().setMouseCoord(fenetrePrincipale.getControleur().getRelativeMousePoint(evt.getPoint()));
    }

    protected void zoomEvent(MouseWheelEvent evt) {
        Point2D p = evt.getPoint();
        // Si CTRL est enfoncer on zoom
        if (evt.isControlDown() && !evt.isShiftDown()) {
            if (evt.getWheelRotation() == -1) {
                fenetrePrincipale.getControleur().zoomIn(p);
            } else if (evt.getWheelRotation() == 1) {
                fenetrePrincipale.getControleur().zoomOut(p);
            }
            fenetrePrincipale.repaintMesure();
        }
        //Sinon on Scroll
        else {
            Component source = (Component) evt.getSource();
            source.getParent().dispatchEvent(evt);
        }
    }


    protected void bougerLeplan(MouseEvent evt) {
//        if (fenetrePrincipale.getBoolSelectionnerActive() && !movingObject) {
        if (!movingObject) {
            double dx = evt.getX() - mousePoint.getX();
            double dy = evt.getY() - mousePoint.getY();
            fenetrePrincipale.getControleur().deplacer(dx, dy);
            mousePoint = new Point2D.Double(mousePoint.getX() + dx, mousePoint.getY() + dy);
        }
    }

    public void debugPoint(MouseEvent evt) {
        System.out.println("Position souris " + mousePoint.getX() + " " + mousePoint.getY());
        Point2D point = fenetrePrincipale.getControleur().getRelativeMousePoint(mousePoint);
        System.out.println("Position domaine " + point.getX() + ", " + point.getY());
        point = fenetrePrincipale.getControleur().getAbsoluteMousePoint(point);
        System.out.println("Position domaine -> souris  " + point.getX() + ", " + point.getY());
    }

    private void setMousePoint(Point2D mousePoint){
        fenetrePrincipale.getControleur().setMousePointDessinateur(mousePoint);
    }

    private void removeInterMurState(){

        if(!wallStateIsValid){
            fenetrePrincipale.getControleur().undo();
            wallState--;
            wallStateIsValid = true;
        }
        for(int i = wallState ; i > 0; i--){
            fenetrePrincipale.getControleur().removeState();
        }
        wallState = 0;

    }
}

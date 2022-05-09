package ca.ulaval.glo2004.gui.EcouteurEvt.Edition;

import ca.ulaval.glo2004.domaine.DTO.*;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.gui.Panneau.ColorPicker;
import ca.ulaval.glo2004.gui.Panneau.PanneauOutils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Objects;

public class EditionObjetEvn extends AbstractAction {

    private MainWindow fenetrePrincipale;

    public EditionObjetEvn(MainWindow fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            PanneauOutils panneauOutils = fenetrePrincipale.getPanneauOutils();
            if (fenetrePrincipale.getBoolSelectionnerActive()) {
                MainWindow.TypeObjetTable typeDernierObjetTableSelectionne =
                        fenetrePrincipale.getControleur().getTypeDernierObjetTableSelectionne();
                Point2D positionClicDernierObjetTableSelectionne =
                        fenetrePrincipale.getControleur().getPositionClicDernierObjetTableSelectionne();
                if (typeDernierObjetTableSelectionne == MainWindow.TypeObjetTable.Boule) {
                    BouleDTO boule = fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBouleDTO();
                    if (!Objects.equals(panneauOutils.getInputPositionX(), panneauOutils.getInputPositionXInitial())
                            || !Objects.equals(panneauOutils.getInputPositionY(), panneauOutils.getInputPositionYInitial())) {
                        double positionX = panneauOutils.getInputPositionX();
                        double positionY = panneauOutils.getInputPositionY();
                        Point2D nouvellePosition = new Point2D.Double(positionX, positionY);
                        if (fenetrePrincipale.getControleur().deplacerBoule(positionClicDernierObjetTableSelectionne, nouvellePosition)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Position de la boule invalide");
                        }
                    }
                    if (!Objects.equals(panneauOutils.getInputRayon(), panneauOutils.getInputRayonInitial())) {
                        double nouveauRayonBoules = panneauOutils.getInputRayon();
                        if (panneauOutils.getInputRayon() > 0 && fenetrePrincipale.getControleur().setRayonBoules(nouveauRayonBoules)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Rayon de la boule invalide");
                        }
                    }
                    if (!boule.isBouleBlancheOriginale()
                            && !Objects.equals(panneauOutils.getInputCouleur(), panneauOutils.getCouleurInitiale())) {
                        Color nouvelleCouleur = ColorPicker.getInstance().getCouleur();
                        if (fenetrePrincipale.getControleur().setCouleurBouleColoree(positionClicDernierObjetTableSelectionne, nouvelleCouleur)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Couleur de la boule invalide");
                        }
                    }
                    panneauOutils.setPropsBouleModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBouleDTO());
                } else if (typeDernierObjetTableSelectionne == MainWindow.TypeObjetTable.Poche) {
                    PocheDTO poche = fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePocheDTO();
                    if (!Objects.equals(panneauOutils.getInputPositionX(), panneauOutils.getInputPositionXInitial())
                            || !Objects.equals(panneauOutils.getInputPositionY(), panneauOutils.getInputPositionYInitial())) {
                        double positionX = panneauOutils.getInputPositionX();
                        double positionY = panneauOutils.getInputPositionY();
                        Point2D nouvellePosition = new Point2D.Double(positionX, positionY);
                        if (fenetrePrincipale.getControleur().deplacerPoche(positionClicDernierObjetTableSelectionne, nouvellePosition)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Position de la poche invalide");
                        }
                    }
                    if (!Objects.equals(panneauOutils.getInputRayon(), panneauOutils.getInputRayonInitial())) {
                        double nouveauRayonPoche = panneauOutils.getInputRayon();
                        if (panneauOutils.getInputRayon() > 0 && fenetrePrincipale.getControleur().setRayonPoche(positionClicDernierObjetTableSelectionne, nouveauRayonPoche)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Rayon de la poche invalide");
                        }
                    }
                    panneauOutils.setPropsPocheModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePocheDTO());
                } else if (typeDernierObjetTableSelectionne == MainWindow.TypeObjetTable.BrisureMur) {
                    BrisureMurDTO brisureMur = fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBrisureMurDTO();
                    if (!Objects.equals(panneauOutils.getInputPositionX(), panneauOutils.getInputPositionXInitial())
                            || !Objects.equals(panneauOutils.getInputPositionY(), panneauOutils.getInputPositionYInitial())) {
                        double positionX = panneauOutils.getInputPositionX();
                        double positionY = panneauOutils.getInputPositionY();
                        Point2D nouvellePosition = new Point2D.Double(positionX, positionY);
                        if (fenetrePrincipale.getControleur().deplacerBrisureMur(positionClicDernierObjetTableSelectionne, nouvellePosition)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Position de la brisure de mur invalide");
                        }
                    }
                    if (!brisureMur.isPourMurContour()
                            && !Objects.equals(panneauOutils.getInputAngle(), panneauOutils.getInputAngleInitial())) {
                        double angleRotation = panneauOutils.getInputAngle();
                        if (fenetrePrincipale.getControleur().rotationMurObstacleAvecBrisureMur(positionClicDernierObjetTableSelectionne, angleRotation)) {
                            panneauOutils.resetMessage();
                        }
                        else {
                            panneauOutils.afficherMessage("Rotation de la brisure de mur invalide");
                        }
                    }
                    panneauOutils.setPropsBrisureMurModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneBrisureMurDTO());
                } else if (typeDernierObjetTableSelectionne == MainWindow.TypeObjetTable.Mur) {
                    MurDTO mur = fenetrePrincipale.getControleur().getDernierObjetTableSelectionneMurDTO();
                    if (mur.isMurContour()) {
                        if (!Objects.equals(panneauOutils.getInputEpaisseur(), panneauOutils.getInputEpaisseurInitial())) {
                            if (panneauOutils.getInputEpaisseur() > 0 && fenetrePrincipale.getControleur().setEpaisseurMursContour(panneauOutils.getInputEpaisseur())) {
                                panneauOutils.resetMessage();
                            } else {
                                panneauOutils.afficherMessage("Épaisseur des murs de contour invalide");
                            }
                        }
                    }
                    else {
                        if (!Objects.equals(panneauOutils.getInputPositionX(), panneauOutils.getInputPositionXInitial())
                                || !Objects.equals(panneauOutils.getInputPositionY(), panneauOutils.getInputPositionYInitial())) {
                            double positionX = panneauOutils.getInputPositionX()-(mur.getLongueur()/2)*Math.cos(mur.getAngle());
                            double positionY = panneauOutils.getInputPositionY()-(mur.getLongueur()/2)*Math.sin(mur.getAngle());
                            Point2D nouvellePosition = new Point2D.Double(positionX, positionY);
                            if (fenetrePrincipale.getControleur().deplacerMurObstacle(positionClicDernierObjetTableSelectionne, nouvellePosition)) {
                                panneauOutils.resetMessage();
                            } else {
                                panneauOutils.afficherMessage("Position de la brisure de mur invalide");
                            }
                        }
                        if (!Objects.equals(panneauOutils.getInputAngle(), panneauOutils.getInputAngleInitial())) {
                            if (fenetrePrincipale.getControleur().setAngleMurObstacle(positionClicDernierObjetTableSelectionne, panneauOutils.getInputAngle()))
                                panneauOutils.resetMessage();
                            else
                                panneauOutils.afficherMessage("Angle de l'obstacle invalide");
                            }
                        if (!Objects.equals(panneauOutils.getInputLongueur(), panneauOutils.getInputLongueurInitial())) {
                            if (panneauOutils.getInputLongueur() > 0
                                    && fenetrePrincipale.getControleur().setLongueurMurObstacle(positionClicDernierObjetTableSelectionne, panneauOutils.getInputLongueur()))
                                panneauOutils.resetMessage();
                            else
                                panneauOutils.afficherMessage("Longueur de l'obstacle invalide");
                        }
                        if (!Objects.equals(panneauOutils.getInputEpaisseur(), panneauOutils.getInputEpaisseurInitial())) {
                            if (panneauOutils.getInputEpaisseur() > 0
                                    && fenetrePrincipale.getControleur().setEpaisseurMurObstacle(positionClicDernierObjetTableSelectionne, panneauOutils.getInputEpaisseur()))
                                panneauOutils.resetMessage();
                            else
                                panneauOutils.afficherMessage("Epaisseur de l'obstacle invalide");
                        }
                    }
                    panneauOutils.setPropsMurModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionneMurDTO());
                } else if (typeDernierObjetTableSelectionne == MainWindow.TypeObjetTable.Portail) {
                    PortailDTO portail = fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePortailDTO();
                    if (!Objects.equals(panneauOutils.getInputPositionX(), panneauOutils.getInputPositionXInitial())
                            || !Objects.equals(panneauOutils.getInputPositionY(), panneauOutils.getInputPositionYInitial())) {
                        double positionX = panneauOutils.getInputPositionX();
                        double positionY = panneauOutils.getInputPositionY();
                        Point2D nouvellePosition = new Point2D.Double(positionX, positionY);
                        if (fenetrePrincipale.getControleur().deplacerPortail(positionClicDernierObjetTableSelectionne, nouvellePosition)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Position du portail invalide");
                        }
                    }
                    if (!Objects.equals(panneauOutils.getInputAngle(), panneauOutils.getInputAngleInitial())) {
                        if (fenetrePrincipale.getControleur().setAnglePortail(positionClicDernierObjetTableSelectionne, panneauOutils.getInputAngle()))
                            panneauOutils.resetMessage();
                        else
                            panneauOutils.afficherMessage("Angle du portail invalide");
                    }
                    if (!Objects.equals(panneauOutils.getInputLongueur(), panneauOutils.getInputLongueurInitial())) {
                        if (panneauOutils.getInputLongueur() > 0
                                && fenetrePrincipale.getControleur().setLongueurPortail(positionClicDernierObjetTableSelectionne, panneauOutils.getInputLongueur()))
                            panneauOutils.resetMessage();
                        else
                            panneauOutils.afficherMessage("Longueur du portail invalide");
                    }
                    panneauOutils.setPropsPortailModifier(fenetrePrincipale.getControleur().getDernierObjetTableSelectionnePortailDTO());
                }
                fenetrePrincipale.getPanneauOutils().labelMetrique();
            } else if (fenetrePrincipale.getBoolAjouterActive()) {
                int indexObjetAAjouter = panneauOutils.getIndexObjetAAjouter();
                // Boule
                if (indexObjetAAjouter == 0) {
                    double positionX = panneauOutils.getInputPositionX();
                    double positionY = panneauOutils.getInputPositionY();
                    Point2D position = new Point2D.Double(positionX, positionY);
                    double rayon = fenetrePrincipale.getControleur().getRayonBoules();
                    int numero = panneauOutils.getInputNumero();
                        Color couleur = ColorPicker.getInstance().getCouleur();
                        BouleDTO boule = new BouleDTO(position, rayon, couleur, numero, false);
                        if (rayon > 0 && fenetrePrincipale.getControleur().ajouterBouleColoree(boule, false)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Ajout de la boule impossible");
                        }
                }
                // Poche
                else if (indexObjetAAjouter == 1) {
                    double positionX = panneauOutils.getInputPositionX();
                    double positionY = panneauOutils.getInputPositionY();
                    Point2D position = new Point2D.Double(positionX, positionY);
                    double rayon = panneauOutils.getInputRayon();
                        PocheDTO poche = new PocheDTO(position, rayon);
                        if (rayon > 0 && fenetrePrincipale.getControleur().ajouterPoche(poche, false)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Ajout de la poche impossible");
                        }
                }
                // Mur d'obstacle
                else if (indexObjetAAjouter == 2) {
                    double angle = panneauOutils.getInputAngle();
                    double longueur = panneauOutils.getInputLongueur();
                    double epaisseur = panneauOutils.getInputEpaisseur();
                    double positionX = panneauOutils.getInputPositionX() - (longueur/2)*Math.cos(angle);
                    double positionY = panneauOutils.getInputPositionY() - (longueur/2)*Math.sin(angle);
                    Point2D position = new Point2D.Double(positionX, positionY);
                        MurDTO mur = new MurDTO(position, angle, longueur, epaisseur, false);
                        if (longueur > 0 && epaisseur > 0 && fenetrePrincipale.getControleur().ajouterMurObstacleSeul(mur, false)) {
                            panneauOutils.resetMessage();
                        } else {
                            panneauOutils.afficherMessage("Ajout du mur impossible");
                        }
                }
                // Paquet de boules colorées
                else if (indexObjetAAjouter == 3) {
                    double positionX = panneauOutils.getInputPositionX();
                    double positionY = panneauOutils.getInputPositionY();
                    Point2D position = new Point2D.Double(positionX, positionY);
                    double angle = panneauOutils.getInputAngle();
                    if (fenetrePrincipale.getControleur().ajouterPaquetBoulesColorees(position, angle, false))
                        panneauOutils.resetMessage();
                    else
                        panneauOutils.afficherMessage("Ajout du paquet de boules impossible");
                }
                // Portail
                else if (indexObjetAAjouter == 4) {
                    double positionX = panneauOutils.getInputPositionX();
                    double positionY = panneauOutils.getInputPositionY();
                    Point2D position = new Point2D.Double(positionX, positionY);
                    double angle = panneauOutils.getInputAngle();
                    double longueur = panneauOutils.getInputLongueur();
                    boolean couleur = panneauOutils.getCouleurPortail();
                    PortailDTO portail = new PortailDTO(position, angle, longueur, couleur);
                    if (!fenetrePrincipale.getControleur().couleurPortailDejaAjoute(couleur)
                            && fenetrePrincipale.getControleur().ajouterPortail(portail, false))
                        panneauOutils.resetMessage();
                    else
                        panneauOutils.afficherMessage("Ajout du portail impossible");
                }
                else throw new RuntimeException();
            }else if(fenetrePrincipale.getPropPhysiquesActive()){
                boolean messageAffiche = false;
                if (fenetrePrincipale.getControleur().setCinetiqueFriction(panneauOutils.getInputCinetiqueFriction())) {
                    panneauOutils.resetMessage();
                } else {
                    messageAffiche = true;
                    panneauOutils.afficherMessage("Coefficient cinétique impossible");
                }
                if (fenetrePrincipale.getControleur().setDensiteBoules(panneauOutils.getInputDensiteBoules()) && !messageAffiche) {
                    panneauOutils.resetMessage();
                } else if(!messageAffiche){
                    messageAffiche = true;
                    panneauOutils.afficherMessage("Densité des boules impossible");
                }
                if (fenetrePrincipale.getControleur().setCoefRestitution(panneauOutils.getInputCoefRestitution()) && !messageAffiche) {
                    panneauOutils.resetMessage();
                } else if(!messageAffiche){
                    messageAffiche = true;
                    panneauOutils.afficherMessage("Coefficient de restitution impossible");
                }
                if(fenetrePrincipale.getControleur().changementTaille(panneauOutils.getInputHauteurTable(), panneauOutils.getInputLargeurTable()) && !messageAffiche){
                    panneauOutils.resetMessage();
                } else if(!messageAffiche){
                    messageAffiche = true;
                    panneauOutils.afficherMessage("Hauteur et Largeur Invalide");
                }
            } else throw new RuntimeException();

        } catch (NumberFormatException e){
        }
    }

}

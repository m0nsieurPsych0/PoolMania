package ca.ulaval.glo2004.gui.EcouteurEvt.RaccourcisMenuEvn;

import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.utils.UtilitaireSerialization;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuEvn extends AbstractAction {

    private final MainWindow fenetrePrincipale;
    private int key;
    private String path;
    private StringBuilder stringBuilder;
    private StringBuilder tableNonEnregistre = new StringBuilder("la table n'a pas été enregistrée");
    private StringBuilder tableImageNonChargee = new StringBuilder("L'image n'a pas été chargée");
    private StringBuilder tableImageNonExport = new StringBuilder("L'image n'a pas été exportée");


    public MenuEvn(MainWindow fenetrePrincipale, int key){
        this.fenetrePrincipale = fenetrePrincipale;
        this.key = key;
        this.path = fenetrePrincipale.getCurrentPath();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Point point;
        switch (key){
            case KeyEvent.VK_G:
                //GrilleMagnetique
                fenetrePrincipale.setBoolGrilleActive();
                break;
            case KeyEvent.VK_N:
                //Nouveau
                fenetrePrincipale.showAccueil();
                fenetrePrincipale.setTailleInitBool(true);
                break;
            case KeyEvent.VK_O:
                //Ouvrir
                path = UtilitaireSerialization.ouvrirSaveTable(true, fenetrePrincipale);
                if(path != null) {
                    fenetrePrincipale.setCurrentPath(path);
                    if(fenetrePrincipale.chargerTable(path)) {
                        stringBuilder = new StringBuilder("Table " + path + " chargée");
                    }else{
                        stringBuilder = new StringBuilder("Erreur de chargement");
                    }
                }else {
                    stringBuilder = tableNonEnregistre;
                }
                fenetrePrincipale.setMessage(stringBuilder.toString());
                break;
            case KeyEvent.VK_S:
                //Enregistrer
                if(fenetrePrincipale.getCurrentPath() == null) {
                    path = UtilitaireSerialization.ouvrirSaveTable(false, fenetrePrincipale);
                    if(path != null) {
                        fenetrePrincipale.setCurrentPath(path);
                    }else {
                        stringBuilder = tableNonEnregistre;
                    }
                }
                if(fenetrePrincipale.enregistrerTable(fenetrePrincipale.getCurrentPath())){
                    stringBuilder = new StringBuilder("Table " + path + " enregistrée");
                }else{
                    stringBuilder = tableNonEnregistre;
                }
                fenetrePrincipale.setMessage(stringBuilder.toString());
                break;
            case KeyEvent.VK_S + (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK):
                //EnregistrerSous
                path = UtilitaireSerialization.ouvrirSaveTable(false, fenetrePrincipale);
                if(path != null) {
                    fenetrePrincipale.setCurrentPath(path);
                    fenetrePrincipale.enregistrerTable(path);
                    stringBuilder = new StringBuilder("Table " + path + " enregistrée");
                }else {
                    stringBuilder = tableNonEnregistre;
                }
                fenetrePrincipale.setMessage(stringBuilder.toString());
                break;
            case KeyEvent.VK_E + (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK):
                //Exporter
                path = UtilitaireSerialization.saveSVG(fenetrePrincipale);
                if(path != null) {
                    try {
                        fenetrePrincipale.enregistrerSVG(path);
                        stringBuilder = new StringBuilder("Table " + path + " exportée");
                    }catch (Exception ex){
                        stringBuilder = tableImageNonExport;
                    }

                }else {
                    stringBuilder = tableImageNonExport;
                }
                fenetrePrincipale.setMessage(stringBuilder.toString());
                break;
                case KeyEvent.VK_E:
                //Exporter PNG
                    path = UtilitaireSerialization.savePNG(fenetrePrincipale);
                    if(path != null) {
                        try {
                            fenetrePrincipale.enregistrerPNG(path);
                            stringBuilder = new StringBuilder("Table " + path + " exportée");
                        }catch (Exception ex){
                            stringBuilder = tableImageNonExport;
                        }

                    }else {
                        stringBuilder = tableImageNonExport;
                    }
                    fenetrePrincipale.setMessage(stringBuilder.toString());
                break;
            case KeyEvent.VK_Q:
                //Quitter
                System.exit(0);
                break;
            case KeyEvent.VK_EQUALS:
                //ZoomIN
                point = fenetrePrincipale.getPanneauDessin().getMousePosition();
                if(point == null){
                    point = new Point(0,0);
                }
                fenetrePrincipale.getControleur().zoomIn(point);

                break;
            case KeyEvent.VK_MINUS:
                //ZoomOut
                point = fenetrePrincipale.getPanneauDessin().getMousePosition();
                if(point == null){
                    point = new Point(0,0);
                }
                fenetrePrincipale.getControleur().zoomOut(point);

                break;
            case KeyEvent.VK_0:
                //TailleInit
                fenetrePrincipale.setTailleInitBool(true);
                break;
            case KeyEvent.VK_H:
                //CacherOutils
                fenetrePrincipale.cacherOutils();
                break;
            case KeyEvent.VK_Z:
                //Undo
                fenetrePrincipale.getControleur().undo();
                break;
            case KeyEvent.VK_Z+ (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK):
                //Redo
                fenetrePrincipale.getControleur().redo();
                break;
            case KeyEvent.VK_U:
                //UniteImperiale
                fenetrePrincipale.setMetrique(false);
                fenetrePrincipale.imperialActive();
                break;
            case KeyEvent.VK_U + (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK):
                //UniteMetrique
                fenetrePrincipale.setMetrique(true);
                fenetrePrincipale.metriqueActive();
                break;
            case KeyEvent.VK_R:
                // Poches réalistes
                fenetrePrincipale.setPochesRealistes();
                break;
            case KeyEvent.VK_P:
                // Pointille
                fenetrePrincipale.setPointille();
                break;
            case KeyEvent.VK_F :
                //Charger Image
                path = UtilitaireSerialization.ouvrirImage(fenetrePrincipale);
                if(path != null) {
                    BufferedImage image;
                    boolean success = true;
                    try {
                        image = ImageIO.read(new File(path));
                    } catch (IOException ex) {
                        image = null;
                        success = false;
                    }
                    fenetrePrincipale.setImage(image);
                    if(success) {
                        stringBuilder = new StringBuilder("Image chargée");
                    }else{
                        stringBuilder = new StringBuilder("Erreur de chargement");
                    }
                }else {
                    stringBuilder = tableImageNonChargee;
                }
                fenetrePrincipale.setMessage(stringBuilder.toString());
                break;
            case KeyEvent.VK_D :
                fenetrePrincipale.dechargerImage();
                break;


        }
        //todo Pas nécessaire de revalider panneauDessin, ça ralentis les performances
//        fenetrePrincipale.repaintPanneauDessin();
//        fenetrePrincipale.revalidatePanneauDessin();

        //Always repaint and revalidate mesure
        fenetrePrincipale.repaintMesure();

    }

}

package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.gui.EcouteurEvt.RaccourcisMenuEvn.MenuEvn;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar implements ActionListener {
    private KeyStroke keyStroke;
    private int key;
    private int mod;
    private final MainWindow fenetrePrincipale;

    public MenuBar(MainWindow fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        //Fichier
        fichier = new JMenu("Fichier");
        // Nouveau
        key = KeyEvent.VK_N;
        Nouveau = new JMenuItem("Nouveau...", key);
        Nouveau.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Nouveau.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Ouvrir
        key = KeyEvent.VK_O;
        Ouvrir = new JMenuItem("Ouvrir...", key);
        Ouvrir.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Ouvrir.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Enregistrer
        key = KeyEvent.VK_S;
        Enregistrer = new JMenuItem("Enregistrer", key);
        Enregistrer.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Enregistrer.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Enregistrer-Sous
        key = KeyEvent.VK_S;
        mod = (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        EnregistrerSous = new JMenuItem("Enregistrer sous...", key);
        EnregistrerSous.setAccelerator(KeyStroke.getKeyStroke(key,mod));
        EnregistrerSous.addActionListener(new MenuEvn(fenetrePrincipale, (key + mod)));

        // Exporter PNG
        key = KeyEvent.VK_E;
        ExporterPng = new JMenuItem("Exporter en PNG", key);
        ExporterPng.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        ExporterPng.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Exporter SVG
        key = KeyEvent.VK_E;
        mod = (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        ExporterSvg = new JMenuItem("Exporter en SVG", key);
        ExporterSvg.setAccelerator(KeyStroke.getKeyStroke(key, mod));
        ExporterSvg.addActionListener(new MenuEvn(fenetrePrincipale, key + mod));

        //Quitter
        key = KeyEvent.VK_Q;
        Quitter = new JMenuItem("Quitter", key);
        Quitter.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Quitter.addActionListener(new MenuEvn(fenetrePrincipale, key));

        //Edition
        edition = new JMenu("Édition");
        // Undo
        key = KeyEvent.VK_Z;
        Undo = new JMenuItem("Undo", key);
        Undo.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Undo.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Redo
        key = KeyEvent.VK_Z;
        mod = (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        Redo = new JMenuItem("Redo", key);
        Redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,mod));
        Redo.addActionListener(new MenuEvn(fenetrePrincipale, (key + mod)));
        //Affichage
        affichage = new JMenu("Affichage");

        // ZoomIn
        key = KeyEvent.VK_EQUALS;
        ZoomIn = new JMenuItem("Zoom in", key);
        ZoomIn.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        ZoomIn.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // ZoomOut
        key = KeyEvent.VK_MINUS;
        ZoomOut = new JMenuItem("Zoom out", key);
        ZoomOut.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        ZoomOut.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // TailleInit
        key = KeyEvent.VK_0;
        TailleInit = new JMenuItem("Taille Initiale", key);
        TailleInit.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        TailleInit.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Unite Impériale
        key = KeyEvent.VK_U;
        Unite = new JMenu("Unités de mesure");
        Imperiales = new JCheckBoxMenuItem("Impériales", false);
        Imperiales.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Imperiales.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Unite Métrique
        key = KeyEvent.VK_U;
        mod = (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        Metriques = new JCheckBoxMenuItem("Métriques", true);
        Metriques.setAccelerator(KeyStroke.getKeyStroke(key, mod));
        Metriques.addActionListener(new MenuEvn(fenetrePrincipale, (key+mod)));

        // Barre d'outil
        key = KeyEvent.VK_H;
        CacherBarreOutil = new JCheckBoxMenuItem("Barre d'outils", true);
        CacherBarreOutil.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        CacherBarreOutil.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Grille
        key = KeyEvent.VK_G;
        Grille = new JCheckBoxMenuItem("Grille Magnétique", false);
        Grille.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Grille.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Poches réalistes
        key = KeyEvent.VK_R;
        PochesRealites = new JCheckBoxMenuItem("Poches réalistes");
        PochesRealites.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        PochesRealites.addActionListener(new MenuEvn(fenetrePrincipale, key));

        // Pointillé
        key = KeyEvent.VK_P;
        Pointille = new JCheckBoxMenuItem("Pointillé de coup", true);
        Pointille.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        Pointille.addActionListener(new MenuEvn(fenetrePrincipale, key));

        //Charger une image
        ChargerImage = new JMenuItem("Charger une image pour la table");
        key = KeyEvent.VK_F;
        ChargerImage = new JMenuItem("Charger une image pour la table", key);
        ChargerImage.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        ChargerImage.addActionListener(new MenuEvn(fenetrePrincipale, key));

        //Decharger une image
        DechargerImage = new JMenuItem("Decharger l'image de la table");
        key = KeyEvent.VK_D;
        DechargerImage = new JMenuItem("Decharger l'image de la table", key);
        DechargerImage.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        DechargerImage.addActionListener(new MenuEvn(fenetrePrincipale, key));

        add(fichier);
        fichier.add(Nouveau);
        fichier.add(Ouvrir);
        fichier.addSeparator();
        fichier.add(ChargerImage);
        fichier.add(DechargerImage);
        fichier.addSeparator();
        fichier.add(Enregistrer);
        fichier.add(EnregistrerSous);
        fichier.addSeparator();
        fichier.add(ExporterPng);
        fichier.add(ExporterSvg);
        fichier.addSeparator();
        fichier.add(Quitter);

        add(edition);
        edition.add(Undo);
        edition.add(Redo);
        add(affichage);
        affichage.add(ZoomIn);
        affichage.add(ZoomOut);
        affichage.add(TailleInit);
        affichage.addSeparator();
        affichage.add(Unite);
        Unite.add(Imperiales);
        Unite.add(Metriques);
        affichage.add(CacherBarreOutil);
        affichage.add(Grille);
        affichage.addSeparator();
        affichage.add(Pointille);
        affichage.add(PochesRealites);
    }

    public void actionPerformed(ActionEvent e){

    }


    private JMenu fichier;
    private JMenu edition;
    private JMenu affichage;


    private JMenuItem Nouveau;
    private JMenuItem Ouvrir;
    private JMenuItem Enregistrer;
    private JMenuItem EnregistrerSous;
    private JMenuItem ExporterPng;
    private JMenuItem ExporterSvg;
    private JMenuItem Quitter;
    private JMenuItem ChargerImage;
    private JMenuItem DechargerImage;



    private JMenuItem Undo;
    private JMenuItem Redo;

    private JMenuItem ZoomIn;
    private JMenuItem ZoomOut;
    private JMenuItem TailleInit;
    private JCheckBoxMenuItem CacherBarreOutil;
    private JMenu Unite;
    private JCheckBoxMenuItem Imperiales;
    private JCheckBoxMenuItem Metriques;
    private JCheckBoxMenuItem Grille;
    private JCheckBoxMenuItem PochesRealites;
    private JCheckBoxMenuItem Pointille;

    public void metriqueActive() {
        Metriques.setState(true);
        Imperiales.setState(false);
    }

    public void imperialActive() {
        Imperiales.setState(true);
        Metriques.setState(false);
    }
}

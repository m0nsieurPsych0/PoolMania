package ca.ulaval.glo2004.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import ca.ulaval.glo2004.domaine.ControleurPoolMania;
import ca.ulaval.glo2004.gui.Mesures.*;
import ca.ulaval.glo2004.gui.Panneau.PanneauAccueil;
import ca.ulaval.glo2004.gui.Panneau.PanneauDessin;
import ca.ulaval.glo2004.gui.Panneau.PanneauOutils;
import ca.ulaval.glo2004.gui.Panneau.PanneauOutilsSimulation;
import ca.ulaval.glo2004.utils.DimensionDomaine;
import ca.ulaval.glo2004.utils.UtilitaireSerialization;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class MainWindow extends JFrame {

    private PanneauDessin panneauDessin;
    private PanneauOutils panneauOutils;
    private PanneauOutilsSimulation panneauOutilsSimulation;
    private PanneauAccueil panneauAccueil;
    private StatusBar statusBar;
    private JSplitPane jSplitPane;
    private JScrollPane dessinScrollPane;
    private JScrollPane outilsScrollPane;
    private JLabel mesureHaut;
    private JLabel mesureGauche;
    private JLabel mesureCoin;
    private boolean metrique;
    private boolean grilleActive;
    private boolean selectionnerActive;
    private boolean ajouterActive;
    private boolean supprimerActive;
    private boolean briserActive;
    private boolean tailleInit = false;
    private boolean propPhysiquesActive;
    private boolean modeEdition;
    private boolean baguetteSouris;
    private boolean animationFrappe = false;
    private boolean pochesRealistes = false;
    private boolean pointille = true;
    private String currentPath;


    private ImageIcon icon;

    private ControleurPoolMania controleur;
    private MenuBar menuBar;
    private DimensionDomaine dimensionDomaine;

    public enum TypeObjetTable { Boule, Poche, BrisureMur, Mur, Portail }

    public MainWindow() {
        baguetteSouris = false;
        metrique = true;
        grilleActive = false;
        selectionnerActive = true;
        ajouterActive = false;
        supprimerActive = false;
        briserActive = false;
        propPhysiquesActive = false;
        modeEdition = true;
        dimensionDomaine = DimensionDomaine.getInstance();
        statusBar = new StatusBar(this);
        panneauOutils = new PanneauOutils(this);
        panneauOutilsSimulation = new PanneauOutilsSimulation(this);
        panneauDessin = new PanneauDessin(this);
        panneauAccueil = new PanneauAccueil(this);
        controleur = new ControleurPoolMania(this);
        menuBar = new MenuBar(this);
        dessinScrollPane = new JScrollPane(panneauDessin);
        outilsScrollPane = new JScrollPane(panneauOutils);
        jSplitPane = new JSplitPane();
        mesureGauche = new MesureGauche(controleur);
        mesureHaut = new MesureHaut(controleur);
        mesureCoin = new MesureCoin(controleur);
        initializeWindow();
    }

    private void initializeWindow() {
        setTitle("PoolMania");
        setIcon();
        setLayout(new BorderLayout());
        setResizable(true);
        setJMenuBar(menuBar);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            System.out.println("UI Look and feel non trouvé");
        }
        buildContent();
        pack();
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showAccueil();
    }

    private void setIcon(){
        try{
            icon = new ImageIcon(getClass().getResource("/icon.png"));
            setIconImage(icon.getImage());
        }
        catch(Exception e){
            System.out.println("erreur png, trying absolute path");
            try{
                BufferedImage img;
                currentPath += "/poolmania/src/main/resources/icon.png";
                img = ImageIO.read(new File(currentPath));
                icon = new ImageIcon(img);
                setIconImage(icon.getImage());

            }
            catch (Exception exception){
                System.out.println("Failed absolute path...");
                icon = new ImageIcon();
            }
        }

    }

    public void showAccueil(){
        jSplitPane.setVisible(false);
        getContentPane().remove(jSplitPane);
        panneauOutils.setAucunProps();
        statusBar.setVisible(false);
        getContentPane().add(panneauAccueil, BorderLayout.CENTER);
        panneauAccueil.setVisible(true);
    }

    public void showDessin(){
        panneauAccueil.setVisible(false);
        statusBar.setVisible(true);
        panneauOutils.setAucunProps();
        getContentPane().remove(panneauAccueil);
        getContentPane().add(jSplitPane, BorderLayout.CENTER);
        jSplitPane.setVisible(true);
    }

    private void buildContent() {
        // SplitPane
        jSplitPane.setDividerSize(4);
        jSplitPane.setResizeWeight(.90);
        jSplitPane.setContinuousLayout(true);
        jSplitPane.setTopComponent(setPanneauDessinScrollPane());
        jSplitPane.setBottomComponent(setPanneauOutilScrollPane());
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }


    public ControleurPoolMania getControleur() {
        return controleur;
    }

    private JScrollPane setPanneauDessinScrollPane() {
        dessinScrollPane.setViewportBorder(new javax.swing.border.MatteBorder(null));
        dessinScrollPane.setFocusable(false);
        dessinScrollPane.setOpaque(false);
        dessinScrollPane.setViewportView(panneauDessin);
        dessinScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        dessinScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        initMesure();
        return dessinScrollPane;
    }

    private JScrollPane setPanneauOutilScrollPane() {

        outilsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outilsScrollPane.setAutoscrolls(true);
        outilsScrollPane.setMinimumSize(new java.awt.Dimension(200, 1000));
        outilsScrollPane.setPreferredSize(new java.awt.Dimension(200, 1000));
        outilsScrollPane.setViewportView(panneauOutils);

        return outilsScrollPane;
    }

    public boolean getMetrique(){
        return metrique;
    }

    public void setMetrique(boolean changer){
        if(metrique != changer){
            metrique = changer;
            panneauOutils.changerMesure();
            panneauAccueil.changerMesure();
            statusBar.changerMesure();
            labelMetrique();
        }
    }

    private void labelMetrique(){
        panneauOutils.labelMetrique();
        panneauAccueil.labelMetrique();
        statusBar.labelMetrique();
    }
    public void initMesure(){
        dessinScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, mesureCoin);
        dessinScrollPane.setColumnHeaderView(mesureHaut);
        dessinScrollPane.setRowHeaderView(mesureGauche);
    }


    public void repaintMesure() {
        mesureGauche.repaint();
        mesureGauche.revalidate();
        mesureHaut.revalidate();
        mesureHaut.repaint();
        mesureCoin.repaint();
        mesureCoin.revalidate();
    }

    public void repaintPanneauDessin(){
        panneauDessin.repaint();
    }

    public void revalidatePanneauDessin(){
        panneauDessin.revalidate();
    }
    
    public PanneauDessin getPanneauDessin() {return panneauDessin;
    }

    public boolean getBoolGrilleActive(){
        return grilleActive;
    }

    public void setBoolGrilleActive(){
        grilleActive = !grilleActive;
//        panneauOutils.changerGrille();
        statusBar.changerGrille();
    }

    public boolean getBoolSelectionnerActive() {
        return selectionnerActive;
    }

    public void setBoolSelectionnerActive() {
        selectionnerActive = true;
        ajouterActive = false;
        supprimerActive = false;
        briserActive = false;
        propPhysiquesActive = false;
    }

    public void setPropPhysiquesActive(){
        propPhysiquesActive = true;
        selectionnerActive = false;
        ajouterActive = false;
        supprimerActive = false;
        briserActive = false;
    }

    public boolean getPropPhysiquesActive(){
        return propPhysiquesActive;
    }

    public boolean getBoolAjouterActive() { return ajouterActive; }

    public void setBoolAjouterActive() {
        ajouterActive = true;
        selectionnerActive = false;
        supprimerActive = false;
        briserActive = false;
        propPhysiquesActive = false;
    }

    public boolean getBoolSupprimerActive() {
        return supprimerActive;
    }

    public void setBoolSupprimerActive() {
        supprimerActive = true;
        selectionnerActive = false;
        ajouterActive = false;
        briserActive = false;
        propPhysiquesActive = false;
    }

    public boolean getBoolBriserActive() {
        return briserActive;
    }

    public void setBoolBriserActive() {
        briserActive = true;
        selectionnerActive = false;
        ajouterActive = false;
        supprimerActive = false;
        propPhysiquesActive = false;
    }
    public void setTailleInitBool(Boolean bool) {
        tailleInit = bool;
    }

    public boolean getTailleInitBool() {
        return tailleInit;
    }

    public DimensionDomaine getDimensionDomaine(){
        return dimensionDomaine;
    }

    public PanneauOutils getPanneauOutils() { return panneauOutils; }

    public StatusBar getStatusBar(){return statusBar;}

    public void cacherOutils() {
        if (outilsScrollPane.isVisible()){
            outilsScrollPane.setVisible(false);
        }else{
            outilsScrollPane.setVisible(true);
            jSplitPane.setBottomComponent(outilsScrollPane);
        }
    }
    public String getCurrentPath(){
        return currentPath;
    }

    public void setCurrentPath(String path){
        if(currentPath != path){
            this.currentPath = path;
        }
    }

    public boolean enregistrerTable(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("IOException for create new file " + path);
            }
        }
        if(controleur.writeObjectTable(path)){
            return true;
        }else{
            return false;
        }
    }

    public boolean enregistrerSVG(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("IOException for create new file " + path);
            }
        }
        String str = controleur.exportSVG();
        try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
            out.write(str);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean enregistrerPNG(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("IOException for create new file " + path);
            }
        }
        try  {
            ImageIO.write(controleur.exportPNG(), "png", file);
            return true;
        } catch (Exception e){
            return false;
        }
    }


    public boolean chargerTable(String path){
        Object table = UtilitaireSerialization.deserializeObject(path);
        if(table != null) {
            this.controleur = new ControleurPoolMania(this, table);
            showDessin();
            return true;
        }else{
            return false;
        }
    }
    
    public void setMessage(String msg){
        statusBar.setMessage(msg);
    }


    private void outilsEdition(){
        setBaguetteSouris(false);
        setBoolSelectionnerActive();
        panneauOutils.setSelectionner();
        outilsScrollPane.setViewportView(panneauOutils);
    }

    private void outilsSimulation(){
        setForceTir(getControleur().getRayonBoules()*2);
        panneauOutilsSimulation.setPropsSimulation();
        setBaguetteSouris(true);
        outilsScrollPane.setViewportView(panneauOutilsSimulation);
    }

    public boolean getModeEdition(){
        return modeEdition;
    }

    public void setModeEdition(boolean nouveauMode){
        if(modeEdition != nouveauMode) {
            modeEdition = nouveauMode;
            if(modeEdition){
                outilsEdition();
            }else{
                outilsSimulation();
            }
        }
    }

    public PanneauOutilsSimulation getPanneauOutilsSim(){
        return panneauOutilsSimulation;
    }

    public double getForce(){
        return panneauOutilsSimulation.getInputForceTir();
    }

    public void setForceTir(double force) {
        panneauOutilsSimulation.setForceTir(force);
    }

    public boolean getBaguetteSouris(){
        return baguetteSouris;
    }

    public void setBaguetteSouris(boolean baguetteSouris){
        this.baguetteSouris = baguetteSouris;
    }

    public double getAngleTir() {
        return panneauOutilsSimulation.getInputAngle();
    }

    public void setAngleTir(double angle){
        panneauOutilsSimulation.setInputAngle(angle*180/Math.PI);
    }

    public void bougerBoules(){
            controleur.bougerBoules();
    }

    public void changerVecteurs(){
        controleur.changerVecteurs();
    }

    public void frappe(){
        controleur.frappe(panneauOutilsSimulation.getInputAngle(), panneauOutilsSimulation.getInputForceTir()
                    - controleur.getRayonBoules());
    }

    public void setAnimationFrappe(){
        if(animationFrappe)
            frappe();
        animationFrappe = !animationFrappe;

    }
    public boolean getAnimationFrappe(){
        return animationFrappe;
    }

    public void collisionFrappe(){
        controleur.collisionFrappe();
    }

    public boolean ballesImmobiles(){
        return controleur.ballesImmobiles();
    }

    public boolean isPochesRealistes() { return pochesRealistes; }

    public void setPochesRealistes() { pochesRealistes = !pochesRealistes; }

    public boolean isPointille() { return pointille; }

    public void setPointille() { pointille = !pointille; }

    public void setImage(BufferedImage image) {
        this.controleur.setImage(image, true);
    }

    public void dechargerImage() {
        controleur.dechargerImage();
    }

    public void metriqueActive() { menuBar.metriqueActive(); }

    public void imperialActive() { menuBar.imperialActive(); }
}

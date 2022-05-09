package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.DTO.*;
import ca.ulaval.glo2004.domaine.dessinateur.DessinateurMesure;
import ca.ulaval.glo2004.domaine.dessinateur.DessinateurTable;
import ca.ulaval.glo2004.domaine.simulateurBillard.SimulateurDeCoup;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.gui.StatusBar;
import ca.ulaval.glo2004.utils.DimensionDomaine;
import ca.ulaval.glo2004.utils.Polygon2D;
import ca.ulaval.glo2004.utils.UtilitaireSerialization;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControleurPoolMania implements Runnable{

    private TableBillard tableBillard;
    private DessinateurTable dessinateurTable;
    private DessinateurMesure dessinateurMesure;
    private MainWindow fenetrePrincipale;
    private StatusBar statusBar;
    private DimensionDomaine dimensionDomaine;
    private SimulateurDeCoup simulateurDeCoup;
    private UndoRedoManager<TableBillard> undoRedoManager;
    private GrilleMagnetique grilleMagnetique;

    public ControleurPoolMania(MainWindow fenetrePrincipale, Object tableBillard){
        TableBillard tableBillardCast = (TableBillard) tableBillard;
        this.fenetrePrincipale = fenetrePrincipale;
        this.tableBillard = tableBillardCast;
        this.undoRedoManager = new UndoRedoManager(this.tableBillard);
        this.tableBillard.setControleur(this);
        init();
    }

    public ControleurPoolMania(MainWindow fenetrePrincipale){
        this.fenetrePrincipale = fenetrePrincipale;
        init();
    }

    public void run(){
        if(!getModeEdition()) {
            bougerBoules();
            changerVecteurs();
            collisionFrappe();
        }
    }

    private void init(){
        this.simulateurDeCoup = new SimulateurDeCoup(this);
        this.grilleMagnetique = new GrilleMagnetique(this);
        this.dessinateurTable = new DessinateurTable(this,
                this.fenetrePrincipale.getPanneauDessin().getInitialDimensions());
        this.dessinateurMesure = new DessinateurMesure(this);
        final ScheduledExecutorService schedulerCollisions = Executors.newScheduledThreadPool(1);
        schedulerCollisions.scheduleAtFixedRate(this::run,1, 1, TimeUnit.MILLISECONDS);
    }

    public ArrayList<Boule> getListeBoules(){
        return tableBillard.getListeBoules();
    }

    public ArrayList<Poche> getListePoches(){
        return tableBillard.getListePoches();
    }

    public boolean ajouterBouleColoree(BouleDTO boule, boolean isClicked) {
        Point2D position;
        if (isClicked)
            position = getRelativeMousePoint(boule.getPosition());
        else
            position = boule.getPosition();

        if (tableBillard.ajouterBouleColoree(fenetrePrincipale.getControleur().getGrilleMagnet(position), boule.getRayon(), boule.getCouleur(), boule.getNumero())) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean ajouterPoche(PocheDTO poche, boolean isClicked) {
        Point2D position;
        if (isClicked)
            position = getRelativeMousePoint(poche.getPosition());
        else
            position = poche.getPosition();

        if (tableBillard.ajouterPoche(fenetrePrincipale.getControleur().getGrilleMagnet(position), poche.getRayon())) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean ajouterMurObstacleSeul(MurDTO mur, boolean isClicked) {
        Point2D position;
        if (isClicked){
            Point2D positionDomaine;
            positionDomaine = getRelativeMousePoint(new Point2D.Double(mur.getPosition().getX(),mur.getPosition().getY()));
            positionDomaine = fenetrePrincipale.getControleur().getGrilleMagnet(positionDomaine);
            double positionX = positionDomaine.getX() - mur.getLongueur()/2 * Math.cos(mur.getAngle());
            double positionY = positionDomaine.getY() - mur.getLongueur()/2 * Math.sin(mur.getAngle());
            position = new Point2D.Double(positionX, positionY);
        }
        else
            position = mur.getPosition();

        if (tableBillard.ajouterMurObstacleSeul(position, mur.getAngle(), mur.getLongueur(), mur.getEpaisseur())) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean deplacerBoule(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerBoule(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean deplacerBouleSansUndo(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerBoule(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            return true;
        }
        return false;
    }

    public boolean deplacerPoche(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerPoche(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean deplacerPocheSansUndo(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerPoche(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            return true;
        }
        return false;
    }

    public void undo() {
        this.tableBillard = undoRedoManager.undo();
        this.tableBillard.setControleur(this);
        fenetrePrincipale.repaintPanneauDessin();
    }

    public void redo() {
        this.tableBillard = undoRedoManager.redo();
        this.tableBillard.setControleur(this);
        fenetrePrincipale.repaintPanneauDessin();
    }

    public void changerMode() {
    }
    public StatusBar getStatusBar(){return fenetrePrincipale.getStatusBar();}

    public Point2D getRelativeMousePoint(Point2D mousePoint) { return dessinateurTable.getRelativeDomainPoint(mousePoint); }

    public Point2D getAbsoluteMousePoint(Point2D mousePoint) { return dessinateurTable.getAbsoluteMousePoint(mousePoint); }

    public boolean getBoolGrilleActive() { return fenetrePrincipale.getBoolGrilleActive(); }

    public boolean getTailleInitBool(){return fenetrePrincipale.getTailleInitBool();}

    public boolean getMetrique(){return fenetrePrincipale.getMetrique();}

    public void setTailleInitBool(Boolean bool){fenetrePrincipale.setTailleInitBool(bool);}

    public void dessinPanneau(Graphics g) {
        dessinateurTable.dessinPanneau(g);
    }

    public Rectangle getPanneauDessinBounds() { return fenetrePrincipale.getPanneauDessin().getBounds();}

    public double getTailleCase() {
        return GrilleMagnetique.getTailleCase();
    }

    public double getScale() { return dessinateurTable.getScale(); }

    public void zoomIn(Point2D point) { dessinateurTable.zoomIn(point); }

    public void zoomOut(Point2D point) { dessinateurTable.zoomOut(point); }

    public void deplacer(double dx, double dy) { dessinateurTable.deplacer(dx, dy); }

    public Polygon2D getPolygonTable() { return tableBillard.getPolygonTable(); }

    public Polygon2D getPolygonTableBrisure(){
        return tableBillard.getPolygonTableBrisure();
    }

    public boolean briserMur(Point2D mousePoint) {
        if (tableBillard.briserMur(getRelativeMousePoint(mousePoint))) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean supprimer(Point2D point) {
        if (tableBillard.supprimer(getRelativeMousePoint(point))) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public void nouvelleTable(int nbMurs, double longueurMurs, double epaisseurMurs, double rayonBoules){
        Point2D centre = new Point2D.Double(fenetrePrincipale.getPanneauDessin().getLocation().getX() +
                fenetrePrincipale.getPanneauDessin().getInitialDimensions().getWidth()/3,
                fenetrePrincipale.getPanneauDessin().getLocation().getY() +
                        fenetrePrincipale.getPanneauDessin().getInitialDimensions().getHeight()/8);
        //TODO changer logique longueur murs dans le constructeur
        this.tableBillard = new TableBillard(this, nbMurs, longueurMurs, Math.PI, epaisseurMurs, rayonBoules, centre);
        this.undoRedoManager = new UndoRedoManager(this.tableBillard);
        fenetrePrincipale.showDessin();
    }

    public void nouvelleTable(){
        Point2D centre = new Point2D.Double(fenetrePrincipale.getPanneauDessin().getLocation().getX() +
                fenetrePrincipale.getPanneauDessin().getInitialDimensions().getWidth()/3,
                fenetrePrincipale.getPanneauDessin().getLocation().getY() +
                        fenetrePrincipale.getPanneauDessin().getInitialDimensions().getHeight()/3);
        //TODO demander
        //this.tableBillard = new TableBillard(this, nbMurs, longueurMurs, Math.PI, epaisseurMurs, rayonBoules, centre);
        fenetrePrincipale.showDessin();
    }
    public ArrayList<Mur> getListeMurs() { return tableBillard.getListeMurs(); }

    public boolean isMurContour(Mur mur) { return tableBillard.isMurContour(mur); }

    public ArrayList<BrisureMur> getListeBrisuresMur() { return tableBillard.getListeBrisuresMur(); }

    public MainWindow.TypeObjetTable getTypeDernierObjetTableSelectionne() {
        if (tableBillard.getDernierObjetTableSelectionne() instanceof Boule)
            return MainWindow.TypeObjetTable.Boule;
        else if (tableBillard.getDernierObjetTableSelectionne() instanceof Poche)
            return MainWindow.TypeObjetTable.Poche;
        else if (tableBillard.getDernierObjetTableSelectionne() instanceof BrisureMur)
            return MainWindow.TypeObjetTable.BrisureMur;
        else if (tableBillard.getDernierObjetTableSelectionne() instanceof Mur)
            return MainWindow.TypeObjetTable.Mur;
        else if (tableBillard.getDernierObjetTableSelectionne() instanceof Portail)
            return MainWindow.TypeObjetTable.Portail;
        else return null;
    }

    public BouleDTO getDernierObjetTableSelectionneBouleDTO() {
        Boule dernierObjetTableSelectionne = (Boule)tableBillard.getDernierObjetTableSelectionne();
        boolean bouleBlancheOriginale = dernierObjetTableSelectionne instanceof BouleBlanche;
        return new BouleDTO(dernierObjetTableSelectionne.getPosition(),
                dernierObjetTableSelectionne.getBoundingBox().getRayon(),
                dernierObjetTableSelectionne.getCouleur(), dernierObjetTableSelectionne.getNumero(), bouleBlancheOriginale);
    }

    public PocheDTO getDernierObjetTableSelectionnePocheDTO() {
        Poche dernierObjetTableSelectionne = (Poche)tableBillard.getDernierObjetTableSelectionne();
        return new PocheDTO(dernierObjetTableSelectionne.getPosition(),
                dernierObjetTableSelectionne.getBoundingBox().getRayon());
    }

    public BrisureMurDTO getDernierObjetTableSelectionneBrisureMurDTO() {
        BrisureMur dernierObjetTableSelectionne = (BrisureMur)tableBillard.getDernierObjetTableSelectionne();
        return new BrisureMurDTO(dernierObjetTableSelectionne.getPosition(),
                isMurContour(dernierObjetTableSelectionne.getMursConnectes().get(0)));
    }

    public MurDTO getDernierObjetTableSelectionneMurDTO() {
        Mur dernierObjetTableSelectionne = (Mur)tableBillard.getDernierObjetTableSelectionne();
        boolean murContour = isMurContour(dernierObjetTableSelectionne);
        return new MurDTO(dernierObjetTableSelectionne.getPosition(),
                dernierObjetTableSelectionne.getAngle(),
                dernierObjetTableSelectionne.getLongueur(),
                dernierObjetTableSelectionne.getEpaisseur(), murContour);
    }

    public PortailDTO getDernierObjetTableSelectionnePortailDTO() {
        Portail dernierObjetTableSelectionne = (Portail)tableBillard.getDernierObjetTableSelectionne();
        return new PortailDTO(dernierObjetTableSelectionne.getPosition(),
                dernierObjetTableSelectionne.getAngle(),
                dernierObjetTableSelectionne.getLongueur(),
                dernierObjetTableSelectionne.getCouleurPortail());
    }

    public void setDernierObjetTableSelectionne(Point2D point) {
        tableBillard.setDernierObjetTableSelectionne(getRelativeMousePoint(point));
    }

    public double getRayonBoules() { return tableBillard.getRayonBoules(); }


    public double getCinetiqueFriction() { return tableBillard.getCinetiqueFriction(); }
    
    public boolean setCinetiqueFriction(double nouvCinetiqueFriction){
        return tableBillard.setCinetiqueFriction(nouvCinetiqueFriction);
    }

    public double getDensiteBoules() { return tableBillard.getDensiteBoules(); }

    public boolean setDensiteBoules(double nouvDensiteBoules){ return tableBillard.setDensiteBoules(nouvDensiteBoules); }

    public double getPoidsBoules() { return tableBillard.getPoidsBoules(); }

    public double getCoefRestitution() { return tableBillard.getCoefRestitution(); }
    
    public boolean setCoefRestitution(double nouvCoefRestitution){
        return tableBillard.setCoefRestitution(nouvCoefRestitution);
    }
    
    public boolean setRayonBoules(double nouveauRayonBoules) {
        if (tableBillard.setRayonBoules(nouveauRayonBoules)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean setCouleurBouleColoree(Point2D position, Color nouvelleCouleur) {
        if (tableBillard.setCouleurBouleColoree(position, nouvelleCouleur)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean setRayonPoche(Point2D position, double nouveauRayonPoche) {
        if (tableBillard.setRayonPoche(position, nouveauRayonPoche)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean deplacerBrisureMur(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerBrisureMur(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean deplacerBrisureMurSansUndo(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerBrisureMur(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            return true;
        }
        return false;
    }

    public boolean deplacerPortail(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerPortail(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean deplacerPortailSansUndo(Point2D position, Point2D nouvellePosition) {
        if (tableBillard.deplacerPortail(position, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            return true;
        }
        return false;
    }

    public void dessinerCoin(Graphics g) {
        dessinateurMesure.dessinerCoin(g);
    }

    public void dessinerGauche(Graphics g) {
        dessinateurMesure.dessinerGauche(g);
    }

    public void dessinerHaut(Graphics g) {
        dessinateurMesure.dessinerHaut(g);
    }

    public double getEpaisseurMursContour() { return tableBillard.getEpaisseurMursContour(); }

    public boolean setEpaisseurMursContour(double nouvelleEpaisseur) {
        if (tableBillard.setEpaisseurMursContour(nouvelleEpaisseur)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public void setTailleCase(double nouvelleTaille){
        grilleMagnetique.setTailleCase(nouvelleTaille);
    }
    public boolean getTailleCaseChanged(){ return grilleMagnetique.getTailleCaseChanged(); }

    public void setGrilleMagnet(Rectangle2D rect, boolean workerDone, Rectangle2D g2Bounds){ grilleMagnetique.setGrilleMagnet(rect, workerDone, g2Bounds); }

    public Point2D getGrilleMagnet(Point2D points){
        return grilleMagnetique.getGrilleMagnet(points);
    }

    public boolean deplacerMurObstacle(Point2D positionCentre, Point2D nouvellePosition) {
        if (tableBillard.deplacerMurObstacle(positionCentre, nouvellePosition)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }


    public boolean setAngleMurObstacle(Point2D positionCentre, double nouvelAngle) {
        if (tableBillard.setAngleMurObstacle(positionCentre, nouvelAngle)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean setLongueurMurObstacle(Point2D positionCentre, double nouvelleLongueur) {
        if (tableBillard.setLongueurMurObstacle(positionCentre, nouvelleLongueur)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean setEpaisseurMurObstacle(Point2D positionCentre, double nouvelleEpaisseur) {
        if (tableBillard.setEpaisseurMurObstacle(positionCentre, nouvelleEpaisseur)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean writeObjectTable(String path){
        return UtilitaireSerialization.serializeObject(path, tableBillard);
    }

    public boolean changementTaille(double height, double width){
        if (tableBillard.changeHeightAndWidth(height, width)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public void addState() { undoRedoManager.addState(tableBillard); }

    public void removeState() { undoRedoManager.removeState(); }

    public boolean rotationMurObstacleAvecBrisureMur(Point2D position, double angleRotation) {
        if (tableBillard.rotationMurObstacleAvecBrisureMur(position, angleRotation)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public Point2D getPositionClicDernierObjetTableSelectionne() {
        return tableBillard.getPositionClicDernierObjetTableSelectionne();
    }

    public boolean getModeEdition(){
        return fenetrePrincipale.getModeEdition();
    }

    public Point2D getMousePoint(){
        return fenetrePrincipale.getPanneauDessin().getMousePosition();
    }

    public Point2D getPositionBouleBlanche(){
        for(int i = 0; i < tableBillard.getListeBoules().size(); i++){
            if(tableBillard.getListeBoules().get(i) instanceof BouleBlanche){
                return tableBillard.getListeBoules().get(i).getPosition();
            }
        }
        return new Point2D.Double(0,0);
    }

    public void setMousePointDessinateur(Point2D mousePoint){
        dessinateurTable.setMousePoint(mousePoint);
    }

    public double getForce(){
        return fenetrePrincipale.getForce();
    }

    public boolean getBaguetteSouris() {
        return fenetrePrincipale.getBaguetteSouris();
    }

    public double getAngleTir() {
        return fenetrePrincipale.getAngleTir();
    }

    public void setAngleTir(double angle) {
        fenetrePrincipale.setAngleTir(angle);
    }

    public void bougerBoules(){
        simulateurDeCoup.bougerBoules();
    }

    public void changerVecteurs(){
        simulateurDeCoup.changerVecteurs();
    }

    public void frappe(double inputAngle, double inputForce){
        simulateurDeCoup.frappe(inputAngle, inputForce);
    }

    public boolean getAnimationFrappe(){
        return fenetrePrincipale.getAnimationFrappe();
    }
    public void setAnimationFrappe(){
        fenetrePrincipale.setAnimationFrappe();
    }

    public double getHauteurTable() {
        return tableBillard.getHauteurTable();
    }

    public double getLargeurTable() {
        return tableBillard.getLargeurTable();
    }

    public void collisionFrappe(){
        simulateurDeCoup.collisionFrappe();
    }

    public void collisionBouleMur(Boule boule, Mur mur){
        simulateurDeCoup.collisionBouleMur(boule, mur);
    }

    public void collision2Boules(Boule boule1, Boule boule2){
        simulateurDeCoup.collisionBouleBoule(boule1, boule2);
    }

    public boolean ballesImmobiles(){
        return simulateurDeCoup.ballesImmobiles();
    }

    public void removeBoule(Boule boule){
        tableBillard.removeBoule(boule);
    }

    public BouleBlanche getBouleBlanche(){
        return tableBillard.getBouleBlanche();
    }

    public void bouleBlancheDansPoche(BouleBlanche boule) {
        this.removeBoule(boule);
    }

    public boolean bouleBlanchePresente() { return tableBillard.bouleBlanchePresente(); }

    public boolean ajouterBouleBlanche(Point point) {
        Point2D position;
        position = getRelativeMousePoint(point);

        if (tableBillard.ajouterBouleBlanche(position, getRayonBoules())) {
            fenetrePrincipale.repaintPanneauDessin();
            return true;
        }
        return false;
    }

    public void retirerBouleBlanche() {
        tableBillard.retirerBouleBlanche();
    }

    public ObjetTable objetCollisionBouleBlancheImaginaire(double positionX, double positionY) {
        return tableBillard.objetCollisionBouleBlancheImaginaire(positionX, positionY);
    }

    public ArrayList<ArrayList<Point2D>> getListePositionsRebondissement() {
        return simulateurDeCoup.getListePositionsRebondissement();
    }

    public boolean isPochesRealistes() { return fenetrePrincipale.isPochesRealistes(); }

    public boolean isPointille() { return fenetrePrincipale.isPointille(); }

    public ArrayList<Mur> getListeMursObstacles(){
        return tableBillard.getListeMursObstacles();
    }

    public void setImage(BufferedImage image, boolean imported){
        if(image != null && imported){
            // https://stackoverflow.com/questions/9558981/flip-image-with-graphics2d
            AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -image.getHeight(null));
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = op.filter(image, null);

        }
        tableBillard.setImage(image);
    }

    public void dechargerImage() {
        tableBillard.setImage(null);
    }

    public BufferedImage getImage(){
        return tableBillard.getImage();
    }

    public boolean ajouterPaquetBoulesColorees(Point2D position, double anglePaquet, boolean isClicked) {
        if (!isClicked)
            position = getRelativeMousePoint(position);

        if (tableBillard.ajouterPaquetBoulesColorees(getRelativeMousePoint(position), anglePaquet)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public String exportSVG() {
        return dessinateurTable.tableToSVG();
    }
    public BufferedImage exportPNG() {
        return dessinateurTable.tableToPNG();
    }

    public Point getTableOrigine() {
        return tableBillard.getTableOrigine();
    }

    public boolean couleurPortailDejaAjoute(boolean couleur) { return tableBillard.couleurPortailDejaAjoute(couleur); }

    public boolean ajouterPortail(PortailDTO portail, boolean isClicked) {
        Point2D position;
        if (isClicked)
            position = getRelativeMousePoint(portail.getPosition());
        else
            position = portail.getPosition();

        if (tableBillard.ajouterPortail(fenetrePrincipale.getControleur().getGrilleMagnet(position),
                portail.getAngle(), portail.getLongueur(), portail.getCouleurPortail())) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public ArrayList<Portail> getListePortails() { return tableBillard.getListePortails(); }

    public boolean setAnglePortail(Point2D positionCentre, double nouvelAngle) {
        if (tableBillard.setAnglePortail(positionCentre, nouvelAngle)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean setLongueurPortail(Point2D positionCentre, double nouvelleLongueur) {
        if (tableBillard.setLongueurPortail(positionCentre, nouvelleLongueur)) {
            fenetrePrincipale.repaintPanneauDessin();
            undoRedoManager.addState(tableBillard);
            return true;
        }
        return false;
    }

    public boolean isTeleportPortailInvalide(Boule boule) { return tableBillard.isTeleportPortailInvalide(boule); }

    public void setForceTir(double force) { fenetrePrincipale.setForceTir(force); }
}

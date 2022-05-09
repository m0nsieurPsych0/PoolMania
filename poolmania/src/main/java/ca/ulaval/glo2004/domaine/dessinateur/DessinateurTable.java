package ca.ulaval.glo2004.domaine.dessinateur;

import ca.ulaval.glo2004.domaine.*;
import ca.ulaval.glo2004.domaine.simulateurBillard.AnimationFrappe;
import ca.ulaval.glo2004.utils.DimensionDomaine;
import ca.ulaval.glo2004.utils.Polygon2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.abs;

public class DessinateurTable{
    private ControleurPoolMania controleur;
    private Dimension initialDimensions;

    // Zoom et scale
    private float winScalingFactor = DimensionDomaine.getInstance().getWindowsScalingFactor();
    private int zoomStep = 0;
    private ArrayList<Double> scaleStepsPos = new ArrayList<Double>();
    private ArrayList<Double> scaleStepsNeg = new ArrayList<Double>();
    private double scale = 1;
    private double previousScale;
    private enum zoom {IN, OUT}

    // Transformation du plan
    private double xMouse = 0;
    private double yMouse = 0;
    private double translateCorrectX = 0;
    private double translateCorrectY = 0;
    private double xDeplacer = 0;
    private double yDeplacer = -500;
    private double xOffset = 0;
    private double yOffset = 0;
    private AffineTransform at;
    private Point2D mousePoint = new Point2D.Double(0,0);

    //Grille
    private double tailleCase, largeur, hauteur, xCorrection, yCorrection, x, xx, yy, y, limX, limY;
    private BasicStroke grilleStroke;
    private Rectangle2D grilleRect = new Rectangle2D.Double(0, 0, 0, 0);
    private Rectangle2D grilleRectWorker = new Rectangle2D.Double(0, 0, 0, 0);
    private boolean computed = false;
    private int computedNum = 0;

    //Simulation
    private Graphics2D gParam;
    private ImageIcon imageBaguette;
    private AnimationFrappe animationFrappe;

    //Export PNG
    private Graphics2D png;
    BufferedImage image;



    public DessinateurTable(ControleurPoolMania controleur, Dimension initialDimensions)
    {
        this.controleur = controleur;
        this.initialDimensions = initialDimensions;
        this.at = new AffineTransform();
        this.animationFrappe = new AnimationFrappe(this.controleur);
        at.scale(1, -1);
        at.translate(0, -DimensionDomaine.getInstance().getHauteurEcran());
    }


    public void dessinPanneau(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        gParam = g2;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        transformePlan(g2);
        dessinerMurs(g2);
        dessinerTable(g2, false);
        dessinerPoches(g2);
        dessinerBrisuresMur(g2);
        dessinerBaguette(g2);
        dessinerPortails(g2);
        dessinerBoules(g2);

        dessinerPointille(g2);

        dessinerGrilleMagnetique(g2);
        g2.dispose();

    }

    private void transformePlan(Graphics2D g2){
        at = setTailleInit(g2);
        if(at.getTranslateX() == 0 || at.getTranslateY() == 0) {
            translateCorrectX = 0;
            translateCorrectY = 0;
        } else {
            translateCorrectX = at.getTranslateX() / winScalingFactor;
            translateCorrectY = at.getTranslateY() / winScalingFactor;
        }
        at.translate(xMouse, yMouse);
        at.scale(scale, -scale);
        at.translate(-xMouse, -yMouse);
        at.translate(xDeplacer, yDeplacer);
        at.translate(-xOffset, -yOffset);
        g2.setTransform(at);
    }

    private void dessinerTable(Graphics2D g, boolean export){
        g.setColor(new Color(0,135,0, 255));
        Polygon2D table = controleur.getPolygonTableBrisure();
        table.calculatePath();
        if (controleur.getImage() == null){
            g.fill(table);
        }
        ArrayList<Mur> listeMurs = controleur.getListeMurs();
        ArrayList<Mur> listeObstacles = controleur.getListeMursObstacles();
        if(controleur.getImage()  != null){
            Shape oldClip = g.getClip();
            if(export){
                Area secondArea = new Area(controleur.getPolygonTableBrisure());
                g.setClip(secondArea);
            } else {
                Shape rectangle = oldClip;
                Area secondArea = new Area(controleur.getPolygonTableBrisure());
                secondArea.intersect(new Area(rectangle));
                g.setClip(secondArea);
            }
            g.drawImage(controleur.getImage() , (int) getMinValue(controleur.getPolygonTableBrisure().xpoints),(int) getMinValue(controleur.getPolygonTableBrisure().ypoints), (int)controleur.getLargeurTable() ,(int)controleur.getHauteurTable(),null);
            g.setClip(oldClip);
        }
        for(Mur m : listeMurs){
            m.getBoundingBox().getForme().calculatePath();
            g.setColor(Color.black);
            g.draw(m.getBoundingBox().getForme().getPolygon());
        }
        for(Mur m : listeObstacles){
            m.getBoundingBox().getForme().calculatePath();
            g.setColor(new Color(110,55,0));
            g.fill(m.getBoundingBox().getForme().getPolygon());
            g.setColor(Color.black);
            g.draw(m.getBoundingBox().getForme().getPolygon());
        }
    }

    //https://stackoverflow.com/questions/35795473/finding-minimum-value-in-array-of-doubles
    private double getMinValue(double[] distances) {
        double minValue = Double.MAX_VALUE;
        for (double distance : distances) {
            minValue = Math.min(distance, minValue);
        }
        return minValue;
    }

    private void dessinerMurs(Graphics2D g) {
        ArrayList<Mur> listeMurs = controleur.getListeMurs();
        for(Mur m : listeMurs){
            g.setColor(new Color(110,55,0));
            m.getBoundingBox().getForme().calculatePath();
            g.fill(m.getBoundingBox().getForme().getPolygon());
            g.setColor(Color.black);
            g.draw(m.getBoundingBox().getForme().getPolygon());
        }
    }

    private void dessinerBrisuresMur(Graphics2D g) {
        if (controleur.getModeEdition()) {
            ArrayList<BrisureMur> listeBrisuresMur = controleur.getListeBrisuresMur();
            for (BrisureMur b : listeBrisuresMur) {
                Point2D position = b.getPosition();
                double rayon = b.getBoundingBox().getRayon();
                g.setColor(Color.red);
                Ellipse2D.Double ellipse = new Ellipse2D.Double((position.getX()-rayon), (position.getY()-rayon),(2*rayon), (2*rayon));
                g.fill(ellipse);
                g.setStroke(new BasicStroke((float)rayon/6));
                g.setColor(Color.black);
                g.draw(ellipse);
            }
        }
    }

    private void dessinerBoules(Graphics2D g){
        ArrayList<Boule> listeBoules = controleur.getListeBoules();
        double rayon = controleur.getRayonBoules();
        if(!controleur.bouleBlanchePresente()){
            g.setColor(Color.white);
            Ellipse2D.Double ellipse = new Ellipse2D.Double(mousePoint.getX()-rayon, mousePoint.getY()-rayon,
                    (2*rayon), (2*rayon));
            g.fill(ellipse);
            g.setColor(Color.black);
            g.setStroke(new BasicStroke((float)rayon/15));
            g.draw(ellipse);
        }
        for(Boule b : listeBoules){
            Point2D position = b.getPosition();
            g.setColor(Color.white);
            Ellipse2D.Double ellipse = new Ellipse2D.Double((position.getX()-rayon), (position.getY()-rayon),(2*rayon), (2*rayon));
            g.fill(ellipse);
            g.setStroke(new BasicStroke((float)rayon/2));
            g.setColor(b.getCouleur());
            ellipse = new Ellipse2D.Double((position.getX() - 3*rayon/4), (position.getY() - 3*rayon/4), 3*rayon/2, 3*rayon/2);
            g.draw(ellipse);
            ellipse = new Ellipse2D.Double((position.getX()-rayon), (position.getY()-rayon),(2*rayon), (2*rayon));
            g.setColor(Color.black);
            g.setStroke(new BasicStroke((float)rayon/15));
            g.draw(ellipse);
            AffineTransform atString = g.getTransform();
            atString.scale(1, -1);
            g.setTransform(atString);
            if(b.getNumero() != 0){
                g.setFont(new Font("Dialog", Font.BOLD, (int)Math.floor(rayon)));
                if(b.getNumero() > 9){
                    g.drawString(Integer.toString(b.getNumero()), (float)(position.getX()-(12*rayon/20)), (float)-(position.getY()-(rayon/3)));
                } else {
                    g.drawString(Integer.toString(b.getNumero()), (float)(position.getX()-(8*rayon/27)), (float)-(position.getY()-(rayon/3)));
                }
            }
            g.setTransform(at);
        }
    }

    private void dessinerPoches(Graphics2D g){
        ArrayList<Poche> listePoches = controleur.getListePoches();
        for(Poche p : listePoches){
            Point2D position = p.getPosition();
            double rayon = p.getBoundingBox().getRayon();
            g.setColor(new Color(70,70,70));
            Ellipse2D.Double ellipse = new Ellipse2D.Double((position.getX()-rayon), (position.getY()-rayon),(2*rayon), (2*rayon));
            g.fill(ellipse);
            g.setStroke(new BasicStroke((float)(rayon/8)));
            g.setColor(Color.black);
            g.draw(ellipse);
            g.setColor(Color.black);
            ellipse = new Ellipse2D.Double((position.getX()-(4*rayon/5)), (position.getY()-(4*rayon/5)),(8*rayon/5), (8*rayon/5));
            g.fill(ellipse);
            g.setColor(new Color(70,70,70));
            g.setStroke(new BasicStroke((float)(rayon/15)));
            double rayonloop = -3*rayon/5;
            double dist = rayon/5;
            double offset;
            while(rayonloop < 4*rayon/5) {
                offset = rayon-Math.max(Math.abs(rayonloop),rayon/5);
                g.draw(new Line2D.Double(new Point2D.Double(position.getX()+rayonloop, position.getY()+offset), new Point2D.Double(position.getX()+rayonloop, position.getY()-offset)));
                g.draw(new Line2D.Double(new Point2D.Double(position.getX()+offset, position.getY()+rayonloop), new Point2D.Double(position.getX()-offset, position.getY()+rayonloop)));
                rayonloop += dist;
            }
        }
    }

    private void dessinerGrilleMagnetique(Graphics2D g2) {
        if (controleur.getBoolGrilleActive()) {
            tailleCase = GrilleMagnetique.getTailleCase();
            grilleRectWorker = g2.getClipBounds();

            if (computedNum == 20) {
                computed = false;
                computedNum = 0;
            } else {
                computedNum++;
            }

            if (tailleCase*scale < 5) {
                double multiplTaille = tailleCase + ((5 - tailleCase*scale)/scale);
                tailleCase *= Math.ceil(multiplTaille/tailleCase);
            }
            largeur = DimensionDomaine.getInstance().getLimites().width / Math.min(scale,1);
            hauteur = DimensionDomaine.getInstance().getLimites().height / Math.min(scale,1);

            xCorrection = ((at.getTranslateX() / winScalingFactor) / scale + largeur) % tailleCase;
            yCorrection = ((-at.getTranslateY() / winScalingFactor) / scale + hauteur) % tailleCase;
            x = -largeur - (at.getTranslateX() / winScalingFactor) / scale + xCorrection;
            y = -hauteur + (at.getTranslateY() / winScalingFactor) / scale + yCorrection;
            limX = largeur - (at.getTranslateX() / winScalingFactor) / scale + xCorrection;
            limY = hauteur + (at.getTranslateY() / winScalingFactor) / scale + yCorrection;

            grilleStroke = new BasicStroke((float) ((1 / winScalingFactor) / scale));
            g2.setStroke(grilleStroke);
            g2.setColor(Color.black);

            computeGrilleWorker();

            while (x < limX) {
                while (y < limY) {
                    grilleRect.setRect(x, y, tailleCase, tailleCase);
                    g2.draw(grilleRect);
                    y += tailleCase;
                }
                x += tailleCase;
                y = -hauteur + (at.getTranslateY() / winScalingFactor) / scale + yCorrection;
            }
        }
    }

    private void computeGrilleWorker(){
        SwingWorker<Boolean, Rectangle2D> workerX = new SwingWorker<Boolean, Rectangle2D>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                xx = -largeur - (at.getTranslateX() / winScalingFactor) / scale + xCorrection;
                yy = -hauteur + (at.getTranslateY() / winScalingFactor) / scale + yCorrection;

                while (xx < limX) {
                    while (yy < limY) {
                        controleur.setGrilleMagnet(new Rectangle2D.Double(xx, yy, tailleCase, tailleCase), false, grilleRectWorker);
                        yy += tailleCase;
                    }
                    xx += tailleCase;
                    yy = -hauteur + (at.getTranslateY() / winScalingFactor) / scale + yCorrection;
                }
                return true;
            }
            @Override
            protected void done(){
                try {
                    controleur.setGrilleMagnet(grilleRect, get(), grilleRectWorker);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        };
        if(!computed){
            workerX.execute();
            computed = true;
        }
    }

    //Source pour le zoom
    //https://github.com/OneLoneCoder/videos/blob/master/OneLoneCoder_PanAndZoom.cpp
    public void zoomIn(Point2D point) {
        //On peut théoriquement zoom-in indéfiniment, au dessus de 500 ça devient très lent
        if(zoomStep < 500){
            Point2D pointRelative = getRelativeDomainPoint(point);
            setPredictableScaling(zoom.IN);
            Point2D pointAbsolute = getAbsoluteSansDeplacer(pointRelative);
            this.xMouse = point.getX();
            this.yMouse = point.getY();
            xOffset = (pointRelative.getX() - pointAbsolute.getX());
            yOffset = (pointRelative.getY() - pointAbsolute.getY());
            controleur.getStatusBar().setZoomLevel((int)(scale * 100));
        }
        else{
            controleur.getStatusBar().setMessage("Zoom-In maximum Atteint!");
        }
    }

    public void zoomOut(Point2D point) {
        // En dessous de 280 zoom-out ça plante et en dessous de 170 la grille commence à disparaître
        if(zoomStep > -200){
            Point2D pointRelative = getRelativeDomainPoint(new Point2D.Double(point.getX(), point.getY()));
            setPredictableScaling(zoom.OUT);
            Point2D pointAbsolute = getAbsoluteSansDeplacer(pointRelative);
            this.xMouse = point.getX();
            this.yMouse = point.getY();
            xOffset = (pointRelative.getX() - pointAbsolute.getX());
            yOffset = (pointRelative.getY() - pointAbsolute.getY());
            controleur.getStatusBar().setZoomLevel((int)(scale * 100));
        }
        else{
            controleur.getStatusBar().setMessage("Zoom-Out maximum Atteint!");
        }
    }

    public void deplacer(double dx, double dy) {
        xDeplacer += dx/scale;
        yDeplacer -= dy/scale;
    }

    public Point2D getRelativeDomainPoint(Point2D mousePoint) {
        Point2D point = new Point2D.Double();
        double vraiX = ((mousePoint.getX() - (at.getTranslateX()/ winScalingFactor) + translateCorrectX)/(at.getScaleX()/ winScalingFactor));
        double vraiY = ((mousePoint.getY() - (at.getTranslateY()/ winScalingFactor) + translateCorrectY)/(at.getScaleY()/ winScalingFactor));
        point.setLocation(vraiX,vraiY);
        return point;
    }

    public Point2D getAbsoluteMousePoint(Point2D realPoint) {
        Point2D point = new Point2D.Double();
        double sourisX = ((realPoint.getX()*(at.getScaleX()/ winScalingFactor) + at.getTranslateX() - translateCorrectX));
        double sourisY = ((realPoint.getY()*(at.getScaleX()/ winScalingFactor) + at.getTranslateY() - translateCorrectY));
        point.setLocation(sourisX,sourisY);
        return point;
    }

    private Point2D getAbsoluteSansDeplacer(Point2D realPoint) {
        Point2D point = new Point2D.Double();
        double sourisX = ((realPoint.getX()*(at.getScaleX() / winScalingFactor) + (at.getTranslateX() / winScalingFactor) - translateCorrectX - xDeplacer));
        double sourisY = ((realPoint.getY()*(at.getScaleY() / winScalingFactor) + (at.getTranslateY() / winScalingFactor) - translateCorrectY - yDeplacer));
        point.setLocation(sourisX,sourisY);
        return point;
    }

    private Point2D getRelativeSansScale(Point2D mousePoint) {
        Point2D point = new Point2D.Double();
        double vraiX = ((mousePoint.getX()- (at.getTranslateX()) + translateCorrectX));
        double vraiY = ((mousePoint.getY()- (at.getTranslateY()) + translateCorrectY));
        point.setLocation(vraiX,vraiY);
        return point;
    }

    public double getScale(){
        return scale;
    }
    public void restoreTailleInit(){
        scale = 1.0;
        xMouse = 0;
        yMouse = 0;
        translateCorrectX = 0;
        translateCorrectY = 0;
        xDeplacer = 0;
        yDeplacer = -500;
        xOffset = 0;
        yOffset = 0;
        zoomStep = 0;
        controleur.getStatusBar().setZoomLevel((int) (scale * 100));
    }

    private AffineTransform setTailleInit(Graphics2D g2){

        if (controleur.getTailleInitBool() && zoomStep != 0 || controleur.getTailleInitBool() && xDeplacer + yDeplacer != 0){

            controleur.setTailleInitBool(false);
            restoreTailleInit();
            return g2.getTransform();
        }
        return g2.getTransform();

    }

    private void setPredictableScaling(zoom zoom){
        /*
        * Le but de cette fonction est d'avoir un scaling exact en augmentant le scale et en revenant.
        * Quand on revient après avoir fait un très grand zoom, les valeurs deviennent trop précises et finissent par s'arrondires.
        * En créant une liste de valeur de scale, on sait avec certitude qu'on aura toujours les mêmes valeurs dans un sens ou dans un autre.
        * Les listes sont calculées qu'une seule fois au courant de l'exécution.
        */

        // On setup les éléments de départ
        if (scaleStepsPos.isEmpty() || scaleStepsNeg.isEmpty()){
            scaleStepsPos.add(1.0);
            scaleStepsNeg.add(1.0);
        }

        switch (zoom){
            case IN:
                zoomStep++;
                if (zoomStep >= 0){
                    // La valeur n'existe pas alors on l'ajoute
                    if(scaleStepsPos.size() == zoomStep){
                        previousScale = scale;
                        scale *= 1.05;
                        scaleStepsPos.add(scale);
                    }
                    else{
                        scale = scaleStepsPos.get(zoomStep);
                    }
                }
                else{
                    scale = scaleStepsNeg.get(abs(zoomStep));
                }
                break;
            case OUT:
                zoomStep--;
                if (zoomStep <= 0){
                    // La valeur n'existe pas alors on l'ajoute
                    if(scaleStepsNeg.size() == abs(zoomStep)){
                        previousScale = scale;
                        scale /= 1.05;
                        scaleStepsNeg.add(scale);
                    }
                    else{
                        scale = scaleStepsNeg.get(abs(zoomStep));
                    }
                }
                else {
                    scale = scaleStepsPos.get(zoomStep);
                }
                break;
        }
    }

    private void dessinerBaguette(Graphics2D g2){
        if(controleur.ballesImmobiles()){
            if(!controleur.getModeEdition() && controleur.bouleBlanchePresente()){
                dessinerPartieBaguette(g2);
            }

        }
    }

    private void dessinerPartieBaguette(Graphics2D g2){

        double longueurQueueStandard = 1422.4;
        double longueurRelative = controleur.getRayonBoules()*30;
        double segment = 0;
        GradientPaint gradientPaint;

        // Contour
        Line2D ligne = ligneBouleMouse(longueurRelative, 0);

        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke((float)controleur.getRayonBoules()/3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2.draw(ligne);

        // Procédé (bleu) 13/3mm
        double longueurProcede = longueurRelative * ((13/3)/longueurQueueStandard);
        Line2D procede = ligneBouleMouse(longueurProcede, segment);
        segment += longueurProcede ;
        // contour
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke((float)controleur.getRayonBoules()/3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2.draw(procede);
        // intérieur
        g2.setColor(new Color(103, 200, 239));
        g2.setStroke(new BasicStroke((float)controleur.getRayonBoules()/4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2.draw(procede);

        // Virolle (blanc)
        double longueurVirole = longueurRelative * (((13/3)*2)/longueurQueueStandard);
        Line2D virolle = ligneBouleMouse(longueurVirole, segment);
        segment += longueurVirole;
        // contour
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke((float)controleur.getRayonBoules()/3, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_MITER));
        g2.draw(virolle);
        // intérieur
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke((float)controleur.getRayonBoules()/4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2.draw(virolle);


        // Flèche (orange)
        double longueurfleche = longueurRelative * ((longueurQueueStandard-segment)/longueurQueueStandard);
        Line2D fleche = ligneBouleMouse(longueurfleche, segment);
        segment += longueurfleche;
        // contour
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke((float)controleur.getRayonBoules()/3, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_MITER));
        g2.draw(fleche);
        // intérieur
        gradientPaint = new GradientPaint(fleche.getP1(), Color.ORANGE, fleche.getP2(), Color.black);
        g2.setPaint(gradientPaint);
        g2.fill(fleche);
        g2.setStroke(new BasicStroke((float)controleur.getRayonBoules()/4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2.draw(fleche);

    }

    private Line2D ligneBouleMouse(double longueur, double segment){
        Point2D positionBlanche = controleur.getPositionBouleBlanche();
        Point2D autreBout;
        Line2D ligne = new Line2D.Double(0,0,0,0);
        double dist = BoundingBox.distanceEntrePoints(positionBlanche, mousePoint);
        double force, angleToSet;
        if (controleur.getAnimationFrappe()){
            force = animationFrappe.getAnimationSteps() + segment;
            if(animationFrappe.getAnimationFinished()){
                controleur.setAnimationFrappe();
                animationFrappe.setAnimationFinished();
            }
        }
        else{
            force = Math.sqrt(4*controleur.getForce()) + controleur.getRayonBoules() + segment;
        }

        if(controleur.getBaguetteSouris()){
            positionBlanche = new Point2D.Double((1-(force/dist))*positionBlanche.getX() +
                    (force/dist)*mousePoint.getX(), (1-(force/dist))*positionBlanche.getY() +
                    (force/dist)*mousePoint.getY());
            dist = (longueur)/BoundingBox.distanceEntrePoints(positionBlanche, mousePoint);

            if(BoundingBox.distanceEntrePoints(controleur.getPositionBouleBlanche(), mousePoint) < force){
                autreBout = new Point2D.Double(-dist*mousePoint.getX()+(1+dist)*positionBlanche.getX(), -dist*mousePoint.getY()+(1+dist)*positionBlanche.getY());
            }
            else {
                autreBout = new Point2D.Double(dist*mousePoint.getX()+(1-dist)*positionBlanche.getX(), dist*mousePoint.getY()+(1-dist)*positionBlanche.getY());
            }
            //Changer l'angle dans le input
            angleToSet = Math.atan2(autreBout.getY()-positionBlanche.getY(), autreBout.getX()-positionBlanche.getX());
            if(angleToSet < 0){
                angleToSet += 2*Math.PI;
            }
            controleur.setAngleTir(angleToSet);

            return new Line2D.Double(positionBlanche, autreBout);

        }else {

            positionBlanche = new Point2D.Double(positionBlanche.getX() + force*Math.cos(controleur.getAngleTir()),
                    positionBlanche.getY() + force*Math.sin(controleur.getAngleTir()));
            autreBout = new Point2D.Double(positionBlanche.getX() + longueur*Math.cos(controleur.getAngleTir()),
                    positionBlanche.getY() + longueur*Math.sin(controleur.getAngleTir()));
            return new Line2D.Double(positionBlanche, autreBout);
        }

    }

    // Inspiré de https://stackoverflow.com/a/21989406
    private void dessinerPointille(Graphics2D g2){
        if (controleur.isPointille() && controleur.ballesImmobiles() && !controleur.getModeEdition() && controleur.bouleBlanchePresente()) {
            ArrayList<ArrayList<Point2D>> listePositionsRebondissement = controleur.getListePositionsRebondissement();

            Graphics2D copieg2 = (Graphics2D) g2.create();
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                    0, new float[]{9}, 0);
            copieg2.setStroke(dashed);
            copieg2.setColor(Color.black);

            ArrayList<Point2D> listePositionsRebondissementBouleBlanche = listePositionsRebondissement.get(0);

            copieg2.setColor(Color.black);
            for (int i = 0; i < listePositionsRebondissementBouleBlanche.size(); i++) {
                Point2D position1 = listePositionsRebondissementBouleBlanche.get(i);
                Point2D position2 = listePositionsRebondissementBouleBlanche.get(i+1);
                copieg2.drawLine((int)position1.getX(), (int)position1.getY(), (int)position2.getX(), (int)position2.getY());

                if (i == listePositionsRebondissementBouleBlanche.size()-2)
                    break;
            }

            copieg2.setColor(Color.darkGray);
            ArrayList<Point2D> listePositionsBouleCollision = listePositionsRebondissement.get(1);
            if (listePositionsBouleCollision.size() != 0) {
                Point2D position1 = listePositionsBouleCollision.get(0);
                Point2D position2 = listePositionsBouleCollision.get(1);
                copieg2.drawLine((int)position1.getX(), (int)position1.getY(), (int)position2.getX(), (int)position2.getY());
            }

            copieg2.dispose();
        }
    }

    public void dessinerPortails(Graphics2D g2) {
        ArrayList<Portail> listePortails = controleur.getListePortails();

        if (!controleur.getModeEdition())
            if (listePortails.get(0) == null || listePortails.get(1) == null)
                return;

        Portail portail = listePortails.get(0);
        if (portail != null) {
            g2.rotate(portail.getAngle(), portail.getPosition().getX(), portail.getPosition().getY());

            double longueur = portail.getLongueur();
            g2.setColor(new Color(0,172,238));
            Ellipse2D.Double ellipse = new Ellipse2D.Double(portail.getPosition().getX()-longueur,
                    portail.getPosition().getY()-1.5*longueur, (2*longueur), (3*longueur));
            g2.fill(ellipse);
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke((float)longueur/15));
            g2.draw(ellipse);

            g2.rotate(-portail.getAngle(), portail.getPosition().getX(), portail.getPosition().getY());
        }

        portail = listePortails.get(1);
        if (portail != null) {
            g2.rotate(portail.getAngle(), portail.getPosition().getX(), portail.getPosition().getY());

            double longueur = portail.getLongueur();
            g2.setColor(new Color(254,106,0));
            Ellipse2D.Double ellipse = new Ellipse2D.Double(portail.getPosition().getX()-longueur,
                    portail.getPosition().getY()-1.5*longueur, (2*longueur), (3*longueur));
            g2.fill(ellipse);
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke((float)longueur/15));
            g2.draw(ellipse);

            g2.rotate(-portail.getAngle(), portail.getPosition().getX(), portail.getPosition().getY());
        }
    }

    public void setMousePoint(Point2D mousePoint){
        this.mousePoint = getRelativeDomainPoint(mousePoint);
    }

    public String tableToSVG(){
        double rayon = controleur.getRayonBoules();
        Point origine = controleur.getTableOrigine();
        String svgString = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\""+controleur.getLargeurTable()*1.2+"\" height=\""+controleur.getHauteurTable()*1.2+"\" transform=\"scale (1, -1)\" transform-origin=\"center\">\n";
        svgString += "<polygon points=\"" + polygonToString(controleur.getPolygonTableBrisure()) +"\" fill=\"#008700\"/>\n";
        for(Boule b : controleur.getListeBoules()){
            Point2D position = b.getPosition();
            svgString += "<circle cx=\"" + (position.getX() - origine.getX()) +"\" cy=\"" + (position.getY() - origine.getY()) +"\" r=\"" + (rayon) + "\" fill=\""+colorToString(b.getCouleur())+"\"/>\n";
            svgString += "<circle cx=\"" + (position.getX() - origine.getX()) +"\" cy=\"" + (position.getY() - origine.getY()) +"\" r=\"" + (rayon/2) + "\" fill=\"#FFFFFF\"/>\n";
        }
        ArrayList<Mur> listeMurs = controleur.getListeMurs();
        for(Mur m : listeMurs) {
            svgString += "<polygon points=\"" + polygonToString(m.getBoundingBox().getForme().getPolygon()) +"\" fill=\"#6e3700\"/>\n";
        }
        ArrayList<Poche> listePoches = controleur.getListePoches();
        for(Poche p : listePoches){
            Point2D position = p.getPosition();
            rayon = p.getBoundingBox().getRayon();
            svgString += "<circle cx=\"" + (position.getX() - origine.getX()) +"\" cy=\"" + (position.getY() - origine.getY()) +"\" r=\"" + rayon + "\" fill=\"#464646FF\"/>\n";
        }
        ArrayList<Portail> listePortails = controleur.getListePortails();

        Portail portail = listePortails.get(0);
        if (portail != null) {
            double longueur = portail.getLongueur();
            svgString += "<ellipse cx=\""+ (portail.getPosition().getX()-origine.getX()) + "\" cy=\""+(portail.getPosition().getY()-origine.getY())+"\" rx=\""+(longueur)+"\" ry=\""+(longueur*1.5)+"\" fill=\"#00ACEEFF\"/>";
        }

        portail = listePortails.get(1);
        if (portail != null) {
            double longueur = portail.getLongueur();
            svgString += "<ellipse cx=\""+ (portail.getPosition().getX()-origine.getX()) + "\" cy=\""+(portail.getPosition().getY()-origine.getY())+"\" rx=\""+(longueur)+"\" ry=\""+(longueur*1.5)+"\" fill=\"#FE6A00FF\"/>";
        }

        svgString += "</svg>";
        return svgString;
    }

    private String colorToString(Color color){
        return "#"+Integer.toHexString(color.getRGB()).substring(2);
    }

    private String polygonToString(Polygon2D polygon){
        String retour = "";
        Point origine = controleur.getTableOrigine();
        double[] xpoints = polygon.xpoints;
        double[] ypoints = polygon.ypoints;
        for (int i=0; i < xpoints.length; i++){
            retour += (xpoints[i] - origine.getX()) + "," + (ypoints[i] - origine.getY()) + " ";
        }
        return retour;
    }

    public BufferedImage tableToPNG(){
        int resolution = 4;
        
        image = new BufferedImage(this.initialDimensions.width*resolution, this.initialDimensions.height*resolution, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.setTransform(AffineTransform.getScaleInstance(resolution,resolution));
        transformePlan(g2);
        dessinerMurs(g2);
        dessinerTable(g2, true);
        dessinerPoches(g2);
        dessinerBaguette(g2);
        dessinerPortails(g2);
        dessinerBoules(g2);
        dessinerPointille(g2);
        dessinerGrilleMagnetique(g2);
        g2.dispose();
        return image;

    }

}

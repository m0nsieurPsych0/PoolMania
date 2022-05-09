package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.utils.Polygon2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class TableBillard implements java.io.Serializable {

    private transient ControleurPoolMania controleur;
    private ArrayList<ObjetTable> listeBoulesPoches;
    private GestionMurs murs;
    private double cinetiqueFriction, CoefRestitution, densiteBoules, rayonBoules, threshold = 8.0;
    private transient BufferedImage image;
    private ObjetTable dernierObjetTableSelectionne;
    private Point2D positionClicDernierObjetTableSelectionne;
    private Portail portailBleu, portailOrange;

    public TableBillard(ControleurPoolMania controleur, int nbMurs, double longueurMurs,
                        double angleOrientationMurs, double epaisseurMurs, double rayonBoules, Point2D centre) {

        this.controleur = controleur;

        double angleAdd = 2 * Math.PI / nbMurs;
        double rayonTable = (longueurMurs/2)/Math.sin(angleAdd/2);

        listeBoulesPoches = new ArrayList<ObjetTable>();
        this.rayonBoules = rayonBoules;
        murs = new GestionMurs(nbMurs, rayonTable, angleOrientationMurs, epaisseurMurs, centre);

        double angleInit = 3 * Math.PI / 2;
        double ratioRayonTable = 0.9;
        if (nbMurs == 4) {
            angleInit = Math.PI / 4;
        }
        for (int i = 0; i < nbMurs; i++) {
            double angle = angleInit + angleOrientationMurs - (i * angleAdd);
            double distancePoches = ratioRayonTable*rayonTable - rayonBoules*1.5;
            double x = centre.getX() + Math.cos(angle) * distancePoches;
            double y = centre.getY() + Math.sin(angle) * distancePoches;
            ajouterPoche(new Point2D.Double(x, y), 1.5*rayonBoules); // TODO implémenter possibilité de choisir rayon initial des poches?
        }
        listeBoulesPoches.add(new BouleBlanche(centre, rayonBoules));
        initPropPhysiques();
    }

    private void initPropPhysiques(){
        //https://www.ift.ulaval.ca/uploads/tx_tacticboursedepartement/Livrable.pdf Valeurs inspirées par ça
        cinetiqueFriction = 0.05;
        CoefRestitution = 0.9;
        densiteBoules = 1700;
    }

    public void setControleur(ControleurPoolMania controleur) { this.controleur = controleur; }

    public boolean ajouterPoche(Point2D position, double rayon) {
        Poche nouvellePoche = new Poche(position, rayon);
        if (!isCercleInvalide(nouvellePoche)) {
            listeBoulesPoches.add(nouvellePoche);
            return true;
        }
        return false;
    }

    public boolean ajouterMurObstacleSeul(Point2D position, double angle, double longueur, double epaisseur) {
        Mur nouveauMurObstacle = new Mur(position, angle, longueur, epaisseur);
        ArrayList<Mur> nouvelleListeMursObstacle = new ArrayList<>();
        nouvelleListeMursObstacle.add(nouveauMurObstacle);

        if (!isListeObstaclesInvalide(nouvelleListeMursObstacle) && !isMurObstacleInvalide(nouvelleListeMursObstacle)) {
            murs.ajouterMurObstacleSeul(nouveauMurObstacle);
            return true;
        }
        return false;
    }

    public boolean ajouterBouleColoree(Point2D position, double rayon, Color couleur, int numero) {
        BouleColoree nouvelleBouleColoree = new BouleColoree(position, rayon, couleur, numero);
        if (!isCercleInvalide(nouvelleBouleColoree)) {
            listeBoulesPoches.add(nouvelleBouleColoree);
            return true;
        }
        return false;
    }

    public boolean deplacerPoche(Point2D position, Point2D nouvellePosition) {
        ObjetTable objet = translatePoint(position);
        if (objet instanceof Poche) {
            Poche poche = (Poche)objet;
            Point2D anciennePositionPoche = poche.getPosition();
            poche.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));
            if (!isCercleInvalide(poche))
                return true;
            else {
                poche.setPosition(anciennePositionPoche);
                return false;
            }
        }
        else throw new RuntimeException();
    }

    public boolean deplacerBoule(Point2D position, Point2D nouvellePosition) {
        ObjetTable objet = translatePoint(position);
        if (objet instanceof Boule) {
            Boule boule = (Boule)objet;
            Point2D anciennePositionBoule = boule.getPosition();
            boule.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));
            if (!isCercleInvalide(boule))
                return true;
            else {
                boule.setPosition(anciennePositionBoule);
                return false;
            }
        }
        else throw new RuntimeException();
    }

    public boolean deplacerPortail(Point2D position, Point2D nouvellePosition) {
        ObjetTable objet = translatePoint(position);
        if (objet instanceof Portail) {
            Portail portail = (Portail)objet;
            Point2D anciennePositionBoule = portail.getPosition();
            portail.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));
            if (!isCercleInvalide(portail))
                return true;
            else {
                portail.setPosition(new Point2D.Double(anciennePositionBoule.getX(), anciennePositionBoule.getY()));
                return false;
            }
        }
        else throw new RuntimeException();
    }

    public boolean supprimer(Point2D point) {
        ObjetTable objet = translatePoint(point);
        if (objet == null)
            return false;
        else if (objet instanceof Portail) {
            supprimerPortail((Portail)objet);
            return true;
        }
        else if (objet instanceof Poche) {
            supprimerPoche((Poche)objet);
            return true;
        }
        else if (objet instanceof BouleColoree) {
            supprimerBouleColoree((BouleColoree)objet);
            return true;
        }
        else if (objet instanceof BrisureMur) {
            // C'est peut-être un mur d'obstacle couvert par une brisure
            Mur mur = murs.trouverMur(point);
            if (mur != null)
                if (isMurContour(mur))
                    return false;
                else {
                    supprimerMurObstacle(mur);
                    return true;
                }
            return false;
        }
        else if (objet instanceof Mur) {
            if (isMurContour((Mur)objet))
                return false;
            else {
                supprimerMurObstacle((Mur)objet);
                return true;
            }
        }
        return false;
    }

    public void supprimerPoche(Poche poche) {
        for (int i = 0; i < listeBoulesPoches.size(); i++)
            if (listeBoulesPoches.get(i) == poche) {
                listeBoulesPoches.remove(i);
                break;
            }
    }

    public void supprimerBouleColoree(BouleColoree bouleColoree) {
        for (int i = 0; i < listeBoulesPoches.size(); i++)
            if (listeBoulesPoches.get(i) == bouleColoree) {
                listeBoulesPoches.remove(i);
                break;
            }
    }

    public boolean briserMur(Point2D mousePoint) {
        Mur murTrouve = murs.trouverMur(mousePoint);
        if (murTrouve != null) {
            murs.briserMur(murTrouve);
            return true;
        }
        return false;
    }

    public boolean deplacerBrisureMur(Point2D position, Point2D nouvellePosition) {
        ObjetTable objet = translatePoint(position);
        if (objet instanceof BrisureMur) {
            BrisureMur brisureMur = (BrisureMur)objet;

            ArrayList<BrisureMur> listeBrisuresMur = getListeBrisuresMur();
            for (BrisureMur brisureMurAVerifier : listeBrisuresMur)
                if (brisureMurAVerifier.getPosition().equals(nouvellePosition))
                    return false;

            ArrayList<Mur> mursConnectes = brisureMur.getMursConnectes();
            Mur mur1 = mursConnectes.get(0);
            Mur mur2 = mursConnectes.get(1);

            Point2D positionOriginaleMur1 = mur1.getPosition();
            double angleOriginalMur1 = mur1.getAngle();
            double longueurOriginaleMur1 = mur1.getLongueur();
            double epaisseurOriginaleMur1 = mur1.getEpaisseur();
            Point2D positionOriginaleMur2 = mur2.getPosition();
            double angleOriginalMur2 = mur2.getAngle();
            double longueurOriginaleMur2 = mur2.getLongueur();
            double epaisseurOriginaleMur2 = mur2.getEpaisseur();

            double nouvelAngleMur1 = Math.atan2(nouvellePosition.getY()-mur1.getPosition().getY(),
                    nouvellePosition.getX()-mur1.getPosition().getX());
            mur1.setAngle(nouvelAngleMur1);

            double nouvelleLongueurMur1 = Math.sqrt(Math.pow(nouvellePosition.getX()-mur1.getPosition().getX(),2)
                    + Math.pow(nouvellePosition.getY()-mur1.getPosition().getY(),2));
            mur1.setLongueur(nouvelleLongueurMur1);

            double extremiteMur2X = mur2.getPosition().getX() + mur2.getLongueur() * Math.cos(mur2.getAngle());
            double extremiteMur2Y = mur2.getPosition().getY() + mur2.getLongueur() * Math.sin(mur2.getAngle());

            double nouvelAngleMur2 = Math.atan2(extremiteMur2Y-nouvellePosition.getY(), extremiteMur2X-nouvellePosition.getX());
            mur2.setAngle(nouvelAngleMur2);

            double nouvelleLongueurMur2 = Math.sqrt(Math.pow(extremiteMur2X-nouvellePosition.getX(),2)
                    + Math.pow(extremiteMur2Y-nouvellePosition.getY(),2));
            mur2.setLongueur(nouvelleLongueurMur2);

            mur2.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));

            if (isMurContour(mur1) && isMurContour(mur2)) {
                murs.updateBoundingBoxContoursSansIntersection();

                if (!isContoursInvalide()) {
                    brisureMur.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));
                    return true;
                }
                else {
                    mur1.setPosition(positionOriginaleMur1);
                    mur1.setAngle(angleOriginalMur1);
                    mur1.setLongueur(longueurOriginaleMur1);
                    mur1.setEpaisseur(epaisseurOriginaleMur1);
                    mur2.setPosition(positionOriginaleMur2);
                    mur2.setAngle(angleOriginalMur2);
                    mur2.setLongueur(longueurOriginaleMur2);
                    mur2.setEpaisseur(epaisseurOriginaleMur2);

                    murs.updateBoundingBoxContoursSansIntersection();
                    return false;
                }
            }
            else if (!isMurContour(mur1) && !isMurContour(mur2)) {
                murs.updateBoundingBoxListeObstaclesSansIntersection(murs.getListeObstacles(mur1));

                if (!isListeObstaclesInvalide(murs.getListeObstacles(mur1))) {
                    brisureMur.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));
                    return true;
                }
                else {
                    mur1.setPosition(positionOriginaleMur1);
                    mur1.setAngle(angleOriginalMur1);
                    mur1.setLongueur(longueurOriginaleMur1);
                    mur1.setEpaisseur(epaisseurOriginaleMur1);
                    mur2.setPosition(positionOriginaleMur2);
                    mur2.setAngle(angleOriginalMur2);
                    mur2.setLongueur(longueurOriginaleMur2);
                    mur2.setEpaisseur(epaisseurOriginaleMur2);

                    murs.updateBoundingBoxListeObstaclesSansIntersection(murs.getListeObstacles(mur1));
                    return false;
                }
            }
            else throw new RuntimeException();
        }
        else throw new RuntimeException();
    }

    private void deplacerBrisureMurSansValidation(Point2D position, Point2D nouvellePosition) {
        ObjetTable objet = translatePoint(position);
        if (objet instanceof BrisureMur) {
            BrisureMur brisureMur = (BrisureMur)objet;
            ArrayList<Mur> mursConnectes = brisureMur.getMursConnectes();
            Mur mur1 = mursConnectes.get(0);
            Mur mur2 = mursConnectes.get(1);


            double nouvelAngleMur1 = Math.atan2(nouvellePosition.getY()-mur1.getPosition().getY(),
                    nouvellePosition.getX()-mur1.getPosition().getX());
            mur1.setAngle(nouvelAngleMur1);

            double nouvelleLongueurMur1 = Math.sqrt(Math.pow(nouvellePosition.getX()-mur1.getPosition().getX(),2)
                    + Math.pow(nouvellePosition.getY()-mur1.getPosition().getY(),2));
            mur1.setLongueur(nouvelleLongueurMur1);

            double extremiteMur2X = mur2.getPosition().getX() + mur2.getLongueur() * Math.cos(mur2.getAngle());
            double extremiteMur2Y = mur2.getPosition().getY() + mur2.getLongueur() * Math.sin(mur2.getAngle());

            double nouvelAngleMur2 = Math.atan2(extremiteMur2Y-nouvellePosition.getY(), extremiteMur2X-nouvellePosition.getX());
            mur2.setAngle(nouvelAngleMur2);

            double nouvelleLongueurMur2 = Math.sqrt(Math.pow(extremiteMur2X-nouvellePosition.getX(),2)
                    + Math.pow(extremiteMur2Y-nouvellePosition.getY(),2));
            mur2.setLongueur(nouvelleLongueurMur2);

            mur2.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));

            if (isMurContour(mur1) && isMurContour(mur2)) {
                murs.updateBoundingBoxContoursSansIntersection();

                    brisureMur.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));

            }
            else if (!isMurContour(mur1) && !isMurContour(mur2)) {
                murs.updateBoundingBoxListeObstaclesSansIntersection(murs.getListeObstacles(mur1));
                    brisureMur.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));
            }
            else throw new RuntimeException();
        }
        else throw new RuntimeException();
    }

    private void supprimerBrisureMur(BrisureMur brisureMur) {
        ArrayList<BrisureMur> listeBrisuresMur = murs.getListeBrisuresMur();
        for (int i = 0; i < listeBrisuresMur.size(); i++)
            if (listeBrisuresMur.get(i) == brisureMur) {
                listeBrisuresMur.remove(i);
                return;
            }
        throw new RuntimeException();
    }

    /*
     * Trouve un objet sur la table situé à la position de la souris
     * Retourne null si aucun objet ne se situe à la position
     */
    public ObjetTable translatePoint(Point2D mousePoint) {
        // J'utilise un Mur pour simuler le pixel du mousePoint comme un BoundingBox
        Mur pixelPoint = new Mur(mousePoint, 0, 1, 1);
        for (BrisureMur brisureMur : murs.getListeBrisuresMur())
            if (brisureMur.getBoundingBox().checkCollision(pixelPoint.getBoundingBox()))
                return brisureMur;
        if (portailBleu != null)
            if (portailBleu.getBoundingBox().checkCollision(pixelPoint.getBoundingBox()))
                    return portailBleu;
        if (portailOrange != null)
            if (portailOrange.getBoundingBox().checkCollision(pixelPoint.getBoundingBox()))
        return portailOrange;
        for (ObjetTable objet : listeBoulesPoches)
            if (objet.getBoundingBox().checkCollision(pixelPoint.getBoundingBox()))
                return objet;
        return murs.trouverMur(mousePoint);
    }


    public double getCinetiqueFriction() { return cinetiqueFriction; }

    public boolean setCinetiqueFriction(double nouvCinetiqueFriction) {
        if (nouvCinetiqueFriction >= 0 && nouvCinetiqueFriction <= 1) {
            cinetiqueFriction = nouvCinetiqueFriction;
            return true;
        }
        return false;
    }

    public double getCoefRestitution() { return CoefRestitution; }

    public boolean setCoefRestitution(double nouvCoefRestitution){
        if (nouvCoefRestitution >= 0.1F && nouvCoefRestitution <= 1) {
            CoefRestitution = nouvCoefRestitution;
            return true;
        }
        return false;
    }

    public double getDensiteBoules() { return densiteBoules; }

    public boolean setDensiteBoules(double nouvDensiteBoules){
        if(nouvDensiteBoules > 50) {
            densiteBoules = nouvDensiteBoules;
            return true;
        }
        return false;
    }

    // Poids en kg
    public double getPoidsBoules() { return (4.0/3.0)*Math.PI*Math.pow(rayonBoules/100,3)*densiteBoules; }
    
    public ArrayList<Boule> getListeBoules() {
        ArrayList<Boule> listeBoules = new ArrayList<Boule>();
        for (ObjetTable objet : listeBoulesPoches)
            if (objet instanceof Boule)
                listeBoules.add((Boule) objet);
        return listeBoules;
    }

    public ArrayList<Poche> getListePoches() {
        ArrayList<Poche> listePoches = new ArrayList<Poche>();
        for (ObjetTable objet : listeBoulesPoches)
            if (objet instanceof Poche)
                listePoches.add((Poche) objet);
        return listePoches;
    }

    public ArrayList<Mur> getListeMurs(){
        ArrayList<Mur> listeMurs = new ArrayList<Mur>();
        ArrayList<ArrayList<Mur>> listeListeMursObstacle = murs.getListeListesObstacles();
        for (ArrayList<Mur> listeMursObstacle : listeListeMursObstacle)
            listeMurs.addAll(listeMursObstacle);
        ArrayList<Mur> listeMursContour = murs.getListeMursContour();
        listeMurs.addAll(listeMursContour);
        return listeMurs;
    }

    public Polygon2D getPolygonTable() { return murs.getPolygonTable(); }

    public Polygon2D getPolygonTableBrisure(){
        return murs.getPolygonTableBrisures();
    }

    public ArrayList<BrisureMur> getListeBrisuresMur() { return murs.getListeBrisuresMur(); }

    public boolean isMurContour(Mur mur) { return murs.isMurContour(mur); }

    public ObjetTable getDernierObjetTableSelectionne() { return dernierObjetTableSelectionne; }

    public Point2D getPositionClicDernierObjetTableSelectionne() { return positionClicDernierObjetTableSelectionne; }

    public void setDernierObjetTableSelectionne(Point2D point) {
        dernierObjetTableSelectionne = translatePoint(point);
        positionClicDernierObjetTableSelectionne = point;
    }

    public boolean setRayonBoules(double nouveauRayon) {
        double rayonOriginal = this.rayonBoules;
        this.rayonBoules = nouveauRayon;

        ArrayList<Boule> listeBoules = getListeBoules();
        for (Boule boule : listeBoules)
            boule.getBoundingBox().setRayon(nouveauRayon);

        boolean isInvalide = false;
        for (Boule boule : listeBoules)
            if (isCercleInvalide(boule)) {
                isInvalide = true;
                break;
            }

        if (isInvalide) {
            this.rayonBoules = rayonOriginal;
            for (Boule boule : listeBoules)
                boule.getBoundingBox().setRayon(rayonOriginal);
            return false;
        }
        return true;
    }

    public boolean setCouleurBouleColoree(Point2D position, Color nouvelleCouleur) {
        ObjetTable objet = translatePoint(position);
        if (objet instanceof BouleColoree) {
            BouleColoree bouleColoree = (BouleColoree)objet;
            bouleColoree.setCouleur(nouvelleCouleur);
            return true;
        }
        return false;
    }

    public boolean setRayonPoche(Point2D position, double nouveauRayon) {
        ObjetTable objet = translatePoint(position);
        if (objet instanceof Poche) {
            Poche poche = (Poche)objet;
            double rayonOriginal = poche.getBoundingBox().getRayon();
            poche.getBoundingBox().setRayon(nouveauRayon);
            if (isCercleInvalide(poche)) {
                poche.getBoundingBox().setRayon(rayonOriginal);
                return false;
            }
            return true;
        }
        else throw new RuntimeException();
    }

    public double getWidth(){
        return murs.getPolygonTableBrisures().getBounds().getWidth();
    }

    public double getHeight(){
        return murs.getPolygonTableBrisures().getBounds().getHeight();
    }

    //https://stackoverflow.com/questions/44082498/java-affinetransform-rotate-polygon-then-get-its-points
    public boolean changeHeightAndWidth(double height, double width){
        controleur.addState();
        boolean valide = true;
        Polygon2D polygon = murs.getPolygonTableBrisures();
        double oldHeight = polygon.getBounds2D().getHeight();
        double oldWidth = polygon.getBounds2D().getWidth();
        double scaleX = width/oldWidth;
        double scaleY = height/oldHeight;
        double[] pointsX = polygon.xpoints;
        double[] pointsY = polygon.ypoints;
        AffineTransform transform = new AffineTransform();
        transform.translate(pointsX[0], pointsY[0]);
        transform.scale(scaleX, scaleY);
        transform.translate(-pointsX[0], -pointsY[0]);
        Point2D[] srcPoints = new Point2D[pointsX.length];
        for (int i = 0; i < pointsX.length; i++){
            srcPoints[i] = new Point2D.Double(pointsX[i], pointsY[i]);
        }
        Point2D[] destPoints = new Point2D[srcPoints.length];
        transform.transform(srcPoints, 0, destPoints, 0, srcPoints.length);
        ArrayList<BrisureMur> listeBrisures= murs.getBrisuresMurContour();
        //On ne deplace pas la premiere brisure
        for (int i = 1; i < listeBrisures.size(); i++){
            if(!srcPoints[i].equals(destPoints[i])){
                deplacerBrisureMurSansValidation(srcPoints[i], destPoints[i]);
            }
        }
        valide = !isContoursInvalide();
        if(!valide){
            controleur.undo();
        }
        controleur.removeState();
        return valide;
    }

    public double getRayonBoules() { return rayonBoules; }

    public double getEpaisseurMursContour() { return murs.getEpaisseurMursContour(); }

    public boolean setEpaisseurMursContour(double nouvelleEpaisseur) {
        double epaisseurOriginale = murs.getEpaisseurMursContour();
        murs.setEpaisseurMursContour(nouvelleEpaisseur);

        if (isCollisionContoursContours() || isCollisionContoursCercles()) {
            murs.setEpaisseurMursContour(epaisseurOriginale);
            return false;
        }
        return true;
    }

    // Vérifie pour tous les contours entre-eux
    public boolean isCollisionContoursContours() {
        ArrayList<Mur> listeMursContour = murs.getListeMursContour();
        for (int i = 0; i < listeMursContour.size(); i++)
            for (int j = 0; j < listeMursContour.size(); j++)
                if (i != j) {
                    Mur mur1 = listeMursContour.get(i);
                    Mur mur2 = listeMursContour.get(j);

                    double positionX1Mur1 = mur1.getPosition().getX() + Math.cos(mur1.getAngle());
                    double positionY1Mur1 = mur1.getPosition().getY() + Math.sin(mur1.getAngle());
                    Point2D point1Mur1 = new Point2D.Double(positionX1Mur1, positionY1Mur1);
                    double positionX2Mur1 = positionX1Mur1 + (mur1.getLongueur()-1)*Math.cos(mur1.getAngle());
                    double positionY2Mur1 = positionY1Mur1 + (mur1.getLongueur()-1)*Math.sin(mur1.getAngle());
                    Point2D point2Mur1 = new Point2D.Double(positionX2Mur1, positionY2Mur1);
                    Line2D lineMur1 = new Line2D.Double(point1Mur1, point2Mur1);

                    double positionX1Mur2 = mur2.getPosition().getX() + Math.cos(mur2.getAngle());
                    double positionY1Mur2 = mur2.getPosition().getY() + Math.sin(mur2.getAngle());
                    Point2D point1Mur2 = new Point2D.Double(positionX1Mur2, positionY1Mur2);
                    double positionX2Mur2 = positionX1Mur2 + (mur2.getLongueur()-1)*Math.cos(mur2.getAngle());
                    double positionY2Mur2 = positionY1Mur2 + (mur2.getLongueur()-1)*Math.sin(mur2.getAngle());
                    Point2D point2Mur2 = new Point2D.Double(positionX2Mur2, positionY2Mur2);
                    Line2D lineMur2 = new Line2D.Double(point1Mur2, point2Mur2);

                    if (lineMur1.intersectsLine(lineMur2))
                        return true;
                }
        return false;
    }

    // Vérifie pour tous les contours avec tous les Cercle (Boule ou Poche)
    public boolean isCollisionContoursCercles() {
        ArrayList<Mur> listeMursContour = murs.getListeMursContour();
        for (Mur mur : listeMursContour) {
            for (ObjetTable objet : listeBoulesPoches)
                if (objet instanceof Boule
                        && objet.getBoundingBox().checkCollision(mur.getBoundingBox()))
                    return true;
            for (Portail portail : getListePortails())
                if (portail != null && portail.getBoundingBox().checkCollision(mur.getBoundingBox()))
                    return true;
        }
        return false;
    }

    // Vérifie si tous les objets (Boule, Poche, obstacle) sont dans les contours
    public boolean isTousObjetsDansContour() {
        for (ObjetTable objet : listeBoulesPoches) {
            if (!murs.pointContenuDansContour(objet.getPosition()))
                return false;
            if (objet instanceof Poche)
                if (isPocheDepasseContour((Poche)objet))
                    return false;
        }
        ArrayList<ArrayList<Mur>> listeListesObstacles = murs.getListeListesObstacles();
        for (ArrayList<Mur> listeObstacles : listeListesObstacles) {
            for (Mur obstacle : listeObstacles)
                if (!murs.pointContenuDansContour(obstacle.getPosition()))
                    return false;
        }
        for (Portail portail : getListePortails())
            if (portail != null
                    && !murs.pointContenuDansContour(portail.getPosition()))
                return false;
        return true;
    }

    // Vérifie si tous les objets sont dans le un des contours est invalide
    public boolean isContoursInvalide() {
        return (!isTousObjetsDansContour() || isCollisionContoursContours() || isCollisionContoursCercles());
    }

    // Vérifie si une liste d'obstacles est invalide
    public boolean isListeObstaclesInvalide(ArrayList<Mur> listeObstacles) {
        for (Mur obstacle : listeObstacles)
            if (isMurContour(obstacle))
                throw new RuntimeException();

        for (Mur obstacle : listeObstacles)
            if (!murs.getPolygonTable().contains(obstacle.getBoundingBox().getForme().getBounds2D()))
                return true;

        for (Mur obstacle : listeObstacles)
            for (ObjetTable objet : listeBoulesPoches)
                if (obstacle.getBoundingBox().checkCollision(objet.getBoundingBox()))
                    return true;

        return false;
    }

    private boolean isMurObstacleInvalide(ArrayList<Mur> listeObstacles){
        ArrayList<Mur> listeMurs = getListeMurs();
        for (int i = 0; i < listeObstacles.size(); i++)
            for (int j = 0; j < listeMurs.size(); j++) {
//                Mur murObstacle = listeObstacles.elementAt(i);
                Mur murObstacle = listeObstacles.get(i);
                Mur mur = listeMurs.get(j);
                if (!isMurContour(mur))
                    if (!murs.isObstaclesDansMemeListe(murObstacle, mur))
                        if (mur.getBoundingBox().checkCollision(murObstacle.getBoundingBox()))
                            return true;
                        else
                        if (mur.getBoundingBox().checkCollision(murObstacle.getBoundingBox()))
                            return true;
            }
        return false;
    }

    public boolean isCercleInvalide(ObjetTable objetAVerifier) {
        if (!(objetAVerifier.getBoundingBox() instanceof Cercle))
            throw new RuntimeException();

        for (Portail portail : getListePortails())
            if (portail != null && objetAVerifier != portail
                    && objetAVerifier.getBoundingBox().checkCollision(portail.getBoundingBox()))
                return true;

        for (ObjetTable objet : listeBoulesPoches)
            if (objetAVerifier.getBoundingBox() != objet.getBoundingBox())
                if (objetAVerifier.getBoundingBox().checkCollision(objet.getBoundingBox()))
                    return true;

        if (objetAVerifier instanceof Boule) {
            ArrayList<Mur> listeMurs = getListeMurs();
            for (Mur mur : listeMurs)
                if (objetAVerifier.getBoundingBox().checkCollision(mur.getBoundingBox()))
                    return true;
        }

        if (objetAVerifier instanceof Poche)
            if (isPocheDepasseContour((Poche)objetAVerifier))
                return true;

        return !murs.pointContenuDansContour(objetAVerifier.getPosition());
    }

    public ArrayList<Mur> getCopieListeObstacles(ArrayList<Mur> listeObstacles) {
        for (Mur obstacle : listeObstacles)
            if (isMurContour(obstacle))
                throw new RuntimeException();

        return new ArrayList<Mur>(listeObstacles);
    }

    public boolean deplacerMurObstacle(Point2D positionCentre, Point2D nouvellePosition) {
        Mur mur = murs.trouverMur(positionCentre);
        if (mur == null) {
            return false;
        }

        if (mur != getDernierObjetTableSelectionne()) {
            return false;
        }

        if (isMurContour(mur))
            throw new RuntimeException();

        ArrayList<Mur> copieListeObstaclesOriginale = getCopieListeObstacles(murs.getListeObstacles(mur));
        //TODO Je préfère laisser le mur de la table sinon, dans certain cas c'est contraingnant.
//        if (!murs.getPolygonTable().contains(mur.getBoundingBox().getForme().getBounds2D())){
//            // S'assure qu'on ne peut pas déplacer librement le mur à l'extérieur de la table
//            return false;
//        }

        if (copieListeObstaclesOriginale.size() == 1) {
            mur.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));

            if (connecterObstacleSeulAListeObstacles(mur, null, null))
                murs.updateBoundingBoxListeObstaclesSansIntersection(murs.getListeObstacles(mur));

            if (isListeObstaclesInvalide(murs.getListeObstacles(mur))) {
                return false;
            }
        }
        else {
            ArrayList<BrisureMur> listeBrisuresAvantApresMur = murs.getListeBrisuresPourUnMur(mur);
            for (BrisureMur brise: listeBrisuresAvantApresMur){
                // Pas le choix de passer par la liste de mur attaché ensemble pour avoir la coordonnée exacte sinon, ça retourne la coordonnée centrale du groupe.
                for(Mur murBrise: brise.getMursConnectes()){
                    if(murBrise == mur){
                        if(Math.abs(nouvellePosition.getX()) > Math.abs(murBrise.getPosition().getX()) + threshold || Math.abs(nouvellePosition.getY()) > Math.abs(murBrise.getPosition().getY()) + threshold){
                            murs.separerObstacleDeListeObstacles(mur);

                            mur.setPosition(new Point2D.Double(nouvellePosition.getX(), nouvellePosition.getY()));

                            for (Mur murOriginal : copieListeObstaclesOriginale)
                                murs.updateBoundingBoxListeObstaclesSansIntersection(
                                        murs.getListeObstacles(murOriginal));

                            int indexMur = copieListeObstaclesOriginale.indexOf(mur);
                            int indexMurAvant = (indexMur == 0) ? copieListeObstaclesOriginale.size()-1 : indexMur-1;
                            int indexMurApres = (indexMur == copieListeObstaclesOriginale.size()-1) ? 0 : indexMur+1;

                            Mur murOriginalementConnecteGauche = copieListeObstaclesOriginale.get(indexMurAvant);
                            Mur murOriginalementConnecteDroite = copieListeObstaclesOriginale.get(indexMurApres);

                            if (connecterObstacleSeulAListeObstacles(mur, murOriginalementConnecteGauche, murOriginalementConnecteDroite))
                                murs.updateBoundingBoxListeObstaclesSansIntersection(murs.getListeObstacles(mur));

                            if (isListeObstaclesInvalide(murs.getListeObstacles(mur))) {
                                return false;
                            }
                            supprimerBrisureMur(listeBrisuresAvantApresMur.get(0));
                            if (listeBrisuresAvantApresMur.size() != 1)
                                supprimerBrisureMur(listeBrisuresAvantApresMur.get(1));
                        }
                        break;
                    }
                }

            }
            
        }

        return true;
    }

    // Inspiré de : https://stackoverflow.com/a/705474/17213452
    public boolean setAngleMurObstacle(Point2D positionCentre, double nouvelAngle) {
        Mur mur = murs.trouverMur(positionCentre);
        if (mur == null)
            throw new RuntimeException();

        if (isMurContour(mur))
            throw new RuntimeException();

        controleur.addState();

        ArrayList<Mur> listeObstacles = murs.getListeObstacles(mur);

        double diffAngle = nouvelAngle - mur.getAngle();

        double positionCentreObstacleX = mur.getPosition().getX() + (mur.getLongueur()/2)*Math.cos(mur.getAngle());
        double positionCentreObstacleY = mur.getPosition().getY() + (mur.getLongueur()/2)*Math.sin(mur.getAngle());

        for (Mur obstacle : listeObstacles) {
            double nouvellePositionObstacleX = (obstacle.getPosition().getX() - positionCentreObstacleX) * Math.cos(diffAngle)
                    - (obstacle.getPosition().getY() - positionCentreObstacleY) * Math.sin(diffAngle) + positionCentreObstacleX;
            double nouvellePositionObstacleY = (obstacle.getPosition().getX() - positionCentreObstacleX) * Math.sin(diffAngle)
                    + (obstacle.getPosition().getY() - positionCentreObstacleY) * Math.cos(diffAngle) + positionCentreObstacleY;

            Point2D nouvellePositionObstacle = new Point2D.Double(nouvellePositionObstacleX, nouvellePositionObstacleY);
            obstacle.setPosition(new Point2D.Double(nouvellePositionObstacle.getX(), nouvellePositionObstacle.getY()));
            obstacle.setAngle(obstacle.getAngle() + diffAngle);
        }

        murs.updateBoundingBoxListeObstaclesSansIntersection(listeObstacles);

        if (isListeObstaclesInvalide(listeObstacles)) {
            controleur.undo();
            controleur.removeState();

            return false;
        }

        if (listeObstacles.size() != 1) {
            for (int i = 1; i < listeObstacles.size(); i++) {
                BrisureMur brisureMur = murs.getListeBrisuresPourUnMur(listeObstacles.get(i)).get(0);
                double nouvellePositionBrisureX = (brisureMur.getPosition().getX()-positionCentreObstacleX)*Math.cos(diffAngle)
                        - (brisureMur.getPosition().getY()-positionCentreObstacleY)*Math.sin(diffAngle) + positionCentreObstacleX;
                double nouvellePositionBrisureY = (brisureMur.getPosition().getX()-positionCentreObstacleX)*Math.sin(diffAngle)
                        + (brisureMur.getPosition().getY()-positionCentreObstacleY)*Math.cos(diffAngle) + positionCentreObstacleY;
                brisureMur.setPosition(new Point2D.Double(nouvellePositionBrisureX, nouvellePositionBrisureY));
            }
        }
        controleur.removeState();

        return true;
    }

    // Inspiré de : https://stackoverflow.com/a/705474/17213452
    public boolean rotationMurObstacleAvecBrisureMur(Point2D position, double angleRotation) {
        ObjetTable objet = translatePoint(position);

        if (!(objet instanceof BrisureMur))
            throw new RuntimeException();

        BrisureMur brisureMurDeRotation = (BrisureMur)objet;

        if (isMurContour(brisureMurDeRotation.getMursConnectes().get(0)))
            throw new RuntimeException();

        controleur.addState();

        ArrayList<Mur> listeObstacles = murs.getListeObstacles(brisureMurDeRotation.getMursConnectes().get(0));

        double positionBrisureMurDeRotationX = brisureMurDeRotation.getPosition().getX();
        double positionBrisureMurDeRotationY = brisureMurDeRotation.getPosition().getY();

        for (Mur obstacle : listeObstacles) {
            double nouvellePositionObstacleX = (obstacle.getPosition().getX() - positionBrisureMurDeRotationX) * Math.cos(angleRotation)
                    - (obstacle.getPosition().getY() - positionBrisureMurDeRotationY) * Math.sin(angleRotation) + positionBrisureMurDeRotationX;
            double nouvellePositionObstacleY = (obstacle.getPosition().getX() - positionBrisureMurDeRotationX) * Math.sin(angleRotation)
                    + (obstacle.getPosition().getY() - positionBrisureMurDeRotationY) * Math.cos(angleRotation) + positionBrisureMurDeRotationY;

            Point2D nouvellePositionObstacle = new Point2D.Double(nouvellePositionObstacleX, nouvellePositionObstacleY);
            obstacle.setPosition(nouvellePositionObstacle);
            obstacle.setAngle(obstacle.getAngle() + angleRotation);
        }

        murs.updateBoundingBoxListeObstaclesSansIntersection(listeObstacles);

        if (isListeObstaclesInvalide(listeObstacles)) {
            controleur.undo();
            controleur.removeState();

            return false;
        }

        if (listeObstacles.size() != 1) {
            for (int i = 1; i < listeObstacles.size(); i++) {
                BrisureMur brisureMur = murs.getListeBrisuresPourUnMur(listeObstacles.get(i)).get(0);
                double nouvellePositionBrisureX = (brisureMur.getPosition().getX()-positionBrisureMurDeRotationX)*Math.cos(angleRotation)
                        - (brisureMur.getPosition().getY()-positionBrisureMurDeRotationY)*Math.sin(angleRotation) + positionBrisureMurDeRotationX;
                double nouvellePositionBrisureY = (brisureMur.getPosition().getX()-positionBrisureMurDeRotationX)*Math.sin(angleRotation)
                        + (brisureMur.getPosition().getY()-positionBrisureMurDeRotationY)*Math.cos(angleRotation) + positionBrisureMurDeRotationY;
                brisureMur.setPosition(new Point2D.Double(nouvellePositionBrisureX, nouvellePositionBrisureY));
            }
        }
        controleur.removeState();

        return true;
    }

    public boolean setLongueurMurObstacle(Point2D positionCentre, double nouvelleLongueur) {
        Mur mur = murs.trouverMur(positionCentre);
        if (mur == null)
            throw new RuntimeException();

        if (isMurContour(mur))
            throw new RuntimeException();

        controleur.addState();

        ArrayList<Mur> listeObstacles = murs.getListeObstacles(mur);

        if (listeObstacles.size() == 1) {
            double diffLongueur = nouvelleLongueur - mur.getLongueur();

            mur.setLongueur(nouvelleLongueur);
            double nouvellePositionX = mur.getPosition().getX() - (diffLongueur/2)*Math.cos(mur.getAngle());
            double nouvellePositionY = mur.getPosition().getY() - (diffLongueur/2)*Math.sin(mur.getAngle());
            mur.setPosition(new Point2D.Double(nouvellePositionX, nouvellePositionY));

            if (isListeObstaclesInvalide(listeObstacles)) {
                controleur.undo();
                controleur.removeState();

                return false;
            }
        }
        else {
            double diffLongueur = nouvelleLongueur - mur.getLongueur();

            double diffPositionX = (diffLongueur/2)*Math.cos(mur.getAngle());
            double diffPositionY = (diffLongueur/2)*Math.sin(mur.getAngle());

            mur.setLongueur(nouvelleLongueur);

            int indexMur = listeObstacles.indexOf(mur);

            for (int i = 0; i < indexMur+1; i++) {
                Mur obstacle = listeObstacles.get(i);
                double nouvellePositionObstacleX = obstacle.getPosition().getX() - diffPositionX;
                double nouvellePositionObstacleY = obstacle.getPosition().getY() - diffPositionY;
                Point2D nouvellePositionObstacle = new Point2D.Double(nouvellePositionObstacleX, nouvellePositionObstacleY);

                obstacle.setPosition(nouvellePositionObstacle);
            }

            for (int i = indexMur+1; i < listeObstacles.size(); i++) {
                Mur obstacle = listeObstacles.get(i);
                double nouvellePositionObstacleX = obstacle.getPosition().getX() + diffPositionX;
                double nouvellePositionObstacleY = obstacle.getPosition().getY() + diffPositionY;
                Point2D nouvellePositionObstacle = new Point2D.Double(nouvellePositionObstacleX, nouvellePositionObstacleY);

                obstacle.setPosition(nouvellePositionObstacle);
            }

            murs.updateBoundingBoxListeObstaclesSansIntersection(listeObstacles);

            if (isListeObstaclesInvalide(listeObstacles)) {
                controleur.undo();
                controleur.removeState();

                return false;
            }

            for (int i = 1; i < indexMur+1; i++) {
                BrisureMur brisureMur = murs.getListeBrisuresPourUnMur(listeObstacles.get(i)).get(0);
                double nouvellePositionBrisureX = brisureMur.getPosition().getX() - diffPositionX;
                double nouvellePositionBrisureY = brisureMur.getPosition().getY() - diffPositionY;
                brisureMur.setPosition(new Point2D.Double(nouvellePositionBrisureX, nouvellePositionBrisureY));
            }

            for (int i = indexMur+1; i < listeObstacles.size(); i++) {
                BrisureMur brisureMur = murs.getListeBrisuresPourUnMur(listeObstacles.get(i)).get(0);
                double nouvellePositionBrisureX = brisureMur.getPosition().getX() + diffPositionX;
                double nouvellePositionBrisureY = brisureMur.getPosition().getY() + diffPositionY;
                brisureMur.setPosition(new Point2D.Double(nouvellePositionBrisureX, nouvellePositionBrisureY));
            }
        }
        controleur.removeState();

        return true;
    }

    public boolean setEpaisseurMurObstacle(Point2D positionCentre, double nouvelleEpaisseur) {
        Mur mur = murs.trouverMur(positionCentre);
        if (mur == null)
            throw new RuntimeException();

        if (isMurContour(mur))
            throw new RuntimeException();

        double epaisseurOriginale = mur.getEpaisseur();

        ArrayList<Mur> listeObstacles = murs.getListeObstacles(mur);
        for (Mur obstacle : listeObstacles)
            obstacle.setEpaisseur(nouvelleEpaisseur);
        murs.updateBoundingBoxListeObstaclesSansIntersection(listeObstacles);

        if (isListeObstaclesInvalide(listeObstacles)) {
            for (Mur obstacle : listeObstacles)
                obstacle.setEpaisseur(epaisseurOriginale);
            murs.updateBoundingBoxListeObstaclesSansIntersection(listeObstacles);
            return false;
        }

        for (Mur obstacle : listeObstacles)
            for (BrisureMur brisureMur : murs.getListeBrisuresPourUnMur(obstacle))
                brisureMur.getBoundingBox().setRayon(nouvelleEpaisseur / 4);
        return true;
    }

    public void supprimerMurObstacle(Mur mur) {
        if (isMurContour(mur))
            throw new RuntimeException();

        ArrayList<Mur> listeObstacles = murs.getListeObstacles(mur);

        if (listeObstacles.size() == 1)
            murs.getListeListesObstacles().remove(listeObstacles);
        else {
            int indexMurASupprimer = listeObstacles.indexOf(mur);
            int indexMurApres = (indexMurASupprimer == listeObstacles.size()-1) ? 0 : indexMurASupprimer+1;
            Mur murApres = listeObstacles.get(indexMurApres);

            ArrayList<BrisureMur> listeBrisuresAvantApresMur = murs.getListeBrisuresPourUnMur(mur);
            supprimerBrisureMur(listeBrisuresAvantApresMur.get(0));
            if (listeBrisuresAvantApresMur.size() == 2)
                supprimerBrisureMur(listeBrisuresAvantApresMur.get(1));

            murs.separerObstacleDeListeObstacles(mur);
            murs.getListeListesObstacles().remove(murs.getListeObstacles(mur));

            murs.updateBoundingBoxListeObstaclesSansIntersection(listeObstacles);
            murs.updateBoundingBoxListeObstaclesSansIntersection(murs.getListeObstacles(murApres));
        }
    }

    public boolean connecterObstacleSeulAListeObstacles(Mur obstacleAConnecter, Mur obstacleOriginalementConnecteGauche, Mur obstacleOriginalementConnecteDroite) {
        if (isMurContour(obstacleAConnecter))
            throw new RuntimeException();

        for (ArrayList<Mur> listeObstacles : murs.getListeListesObstacles())
            if (listeObstacles.contains(obstacleAConnecter))
                if (listeObstacles.size() != 1)
                    throw new RuntimeException();

        double epaisseurObstacleAConnecter = obstacleAConnecter.getEpaisseur();
        Cercle cercleGaucheObstacleAConnecter = new Cercle(obstacleAConnecter.getPosition(), epaisseurObstacleAConnecter/2);
        double positionCercleDroiteObstacleAConnecterX = obstacleAConnecter.getPosition().getX()
                + obstacleAConnecter.getLongueur()*Math.cos(obstacleAConnecter.getAngle());
        double positionCercleDroiteObstacleAConnecterY = obstacleAConnecter.getPosition().getY()
                + obstacleAConnecter.getLongueur()*Math.sin(obstacleAConnecter.getAngle());
        Cercle cercleDroiteObstacleAConnecter = new Cercle(new Point2D.Double(positionCercleDroiteObstacleAConnecterX, positionCercleDroiteObstacleAConnecterY),
                epaisseurObstacleAConnecter/2);

        for (ArrayList<Mur> listeObstacles : murs.getListeListesObstacles()) {
            if (!listeObstacles.contains(obstacleAConnecter) && !listeObstacles.contains(obstacleOriginalementConnecteGauche)
                    && !listeObstacles.contains(obstacleOriginalementConnecteDroite)) {
                Mur obstacleGauche = listeObstacles.get(0);

                double epaisseurListeObstacles = obstacleGauche.getEpaisseur();
                Cercle cercleGaucheObstacleGauche = new Cercle(obstacleGauche.getPosition(), epaisseurListeObstacles/2);

                Mur obstacleDroite = listeObstacles.get(listeObstacles.size()-1);

                double positionCercleDroiteObstacleDroiteX = obstacleDroite.getPosition().getX()
                        + obstacleDroite.getLongueur()*Math.cos(obstacleDroite.getAngle());
                double positionCercleDroiteObstacleDroiteY = obstacleDroite.getPosition().getY()
                        + obstacleDroite.getLongueur()*Math.sin(obstacleDroite.getAngle());
                Cercle cercleDroiteObstacleDroite = new Cercle(new Point2D.Double(positionCercleDroiteObstacleDroiteX, positionCercleDroiteObstacleDroiteY),
                        epaisseurListeObstacles/2);

                if ((cercleGaucheObstacleAConnecter.checkCollision(cercleGaucheObstacleGauche) && cercleDroiteObstacleAConnecter.checkCollision(cercleGaucheObstacleGauche))
                        || (cercleGaucheObstacleAConnecter.checkCollision(cercleGaucheObstacleGauche) && cercleDroiteObstacleAConnecter.checkCollision(cercleDroiteObstacleDroite))
                        || (cercleGaucheObstacleAConnecter.checkCollision(cercleDroiteObstacleDroite) && cercleDroiteObstacleAConnecter.checkCollision(cercleGaucheObstacleGauche))
                        || (cercleGaucheObstacleAConnecter.checkCollision(cercleDroiteObstacleDroite) && cercleDroiteObstacleAConnecter.checkCollision(cercleDroiteObstacleDroite)))
                    return false;

                if (cercleGaucheObstacleGauche.checkCollision(cercleGaucheObstacleAConnecter)
                        && !obstacleAConnecter.getBoundingBox().checkCollision(obstacleGauche.getBoundingBox())) {
                    obstacleAConnecter.setPosition(new Point2D.Double(obstacleGauche.getPosition().getX(), obstacleGauche.getPosition().getY()));

                    double positionCentreObstacleAConnecterX = obstacleAConnecter.getPosition().getX()
                            + (obstacleAConnecter.getLongueur()/2)*Math.cos(obstacleAConnecter.getAngle());
                    double positionCentreObstacleAConnecterY = obstacleAConnecter.getPosition().getY()
                            + (obstacleAConnecter.getLongueur()/2)*Math.sin(obstacleAConnecter.getAngle());
                    setAngleMurObstacle(new Point2D.Double(positionCentreObstacleAConnecterX, positionCentreObstacleAConnecterY),
                            obstacleAConnecter.getAngle()+Math.PI);

                    double epaisseurListe = listeObstacles.get(0).getEpaisseur();

                    murs.getListeListesObstacles().remove(murs.getListeObstacles(obstacleAConnecter));
                    listeObstacles.add(0, obstacleAConnecter);

                    if (epaisseurListe > epaisseurObstacleAConnecter)
                        obstacleAConnecter.setEpaisseur(epaisseurListe);
                    else if (epaisseurListe < epaisseurObstacleAConnecter)
                        for (Mur obstacle : listeObstacles)
                            obstacle.setEpaisseur(epaisseurObstacleAConnecter);

                    BrisureMur brisureMur = new BrisureMur(listeObstacles.get(1).getPosition(),
                            obstacleAConnecter, listeObstacles.get(1));
                    murs.getListeBrisuresMur().add(brisureMur);

                    return true;
                }

                if (cercleGaucheObstacleGauche.checkCollision(cercleDroiteObstacleAConnecter)
                        && !obstacleAConnecter.getBoundingBox().checkCollision(obstacleGauche.getBoundingBox())) {
                    double positionObstacleAConnecterX = obstacleGauche.getPosition().getX()
                            - obstacleAConnecter.getLongueur()*Math.cos(obstacleAConnecter.getAngle());
                    double positionObstacleAConnecterY = obstacleGauche.getPosition().getY()
                            - obstacleAConnecter.getLongueur()*Math.sin(obstacleAConnecter.getAngle());

                    obstacleAConnecter.setPosition(new Point2D.Double(positionObstacleAConnecterX, positionObstacleAConnecterY));

                    double epaisseurListe = listeObstacles.get(0).getEpaisseur();

                    murs.getListeListesObstacles().remove(murs.getListeObstacles(obstacleAConnecter));
                    listeObstacles.add(0, obstacleAConnecter);

                    if (epaisseurListe > epaisseurObstacleAConnecter)
                        obstacleAConnecter.setEpaisseur(epaisseurListe);
                    else if (epaisseurListe < epaisseurObstacleAConnecter)
                        for (Mur obstacle : listeObstacles)
                            obstacle.setEpaisseur(epaisseurObstacleAConnecter);

                    BrisureMur brisureMur = new BrisureMur(listeObstacles.get(1).getPosition(),
                            obstacleAConnecter, listeObstacles.get(1));
                    murs.getListeBrisuresMur().add(brisureMur);

                    return true;
                }

                if (cercleDroiteObstacleDroite.checkCollision(cercleGaucheObstacleAConnecter)
                        && !obstacleAConnecter.getBoundingBox().checkCollision(obstacleDroite.getBoundingBox())) {
                    double positionObstacleAConnecterX = obstacleDroite.getPosition().getX()
                            + obstacleDroite.getLongueur()*Math.cos(obstacleDroite.getAngle());
                    double positionObstacleAConnecterY = obstacleDroite.getPosition().getY()
                            + obstacleDroite.getLongueur()*Math.sin(obstacleDroite.getAngle());

                    obstacleAConnecter.setPosition(new Point2D.Double(positionObstacleAConnecterX, positionObstacleAConnecterY));

                    double epaisseurListe = listeObstacles.get(0).getEpaisseur();

                    murs.getListeListesObstacles().remove(murs.getListeObstacles(obstacleAConnecter));
                    listeObstacles.add(obstacleAConnecter);

                    if (epaisseurListe > epaisseurObstacleAConnecter)
                        obstacleAConnecter.setEpaisseur(epaisseurListe);
                    else if (epaisseurListe < epaisseurObstacleAConnecter)
                        for (Mur obstacle : listeObstacles)
                            obstacle.setEpaisseur(epaisseurObstacleAConnecter);

                    BrisureMur brisureMur = new BrisureMur(listeObstacles.get(listeObstacles.size()-1).getPosition(),
                            listeObstacles.get(listeObstacles.size()-2), obstacleAConnecter);
                    murs.getListeBrisuresMur().add(brisureMur);

                    return true;
                }

                if (cercleDroiteObstacleDroite.checkCollision(cercleDroiteObstacleAConnecter)
                        && !obstacleAConnecter.getBoundingBox().checkCollision(obstacleDroite.getBoundingBox())) {
                    double positionObstacleAConnecterX = obstacleDroite.getPosition().getX()
                            + obstacleDroite.getLongueur()*Math.cos(obstacleDroite.getAngle())
                            - obstacleAConnecter.getLongueur()*Math.cos(obstacleAConnecter.getAngle());
                    double positionObstacleAConnecterY = obstacleDroite.getPosition().getY()
                            + obstacleDroite.getLongueur()*Math.sin(obstacleDroite.getAngle())
                            - obstacleAConnecter.getLongueur()*Math.sin(obstacleAConnecter.getAngle());

                    obstacleAConnecter.setPosition(new Point2D.Double(positionObstacleAConnecterX, positionObstacleAConnecterY));

                    double positionCentreObstacleAConnecterX = obstacleAConnecter.getPosition().getX()
                            + (obstacleAConnecter.getLongueur()/2)*Math.cos(obstacleAConnecter.getAngle());
                    double positionCentreObstacleAConnecterY = obstacleAConnecter.getPosition().getY()
                            + (obstacleAConnecter.getLongueur()/2)*Math.sin(obstacleAConnecter.getAngle());
                    setAngleMurObstacle(new Point2D.Double(positionCentreObstacleAConnecterX, positionCentreObstacleAConnecterY),
                            obstacleAConnecter.getAngle()+Math.PI);

                    double epaisseurListe = listeObstacles.get(0).getEpaisseur();

                    murs.getListeListesObstacles().remove(murs.getListeObstacles(obstacleAConnecter));
                    listeObstacles.add(obstacleAConnecter);

                    if (epaisseurListe > epaisseurObstacleAConnecter)
                        obstacleAConnecter.setEpaisseur(epaisseurListe);
                    else if (epaisseurListe < epaisseurObstacleAConnecter)
                        for (Mur obstacle : listeObstacles)
                            obstacle.setEpaisseur(epaisseurObstacleAConnecter);

                    BrisureMur brisureMur = new BrisureMur(listeObstacles.get(listeObstacles.size()-1).getPosition(),
                            listeObstacles.get(listeObstacles.size()-2), obstacleAConnecter);
                    murs.getListeBrisuresMur().add(brisureMur);

                    return true;
                }
            }
        }
        return false;
    }

    // Vérifie si une poche dépasse le contour (puisqu'on ne vérifie pas pour les collisions)
    public boolean isPocheDepasseContour(Poche poche) {
        ArrayList<Mur> listeMursContour = murs.getListeMursContour();
        for (Mur mur : listeMursContour) {
            double positionX1Mur = mur.getPosition().getX()+(mur.getEpaisseur()/2)*Math.cos(mur.getAngle()+Math.PI/2);
            double positionY1Mur = mur.getPosition().getY()+(mur.getEpaisseur()/2)*Math.sin(mur.getAngle()+Math.PI/2);
            Point2D point1Mur = new Point2D.Double(positionX1Mur, positionY1Mur);
            double positionX2Mur = positionX1Mur+mur.getLongueur()*Math.cos(mur.getAngle());
            double positionY2Mur = positionY1Mur+mur.getLongueur()*Math.sin(mur.getAngle());
            Point2D point2Mur = new Point2D.Double(positionX2Mur, positionY2Mur);
            Line2D lineMur = new Line2D.Double(point1Mur, point2Mur);

            if (lineMur.ptSegDist(poche.getPosition()) < poche.getBoundingBox().getRayon())
                return true;
        }
        return false;
    }

    public void removeBoule(Boule boule){
        listeBoulesPoches.remove(boule);
    }

    public double getHauteurTable() {
        return murs.getPolygonTableBrisures().getBounds2D().getHeight();
    }

    public double getLargeurTable() {
        return murs.getPolygonTableBrisures().getBounds2D().getWidth();
    }

    public BouleBlanche getBouleBlanche() {
        ArrayList<Boule> listeBoules = getListeBoules();
        for (int i = 0; i < listeBoules.size(); i++) {
            if (listeBoules.get(i) instanceof BouleBlanche) {
                return (BouleBlanche) listeBoules.get(i);
            }
        }
        return null;
    }

    public boolean ajouterBouleBlanche(Point2D position, double rayon) {
        BouleBlanche nouvelleBouleBlanche = new BouleBlanche(position, rayon);
        if (!isCercleInvalide(nouvelleBouleBlanche)) {
            listeBoulesPoches.add(nouvelleBouleBlanche);
            return true;
        }
        return false;
    }

    public ObjetTable objetCollisionBouleBlancheImaginaire(double positionX, double positionY) {
        Boule bouleBlancheImaginaire = new Boule(new Point2D.Double(positionX, positionY), rayonBoules);
        BoundingBox boundingBoxBouleBlancheImaginaire = bouleBlancheImaginaire.getBoundingBox();
        ArrayList<Portail> listePortails = getListePortails();
        if (listePortails.get(0) != null && listePortails.get(1) != null)
            for (ObjetTable objet : listePortails)
                if (boundingBoxBouleBlancheImaginaire.checkCollision(objet.getBoundingBox()))
                    return objet;
        for (ObjetTable objet : getListeMurs())
            if (boundingBoxBouleBlancheImaginaire.checkCollision(objet.getBoundingBox()))
                return objet;
        for (ObjetTable objet : listeBoulesPoches) {
            if (!(objet instanceof BouleBlanche) && boundingBoxBouleBlancheImaginaire.checkCollision(objet.getBoundingBox()))
                return objet;
        }
        return null;
    }

    public ArrayList<Mur> getListeMursObstacles(){
        ArrayList<ArrayList<Mur>> listeListeObstacles = murs.getListeListesObstacles();
        ArrayList<Mur> listeObstacles = new ArrayList<Mur>();
        for(ArrayList<Mur> listeMurs : listeListeObstacles){
            for(Mur mur: listeMurs){
                listeObstacles.add(mur);
            }
        }
        return listeObstacles;
    }

    // Inspiré de : https://stackoverflow.com/a/59466733
    // Inspiré de : https://stackoverflow.com/a/705474/17213452
    public boolean ajouterPaquetBoulesColorees(Point2D position, double anglePaquet) {
        double rayonBoules = 1.25*getRayonBoules(); // Si on les colle plus ensemble ya une infinité de collisions

        double positionBoule1X = position.getX();
        double positionBoule1Y = position.getY()+2*Math.sqrt(3)*rayonBoules;
        double rotationBoule1X = ((positionBoule1X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule1Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule1Y = ((positionBoule1X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule1Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule1 = new BouleColoree(new Point2D.Double(rotationBoule1X, rotationBoule1Y), getRayonBoules(), Color.yellow, 1);
        if (isCercleInvalide(boule1))
            return false;

        double positionBoule2X = position.getX()-rayonBoules;
        double positionBoule2Y = position.getY()+Math.sqrt(3)*rayonBoules;
        double rotationBoule2X = ((positionBoule2X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule2Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule2Y = ((positionBoule2X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule2Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule2 = new BouleColoree(new Point2D.Double(rotationBoule2X, rotationBoule2Y), getRayonBoules(), Color.blue, 2);
        if (isCercleInvalide(boule2))
            return false;

        double positionBoule3X = position.getX()+rayonBoules;
        double positionBoule3Y = position.getY()+Math.sqrt(3)*rayonBoules;
        double rotationBoule3X = ((positionBoule3X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule3Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule3Y = ((positionBoule3X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule3Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule3 = new BouleColoree(new Point2D.Double(rotationBoule3X, rotationBoule3Y), getRayonBoules(), Color.red, 3);
        if (isCercleInvalide(boule3))
            return false;

        double positionBoule4X = position.getX()-2*rayonBoules;
        double positionBoule4Y = position.getY();
        double rotationBoule4X = ((positionBoule4X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule4Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule4Y = ((positionBoule4X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule4Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule4 = new BouleColoree(new Point2D.Double(rotationBoule4X, rotationBoule4Y), getRayonBoules(), new Color(128,0,128), 4);
        if (isCercleInvalide(boule4))
            return false;

        double positionBoule5X = position.getX();
        double positionBoule5Y = position.getY();
        double rotationBoule5X = ((positionBoule5X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule5Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule5Y = ((positionBoule5X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule5Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule5 = new BouleColoree(new Point2D.Double(rotationBoule5X, rotationBoule5Y), getRayonBoules(), Color.orange, 5);
        if (isCercleInvalide(boule5))
            return false;

        double positionBoule6X = position.getX()+2*rayonBoules;
        double positionBoule6Y = position.getY();
        double rotationBoule6X = ((positionBoule6X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule6Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule6Y = ((positionBoule6X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule6Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule6 = new BouleColoree(new Point2D.Double(rotationBoule6X, rotationBoule6Y), getRayonBoules(), Color.green, 6);
        if (isCercleInvalide(boule6))
            return false;

        double positionBoule7X = position.getX()-3*rayonBoules;
        double positionBoule7Y = position.getY()-Math.sqrt(3)*rayonBoules;
        double rotationBoule7X = ((positionBoule7X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule7Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule7Y = ((positionBoule7X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule7Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule7 = new BouleColoree(new Point2D.Double(rotationBoule7X, rotationBoule7Y), getRayonBoules(), new Color(128,0,32), 7);
        if (isCercleInvalide(boule7))
            return false;

        double positionBoule8X = position.getX()-rayonBoules;
        double positionBoule8Y = position.getY()-Math.sqrt(3)*rayonBoules;
        double rotationBoule8X = ((positionBoule8X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule8Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule8Y = ((positionBoule8X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule8Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule8 = new BouleColoree(new Point2D.Double(rotationBoule8X, rotationBoule8Y), getRayonBoules(), Color.black, 8);
        if (isCercleInvalide(boule8))
            return false;

        double positionBoule9X = position.getX()+rayonBoules;
        double positionBoule9Y = position.getY()-Math.sqrt(3)*rayonBoules;
        double rotationBoule9X = ((positionBoule9X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule9Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule9Y = ((positionBoule9X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule9Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule9 = new BouleColoree(new Point2D.Double(rotationBoule9X, rotationBoule9Y), getRayonBoules(), Color.yellow, 9);
        if (isCercleInvalide(boule9))
            return false;

        double positionBoule10X = position.getX()+3*rayonBoules;
        double positionBoule10Y = position.getY()-Math.sqrt(3)*rayonBoules;
        double rotationBoule10X = ((positionBoule10X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule10Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule10Y = ((positionBoule10X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule10Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule10 = new BouleColoree(new Point2D.Double(rotationBoule10X, rotationBoule10Y), getRayonBoules(), Color.blue, 10);
        if (isCercleInvalide(boule10))
            return false;

        double positionBoule11X = position.getX()-4*rayonBoules;
        double positionBoule11Y = position.getY()-2*Math.sqrt(3)*rayonBoules;
        double rotationBoule11X = ((positionBoule11X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule11Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule11Y = ((positionBoule11X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule11Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule11 = new BouleColoree(new Point2D.Double(rotationBoule11X, rotationBoule11Y), getRayonBoules(), Color.red, 11);
        if (isCercleInvalide(boule11))
            return false;

        double positionBoule12X = position.getX()-2*rayonBoules;
        double positionBoule12Y = position.getY()-2*Math.sqrt(3)*rayonBoules;
        double rotationBoule12X = ((positionBoule12X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule12Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule12Y = ((positionBoule12X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule12Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule12 = new BouleColoree(new Point2D.Double(rotationBoule12X, rotationBoule12Y), getRayonBoules(), new Color(128,0,128), 12);
        if (isCercleInvalide(boule12))
            return false;

        double positionBoule13X = position.getX();
        double positionBoule13Y = position.getY()-2*Math.sqrt(3)*rayonBoules;
        double rotationBoule13X = ((positionBoule13X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule13Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule13Y = ((positionBoule13X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule13Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule13 = new BouleColoree(new Point2D.Double(rotationBoule13X, rotationBoule13Y), getRayonBoules(), Color.orange, 13);
        if (isCercleInvalide(boule13))
            return false;

        double positionBoule14X = position.getX()+2*rayonBoules;
        double positionBoule14Y = position.getY()-2*Math.sqrt(3)*rayonBoules;
        double rotationBoule14X = ((positionBoule14X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule14Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule14Y = ((positionBoule14X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule14Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule14 = new BouleColoree(new Point2D.Double(rotationBoule14X, rotationBoule14Y), getRayonBoules(), Color.green, 14);
        if (isCercleInvalide(boule14))
            return false;

        double positionBoule15X = position.getX()+4*rayonBoules;
        double positionBoule15Y = position.getY()-2*Math.sqrt(3)*rayonBoules;
        double rotationBoule15X = ((positionBoule15X - position.getX()) * Math.cos(anglePaquet))
                - ((positionBoule15Y - position.getY()) * Math.sin(anglePaquet)) + position.getX();
        double rotationBoule15Y = ((positionBoule15X - position.getX()) * Math.sin(anglePaquet))
                + ((positionBoule15Y - position.getY()) * Math.cos(anglePaquet)) + position.getY();
        BouleColoree boule15 = new BouleColoree(new Point2D.Double(rotationBoule15X, rotationBoule15Y), getRayonBoules(), new Color(128,0,32), 15);
        if (isCercleInvalide(boule15))
            return false;

        listeBoulesPoches.add(boule1);
        listeBoulesPoches.add(boule2);
        listeBoulesPoches.add(boule3);
        listeBoulesPoches.add(boule4);
        listeBoulesPoches.add(boule5);
        listeBoulesPoches.add(boule6);
        listeBoulesPoches.add(boule7);
        listeBoulesPoches.add(boule8);
        listeBoulesPoches.add(boule9);
        listeBoulesPoches.add(boule10);
        listeBoulesPoches.add(boule11);
        listeBoulesPoches.add(boule12);
        listeBoulesPoches.add(boule13);
        listeBoulesPoches.add(boule14);
        listeBoulesPoches.add(boule15);

        return true;
    }

    public Point getTableOrigine(){
        int minX = (int)getPolygonTable().xpoints[0];
        for (int i = 1; i < getPolygonTable().xpoints.length; i++){
            int x = (int)getPolygonTable().xpoints[i];
            if(x < minX){
                minX = x;
            }
        }

        int minY = (int)getPolygonTable().ypoints[0];
        for (int i = 1; i < getPolygonTable().ypoints.length; i++){
            int x = (int)getPolygonTable().ypoints[i];
            if(x < minY){
                minY = x;
            }
        }
        return new Point((int)(minX - getEpaisseurMursContour()*2), (int)(minY- getEpaisseurMursContour()*2));
    }

    public boolean ajouterPortail(Point2D position, double angle, double longueur, boolean couleur) {
        if (couleur) {
            if (portailBleu != null)
                return false;
        }
        else {
            if (portailOrange != null)
                return false;
        }

        Portail nouveauPortail = new Portail(position, angle, longueur, couleur);
        if (!isCercleInvalide(nouveauPortail)) {
            if (couleur) portailBleu = nouveauPortail;
            else portailOrange = nouveauPortail;
            return true;
        }
        return false;
    }

    public void supprimerPortail(Portail portail) {
        if (portail == portailBleu)
            portailBleu = null;
        else if (portail == portailOrange)
            portailOrange = null;
        else throw new RuntimeException();
    }

    public boolean couleurPortailDejaAjoute(boolean couleur) {
        if (couleur)
            return portailBleu != null;
        else
            return portailOrange != null;
    }

    public ArrayList<Portail> getListePortails() {
        ArrayList<Portail> listePortails = new ArrayList<>();
        listePortails.add(portailBleu);
        listePortails.add(portailOrange);
        return listePortails;
    }

    public boolean setAnglePortail(Point2D positionCentre, double nouvelAngle) {
        ObjetTable objet = translatePoint(positionCentre);
        if (!(objet instanceof Portail))
            throw new RuntimeException();
        Portail portail = (Portail)objet;

        portail.setAngle(nouvelAngle);
        return true;
    }

    public boolean setLongueurPortail(Point2D positionCentre, double nouvelleLongueur) {
        ObjetTable objet = translatePoint(positionCentre);
        if (!(objet instanceof Portail))
            throw new RuntimeException();
        Portail portail = (Portail)objet;

        double longueurOriginal = portail.getLongueur();
        portail.setLongueur(nouvelleLongueur);
        if (isCercleInvalide(portail)) {
            portail.setLongueur(longueurOriginal);
            return false;
        }
        return true;
    }

    public boolean isTeleportPortailInvalide(Boule boule) {
        for (ObjetTable objet : listeBoulesPoches)
            if (boule.getBoundingBox() != objet.getBoundingBox())
                if (boule.getBoundingBox().checkCollision(objet.getBoundingBox()))
                    return true;

        ArrayList<Mur> listeMurs = getListeMurs();
        for (Mur mur : listeMurs)
            if (boule.getBoundingBox().checkCollision(mur.getBoundingBox()))
                return true;

        return !murs.pointContenuDansContour(boule.getPosition());
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    //https://stackoverflow.com/questions/15058663/how-to-serialize-an-object-that-includes-bufferedimages
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (image != null){
            ImageIO.write(image, "png", out); // png is lossless
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
    }

    public boolean bouleBlanchePresente() {
        boolean retour = false;
        for (Boule b: getListeBoules()) {
            if(b instanceof BouleBlanche){
                if(!getPolygonTable().contains(b.getPosition())) {
                    retirerBouleBlanche();
                }
                retour = true;
            }
        }
        return retour;
    }

    public void retirerBouleBlanche() {
        for (Boule b: getListeBoules()) {
            if(b instanceof BouleBlanche){
                removeBoule(b);
            }
        }
    }
}

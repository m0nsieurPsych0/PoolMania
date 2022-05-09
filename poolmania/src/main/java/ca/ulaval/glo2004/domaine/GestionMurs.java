package ca.ulaval.glo2004.domaine;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ca.ulaval.glo2004.utils.Polygon2D;

public class GestionMurs implements java.io.Serializable {

    private ArrayList<Mur> listeMursContour;
    private ArrayList<ArrayList<Mur>> listeListesObstacles;
    private ArrayList<BrisureMur> listeBrisuresMur;

    private double epaisseurMursContour;

    public GestionMurs(int nbMurs, double rayonTable, double angleOrientationMurs, double epaisseurMursContour, Point2D centre) {
        if (nbMurs < 3) throw new RuntimeException();
        this.listeMursContour = new ArrayList<>();
        this.listeListesObstacles = new ArrayList<ArrayList<Mur>>();
        this.listeBrisuresMur = new ArrayList<BrisureMur>();
        this.epaisseurMursContour = epaisseurMursContour;
        //Les murs sont construits à partir d'un cercle.

        double angleAdd = 2 * Math.PI / nbMurs;
        for (int i = 0; i < nbMurs; i++) {
            double angleInit = (nbMurs == 4) ? Math.PI/4 : 3*Math.PI/2;
            double angle = angleInit + angleOrientationMurs - i * angleAdd;
            double x = centre.getX() + Math.cos(angle) * rayonTable;
            double y = centre.getY() + Math.sin(angle) * rayonTable;
            double prochainAngle = angleInit + angleOrientationMurs - (i+1) * angleAdd;
            double prochainX = centre.getX() + Math.cos(prochainAngle) * rayonTable;
            double prochainY = centre.getY() + Math.sin(prochainAngle) * rayonTable;
            double angleMur = Math.atan2(prochainY-y, prochainX-x);
            double distEntreMurs = Math.sqrt(Math.pow(prochainY-y,2) + Math.pow(prochainX-x,2));
            Mur mur = new Mur(new Point2D.Double(x, y), angleMur, distEntreMurs, epaisseurMursContour);
            listeMursContour.add(mur);
        }
        for (int i = 0; i < nbMurs; i++) {
            int indexMurAvant = (i == 0) ? nbMurs-1 : i-1;
            listeBrisuresMur.add(new BrisureMur(listeMursContour.get(i).getPosition(),
                    listeMursContour.get(indexMurAvant), listeMursContour.get(i)));
        }
        updateBoundingBoxContoursSansIntersection();
    }

    public void briserMur(Mur mur) {
        BrisureMur brisureMurDejaPresente = null;
        for (BrisureMur brisureMur : listeBrisuresMur)
            if (brisureMur.getMursConnectes().get(0) == mur)
                brisureMurDejaPresente = brisureMur;

        mur.setLongueur(mur.getLongueur()/2);

        double positionNouveauMurX = mur.getPosition().getX() + mur.getLongueur() * Math.cos(mur.getAngle());
        double positionNouveauMurY = mur.getPosition().getY() + mur.getLongueur() * Math.sin(mur.getAngle());
        Point2D pointNouveauMur = new Point2D.Double(positionNouveauMurX, positionNouveauMurY);
        Mur nouveauMur = new Mur(pointNouveauMur, mur.getAngle(), mur.getLongueur(), mur.getEpaisseur());

        Point2D pointBrisure = new Point2D.Double(positionNouveauMurX, positionNouveauMurY);
        BrisureMur nouvelleBrisureMur = new BrisureMur(pointBrisure, mur, nouveauMur);

        //trouver brisure d'avant
        int i;
        for(i = 0; i < listeBrisuresMur.size(); i++){
            if(listeBrisuresMur.get(i).getMur1() == mur){
                break;
            }
        }
        if(isMurContour(mur)){
            listeBrisuresMur.add(i+1, nouvelleBrisureMur);

        } else {
            listeBrisuresMur.add(i, nouvelleBrisureMur);
        }

        if (brisureMurDejaPresente != null)
            brisureMurDejaPresente.getMursConnectes().set(0, nouveauMur);

        if (isMurContour(mur)) {
            ajouterMurContour(mur, nouveauMur);
            updateBoundingBoxContoursSansIntersection();
        }
        else {
            ajouterMurObstacleApresBriser(mur, nouveauMur);
            updateBoundingBoxListeObstaclesSansIntersection(getListeObstacles(mur));
        }
    }

    /*
     * Trouve un mur sur la table situé à la position de la souris
     * Retourne null si aucun mur ne se situe à la position
     */
    public Mur trouverMur(Point2D mousePoint) {
        // J'utilise un Mur pour simuler le pixel du mousePoint comme un BoundingBox
        Mur pixelPoint = new Mur(mousePoint, 0, 1, 1);
        for (ArrayList<Mur> listeObstacles : listeListesObstacles)
            for (Mur obstacle : listeObstacles)
                if (pixelPoint.getBoundingBox().checkCollision(obstacle.getBoundingBox()))
                    return obstacle;
        for (Mur mur : listeMursContour)
            if (pixelPoint.getBoundingBox().checkCollision(mur.getBoundingBox()))
                return mur;
        return null;
    }

    private void ajouterMurContour(Mur murOriginal, Mur murComplementaire) {
        int indexMurOriginal = listeMursContour.indexOf(murOriginal);
        if (indexMurOriginal == -1) throw new RuntimeException();
        int indexNouveauMur = indexMurOriginal + 1;
        listeMursContour.add(indexNouveauMur, murComplementaire);
        //resetBoundingBoxContourAvantUpdate();
        for (int i = 0; i < listeMursContour.size(); i++) {
            int indexMurAvant = (i == 0) ? listeMursContour.size()-1 : i-1;
            int indexMurApres = (i == listeMursContour.size()-1) ? 0 : i+1;
            listeMursContour.get(i).updateBoundingBoxMurSansIntersection(listeMursContour.get(indexMurAvant));
            if (indexMurAvant != indexMurApres)
                listeMursContour.get(i).updateBoundingBoxMurSansIntersection(listeMursContour.get(indexMurApres));
        }
    }

    public void ajouterMurObstacleSeul(Mur mur) {
        ArrayList<Mur> listeMur = new ArrayList<>();
        listeMur.add(mur);
        listeListesObstacles.add(listeMur);
    }

    public Polygon2D getPolygonTable() {
        int nbMursContour = listeMursContour.size();
        double[] pointsX = new double[nbMursContour];
        double[] pointsY = new double[nbMursContour];
        for (int i = 0; i < nbMursContour; i++) {
            Mur mur = listeMursContour.get(i);
            pointsX[i] = mur.getPosition().getX() + (mur.getEpaisseur()/2)*Math.cos(mur.getAngle());
            pointsY[i] = mur.getPosition().getY() + (mur.getEpaisseur()/2)*Math.sin(mur.getAngle());
        }
        return new Polygon2D(pointsX, pointsY, nbMursContour);
    }

    public Polygon2D getPolygonTableBrisures() {
        ArrayList<BrisureMur> listeBrisuresMurContour = getBrisuresMurContour();
        int nbBrisures = listeBrisuresMurContour.size();
        double[] pointsX = new double[nbBrisures];
        double[] pointsY = new double[nbBrisures];
        for (int i = 0; i < nbBrisures; i++) {
            BrisureMur mur = listeBrisuresMurContour.get(i);
            pointsX[i] = mur.getPosition().getX();
            pointsY[i] = mur.getPosition().getY();
        }
        return new Polygon2D(pointsX, pointsY, nbBrisures);
    }

    public ArrayList<BrisureMur> getBrisuresMurContour() {
        ArrayList<BrisureMur> listeRetour = new ArrayList<BrisureMur>();
        for (BrisureMur brisure : listeBrisuresMur) {
            if(isMurContour(brisure.getMursConnectes().get(0))){
                listeRetour.add(brisure);
            }
        }
        return listeRetour;
    }

    public boolean pointContenuDansContour(Point2D point) { return getPolygonTable().contains(point); }

    public ArrayList<Mur> getListeMursContour(){
        return listeMursContour;
    }

    public ArrayList<ArrayList<Mur>> getListeListesObstacles(){
        return listeListesObstacles;
    }

    public ArrayList<BrisureMur> getListeBrisuresMur() { return listeBrisuresMur; }

    public double getEpaisseurMursContour() { return epaisseurMursContour; }

    public void resetBoundingBoxContoursAvantUpdate() {
        for (Mur mur : listeMursContour)
            mur.resetBoundingBoxAvantUpdate();
    }

    public void updateBoundingBoxContoursSansIntersection() {
        resetBoundingBoxContoursAvantUpdate();

        int nbMursContour = listeMursContour.size();
        for (int i = 0; i < nbMursContour; i++) {
            int indexMurAvant = (i == 0) ? nbMursContour-1 : i-1;
            int indexMurApres = (i == nbMursContour-1) ? 0 : i+1;
            listeMursContour.get(i).updateBoundingBoxMurSansIntersection(listeMursContour.get(indexMurAvant));
            if (indexMurAvant != indexMurApres)
                listeMursContour.get(i).updateBoundingBoxMurSansIntersection(listeMursContour.get(indexMurApres));
        }
    }

    public void setEpaisseurMursContour(double nouvelleEpaisseur) {
        for (Mur mur : listeMursContour)
            mur.setEpaisseur(nouvelleEpaisseur);
        for (BrisureMur brisureMur : listeBrisuresMur)
            brisureMur.getBoundingBox().setRayon(nouvelleEpaisseur / 4);
        epaisseurMursContour = nouvelleEpaisseur;

        updateBoundingBoxContoursSansIntersection();
    }

    public boolean isMurContour(Mur mur) {
        for (Mur murAVerifier : listeMursContour)
            if (murAVerifier == mur)
                return true;
        return false;
    }

    public boolean isObstaclesDansMemeListe(Mur mur1, Mur mur2) {
        if (isMurContour(mur1) || isMurContour(mur2))
            throw new RuntimeException();

        if (mur1 == mur2)
            return true;

        int indexListeQuiContientMur1 = -1;
        int indexListeQuiContientMur2 = -1;
        for (int i = 0; i < listeListesObstacles.size(); i++) {
            ArrayList<Mur> listeObstacles = listeListesObstacles.get(i);
            for (Mur obstacle : listeObstacles) {
                if (obstacle == mur1)
                    indexListeQuiContientMur1 = i;
                if (obstacle == mur2)
                    indexListeQuiContientMur2 = i;
            }
        }

        if (indexListeQuiContientMur1 == -1 && indexListeQuiContientMur2 == -1)
            throw new RuntimeException();

        return (indexListeQuiContientMur1 == indexListeQuiContientMur2);
    }

    public ArrayList<Mur> getListeObstacles(Mur mur) {
        if (isMurContour(mur))
            throw new RuntimeException();

        for (ArrayList<Mur> listeObstacles : listeListesObstacles) {
            for (int i = 0; i < listeObstacles.size(); i++)
                if (listeObstacles.get(i) == mur)
                    return listeObstacles;
        }
        throw new RuntimeException();
    }

    public void separerObstacleDeListeObstacles(Mur mur) {
        if (isMurContour(mur))
            throw new RuntimeException();

        ArrayList<Mur> listeObstacles = getListeObstacles(mur);

        if (listeObstacles.size() == 1)
            throw new RuntimeException();

        for (int i = 0; i < listeObstacles.size(); i++) {
            if (listeObstacles.get(i) == mur) {
                ajouterMurObstacleSeul(mur);
                listeObstacles.remove(mur);

                if (i < listeObstacles.size()-1) {
                    Mur obstacleSepare = listeObstacles.get(i);
                    ajouterMurObstacleSeul(obstacleSepare);
                    listeObstacles.remove(obstacleSepare);

                    ArrayList<Mur> listeSepareObstacles = getListeObstacles(obstacleSepare);
                    for (int j = i; j < listeObstacles.size(); j++) {
                        listeSepareObstacles.add(listeObstacles.get(j));
                        listeObstacles.remove(listeObstacles.get(j));
                    }
                }
                else if (i == listeObstacles.size()-1) {
                    Mur obstacleSepare = listeObstacles.get(i);
                    ajouterMurObstacleSeul(obstacleSepare);
                    listeObstacles.remove(obstacleSepare);
                }

                for (ArrayList<Mur> listeObstaclesAVerifier : listeListesObstacles)
                    if (listeObstaclesAVerifier.size() == 0) {
                        listeListesObstacles.remove(listeObstaclesAVerifier);
                        break;
                    }

                return;
            }
        }
        throw new RuntimeException();
    }

    public void ajouterMurObstacleApresBriser(Mur murOriginal, Mur murComplementaire) {
        ArrayList<Mur> listeObstacles = getListeObstacles(murOriginal);

        int indexMurOriginal = listeObstacles.indexOf(murOriginal);
        if (indexMurOriginal == -1) throw new RuntimeException();
        int indexNouveauMur = indexMurOriginal + 1;
        listeObstacles.add(indexNouveauMur, murComplementaire);
    }

    public void updateBoundingBoxListeObstaclesSansIntersection(ArrayList<Mur> listeObstacles) {
        for (Mur obstacle : listeObstacles)
            if (isMurContour(obstacle))
                throw new RuntimeException();

        resetBoundingBoxListeObstaclesAvantUpdate(listeObstacles);

        if (listeObstacles.size() != 1) {
            for (int i = 0; i < listeObstacles.size(); i++) {
                if (i != 0)
                    listeObstacles.get(i).updateBoundingBoxMurSansIntersection(listeObstacles.get(i-1));
                if (i != listeObstacles.size()-1)
                    listeObstacles.get(i).updateBoundingBoxMurSansIntersection(listeObstacles.get(i+1));
            }
        }
    }

    public ArrayList<BrisureMur> getListeBrisuresPourUnMur(Mur mur) {
        ArrayList<BrisureMur> listeBrisuresARetourner = new ArrayList<BrisureMur>();

        ArrayList<BrisureMur> listeBrisuresMur = getListeBrisuresMur();
        for (BrisureMur brisureMur : listeBrisuresMur)
            if (brisureMur.getMursConnectes().get(1) == mur)
                listeBrisuresARetourner.add(brisureMur);
        // On sépare la recherche en deux pour garder l'ordre des brisures dans la liste qu'on retourne
        for (BrisureMur brisureMur : listeBrisuresMur)
            if (brisureMur.getMursConnectes().get(0) == mur)
                listeBrisuresARetourner.add(brisureMur);

        return listeBrisuresARetourner;
    }

    public void resetBoundingBoxListeObstaclesAvantUpdate(ArrayList<Mur> listeObstacles) {
        for (Mur obstacle : listeObstacles)
            if (isMurContour(obstacle))
                throw new RuntimeException();

        for (Mur obstacle : listeObstacles)
            obstacle.resetBoundingBoxAvantUpdate();
    }

}

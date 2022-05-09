package ca.ulaval.glo2004.domaine.simulateurBillard;

import ca.ulaval.glo2004.domaine.*;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class SimulateurDeCoup{

    private ControleurPoolMania controleur;
    private Vector<Clip> clipVector;
    private final int tailleVector = 9;
    public enum TypeObjetTable { Boule, Poche, Mur };
    private boolean resetterForceTir = false;

    public SimulateurDeCoup(ControleurPoolMania controleur) {
        this.controleur = controleur;
        audioInit();
    }

    public void playSound(TypeObjetTable type){
        try{
            if(type == TypeObjetTable.Boule){
                int i = 0;
                Clip myClip = clipVector.get(i);
                while(myClip.isRunning()){
                    i = i + 3;
                    myClip = clipVector.get(i);
                }
                FloatControl gainControl =
                        (FloatControl) myClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMaximum()/2);
                myClip.start();
                myClip.setFramePosition(0);
            }else if(type == TypeObjetTable.Mur){
                int i = 1;
                Clip myClip = clipVector.get(i);
                while(myClip.isRunning()){
                    i = i + 3;
                    myClip = clipVector.get(i);
                }
                FloatControl gainControl =
                        (FloatControl) myClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMaximum()/2);
                myClip.start();
                myClip.setFramePosition(0);
            }else if(type == TypeObjetTable.Poche){
                int i = 2;
                Clip myClip = clipVector.get(i);
                while(myClip.isRunning()){
                    i = i + 3;
                    myClip = clipVector.get(i);
                }
                FloatControl gainControl =
                        (FloatControl) myClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMaximum()/2);
                myClip.start();
                myClip.setFramePosition(0);
            }
        }catch(Exception e){
        }
    }

    private void audioInit(){
        try {
            clipVector = new Vector<Clip>(tailleVector);
            URL ballUrl = getClass().getResource("/billiard1.wav");
            URL wallUrl = getClass().getResource("/billiard2.wav");
            URL holeUrl = getClass().getResource("/billiard3.wav");
            for(int i = 3; i < tailleVector + 3; i++){
                Clip myClip = AudioSystem.getClip();
                if(i % 3 == 0){
                    myClip.open(AudioSystem.getAudioInputStream(ballUrl));
                }else if(i % 3 == 1){
                    myClip.open(AudioSystem.getAudioInputStream(wallUrl));
                }else if(i % 3 == 2){
                    myClip.open(AudioSystem.getAudioInputStream(holeUrl));
                }
                //mute et start les clips puis remettre à 0
                FloatControl gainControl =
                        (FloatControl) myClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMinimum());
                myClip.start();
                myClip.setFramePosition(0);
                clipVector.add(myClip);
            }
        }catch(Exception e){
            System.out.println("Audio non chargé");
        }
    }



    public void collisionBouleMur(Boule boule, Mur mur) {
        double coteGaucheMur1X = mur.getPosition().getX()+(mur.getEpaisseur()/2-1)*Math.cos(mur.getAngle()+Math.PI/2);
        double coteGaucheMur1Y = mur.getPosition().getY()+(mur.getEpaisseur()/2-1)*Math.sin(mur.getAngle()+Math.PI/2);
        Point2D coteGaucheMur1 = new Point2D.Double(coteGaucheMur1X, coteGaucheMur1Y);
        double coteGaucheMur2X = mur.getPosition().getX()-(mur.getEpaisseur()/2-1)*Math.cos(mur.getAngle()+Math.PI/2);
        double coteGaucheMur2Y = mur.getPosition().getY()-(mur.getEpaisseur()/2-1)*Math.sin(mur.getAngle()+Math.PI/2);
        Point2D coteGaucheMur2 = new Point2D.Double(coteGaucheMur2X, coteGaucheMur2Y);
        Line2D coteGaucheMur = new Line2D.Double(coteGaucheMur1, coteGaucheMur2);
        double distBouleCoteGaucheMur = coteGaucheMur.ptSegDist(boule.getPosition());

        double coteDroiteMur1X = mur.getPosition().getX()+(mur.getEpaisseur()/2-1)*Math.cos(mur.getAngle()+Math.PI/2)
                +mur.getLongueur()*Math.cos(mur.getAngle());
        double coteDroiteMur1Y = mur.getPosition().getY()+(mur.getEpaisseur()/2-1)*Math.sin(mur.getAngle()+Math.PI/2)
                +mur.getLongueur()*Math.sin(mur.getAngle());
        Point2D coteDroiteMur1 = new Point2D.Double(coteDroiteMur1X, coteDroiteMur1Y);
        double coteDroiteMur2X = mur.getPosition().getX()-(mur.getEpaisseur()/2-1)*Math.cos(mur.getAngle()+Math.PI/2)
                +mur.getLongueur()*Math.cos(mur.getAngle());
        double coteDroiteMur2Y = mur.getPosition().getY()-(mur.getEpaisseur()/2-1)*Math.sin(mur.getAngle()+Math.PI/2)
                +mur.getLongueur()*Math.sin(mur.getAngle());
        Point2D coteDroiteMur2 = new Point2D.Double(coteDroiteMur2X, coteDroiteMur2Y);
        Line2D coteDroiteMur = new Line2D.Double(coteDroiteMur1, coteDroiteMur2);
        double distBouleCoteDroiteMur = coteDroiteMur.ptSegDist(boule.getPosition());

        double coteHautMur1X = mur.getPosition().getX()+(mur.getEpaisseur()/2)*Math.cos(mur.getAngle()+Math.PI/2)
                +Math.cos(mur.getAngle());
        double coteHautMur1Y = mur.getPosition().getY()+(mur.getEpaisseur()/2)*Math.sin(mur.getAngle()+Math.PI/2)
                +Math.sin(mur.getAngle());
        Point2D coteHautMur1 = new Point2D.Double(coteHautMur1X, coteHautMur1Y);
        double coteHautMur2X = mur.getPosition().getX()+(mur.getEpaisseur()/2)*Math.cos(mur.getAngle()+Math.PI/2)
                +(mur.getLongueur()-1)*Math.cos(mur.getAngle());
        double coteHautMur2Y = mur.getPosition().getY()+(mur.getEpaisseur()/2)*Math.sin(mur.getAngle()+Math.PI/2)
                +(mur.getLongueur()-1)*Math.sin(mur.getAngle());
        Point2D coteHautMur2 = new Point2D.Double(coteHautMur2X, coteHautMur2Y);
        Line2D coteHautMur = new Line2D.Double(coteHautMur1, coteHautMur2);
        double distBouleCoteHautMur = coteHautMur.ptSegDist(boule.getPosition());

        double coteBasMur1X = mur.getPosition().getX()-(mur.getEpaisseur()/2)*Math.cos(mur.getAngle()+Math.PI/2)
                +Math.cos(mur.getAngle());
        double coteBasMur1Y = mur.getPosition().getY()-(mur.getEpaisseur()/2)*Math.sin(mur.getAngle()+Math.PI/2)
                +Math.sin(mur.getAngle());
        Point2D coteBasMur1 = new Point2D.Double(coteBasMur1X, coteBasMur1Y);
        double coteBasMur2X = mur.getPosition().getX()-(mur.getEpaisseur()/2)*Math.cos(mur.getAngle()+Math.PI/2)
                +(mur.getLongueur()-1)*Math.cos(mur.getAngle());
        double coteBasMur2Y = mur.getPosition().getY()-(mur.getEpaisseur()/2)*Math.sin(mur.getAngle()+Math.PI/2)
                +(mur.getLongueur()-1)*Math.sin(mur.getAngle());
        Point2D coteBasMur2 = new Point2D.Double(coteBasMur2X, coteBasMur2Y);
        Line2D coteBasMur = new Line2D.Double(coteBasMur1, coteBasMur2);
        double distBouleCoteBasMur = coteBasMur.ptSegDist(boule.getPosition());
        double angleMur;

        if ((distBouleCoteGaucheMur < distBouleCoteHautMur
                && distBouleCoteGaucheMur < distBouleCoteBasMur)
                || (distBouleCoteDroiteMur < distBouleCoteHautMur
                && distBouleCoteDroiteMur < distBouleCoteBasMur))
            angleMur = mur.getAngle()+Math.PI/2;
        else
            angleMur = mur.getAngle();

        double vitesseY = boule.getVitesseY();
        double vitesseX = boule.getVitesseX();
        double CoefRestitution = controleur.getCoefRestitution();

        double angleBalle = Math.atan2(vitesseY, vitesseX);
        if (angleBalle < 0)
            angleBalle += 2*Math.PI;

        if (Math.abs(angleMur - angleBalle) > Math.PI/2) {
            if (angleMur < Math.PI)
                angleMur += Math.PI;
            else
                angleMur -= Math.PI;
        }

        if (angleBalle < angleMur)
            angleBalle = angleMur + Math.abs(angleBalle - angleMur);
        else
            angleBalle = angleMur - Math.abs(angleBalle - angleMur);

        if (angleBalle > 2*Math.PI)
            angleBalle -= 2*Math.PI;
        else if (angleBalle < 0)
            angleBalle += 2*Math.PI;

        double vitesseFinale = CoefRestitution*Math.sqrt(Math.pow(vitesseX,2) + Math.pow(vitesseY,2));

        double angleCorrection = Math.atan2(vitesseY, vitesseX);
        if (angleCorrection < 0)
            angleCorrection += 2*Math.PI;

        //Tentative de fix les problèmes de collision trop rapide avec mur
        double dist = distance(new Point2D.Double(boule.getPosition().getX(), boule.getPosition().getY()), mur.getPolygon(), 1);
        dist = -dist + controleur.getRayonBoules();
        if(dist > 0) {
            boule.setPosition(new Point2D.Double(boule.getPosition().getX() + dist*Math.cos(mur.getAngle() - Math.PI/2), boule.getPosition().getY() + dist*Math.sin(mur.getAngle() - Math.PI/2)));
        }
        //fin
        boule.setVitesse(vitesseFinale*Math.cos(angleBalle)*controleur.getCoefRestitution(), vitesseFinale*Math.sin(angleBalle)*controleur.getCoefRestitution());
    }

    // Inspiré du pseudo-code du livrable 3
    public void collisionBouleBoule(Boule boule1, Boule boule2) {
        double normaleX = boule2.getPosition().getX() - boule1.getPosition().getX();
        double normaleY = boule2.getPosition().getY() - boule1.getPosition().getY();

        double[] vecteurUnitaire = calculerVecteurUnitaireVitesse(normaleX, normaleY);

        double vitesse1x = boule1.getVitesseX()*controleur.getCoefRestitution();
        double vitesse1y = boule1.getVitesseY()*controleur.getCoefRestitution();
        double vitesse2x = boule2.getVitesseX()*controleur.getCoefRestitution();
        double vitesse2y = boule2.getVitesseY()*controleur.getCoefRestitution();
        
        if (vecteurUnitaire != null) {
            double[] tangenteUnitaire = new double[] {-vecteurUnitaire[1], vecteurUnitaire[0]};

            double vitesseNormale1X = (vecteurUnitaire[0]*vitesse2x + vecteurUnitaire[1]*vitesse2y)*vecteurUnitaire[0];
            double vitesseNormale1Y = (vecteurUnitaire[0]*vitesse2x + vecteurUnitaire[1]*vitesse2y)*vecteurUnitaire[1];

            double vitesseTangente1X = (tangenteUnitaire[0]*vitesse1x + tangenteUnitaire[1]*vitesse1y)*tangenteUnitaire[0];
            double vitesseTangente1Y = (tangenteUnitaire[0]*vitesse1x + tangenteUnitaire[1]*vitesse1y)*tangenteUnitaire[1];

            double vitesseNormale2X = (vecteurUnitaire[0]*vitesse1x + vecteurUnitaire[1]*vitesse1y)*vecteurUnitaire[0];
            double vitesseNormale2Y = (vecteurUnitaire[0]*vitesse1x + vecteurUnitaire[1]*vitesse1y)*vecteurUnitaire[1];

            double vitesseTangente2X = (tangenteUnitaire[0]*vitesse2x + tangenteUnitaire[1]*vitesse2y)*tangenteUnitaire[0];
            double vitesseTangente2Y = (tangenteUnitaire[0]*vitesse2x + tangenteUnitaire[1]*vitesse2y)*tangenteUnitaire[1];

            double dist = -BoundingBox.distanceEntrePoints(boule1.getPosition(), boule2.getPosition()) + (controleur.getRayonBoules()*2);
            dist = dist/2;
            double angleBoule = Math.atan2(boule2.getPosition().getY() - boule1.getPosition().getY(),boule2.getPosition().getX() - boule1.getPosition().getX());
            boule1.setPosition(new Point2D.Double(boule1.getPosition().getX() + dist*Math.cos(angleBoule - Math.PI), boule1.getPosition().getY() + dist*Math.sin(angleBoule - Math.PI)));
            boule2.setPosition(new Point2D.Double(boule2.getPosition().getX() + dist*Math.cos(angleBoule), boule2.getPosition().getY() + dist*Math.sin(angleBoule)));

            boule1.setVitesse((vitesseNormale1X+vitesseTangente1X) , (vitesseNormale1Y+vitesseTangente1Y) );
            boule2.setVitesse((vitesseNormale2X+vitesseTangente2X) , (vitesseNormale2Y+vitesseTangente2Y) );
        }
    }

    // Inspiré du pseudo-code du livrable 3
    public double[] calculerVecteurUnitaireVitesse(double normaleX, double normaleY) {
        double pente = Math.sqrt(Math.pow(normaleX,2) + Math.pow(normaleY,2));

        if (pente > 0)
            return new double[] {normaleX/pente, normaleY/pente};

        return null;
    }

    public boolean ballesImmobiles(){
        ArrayList<Boule> listeBoules = controleur.getListeBoules();
        boolean reponse = true;
        for(int i = 0; i < listeBoules.size(); i++){
            if(listeBoules.get(i).getVitesseX() != 0 || listeBoules.get(i).getVitesseY() != 0){
                reponse = false;
                break;
            }
        }
        if (!reponse)
            resetterForceTir = true;
        if (reponse && resetterForceTir) {
            controleur.setForceTir(controleur.getRayonBoules()*2);
            resetterForceTir = false;
        }

        return reponse;
    }

    //source http://www.java2s.com/example/java/java.lang/distance-between-point-and-shape.html
    public static double distance(final Point2D p, final Shape s,
                                  final double eps) {
        if (s.contains(p))
            return -1;
        final PathIterator pi = s.getPathIterator(null, eps);
        final Line2D line = new Line2D.Double();
        double bestDistSq = Double.POSITIVE_INFINITY;
        double firstX = Double.NaN;
        double firstY = Double.NaN;
        double lastX = Double.NaN;
        double lastY = Double.NaN;
        final double coords[] = new double[6];
        while (!pi.isDone()) {
            final boolean validLine;
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    lastX = coords[0];/*from www.java2s.com*/
                    lastY = coords[1];
                    firstX = lastX;
                    firstY = lastY;
                    validLine = false;
                    break;
                case PathIterator.SEG_LINETO: {
                    final double x = coords[0];
                    final double y = coords[1];
                    line.setLine(lastX, lastY, x, y);
                    lastX = x;
                    lastY = y;
                    validLine = true;
                    break;
                }
                case PathIterator.SEG_CLOSE:
                    line.setLine(lastX, lastY, firstX, firstY);
                    validLine = true;
                    break;
                default:
                    throw new AssertionError();
            }
            if (validLine) {
                final double distSq = line.ptSegDistSq(p);
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                }
            }
            pi.next();
        }
        return Math.sqrt(bestDistSq);
    }

    public void bougerBoules(){
        ArrayList<Boule> listeBoules = controleur.getListeBoules();
        for(int i = 0; i < listeBoules.size(); i++){
            listeBoules.get(i).translateVitesse();
        }
    }


    public void changerVecteurs(){
        ArrayList<Boule> listeBoules = controleur.getListeBoules();
        for(int i = 0; i < listeBoules.size(); i++){
            listeBoules.get(i).updateVitesse(controleur.getCinetiqueFriction(), controleur.getPoidsBoules());
        }
    }

    public void frappe(double inputAngle, double inputForce){
        BouleBlanche bouleBlanche= controleur.getBouleBlanche();
        double vitesse = Math.sqrt(inputForce*2/controleur.getPoidsBoules())*100;
        playSound(TypeObjetTable.Mur);
        bouleBlanche.setVitesse(vitesse*Math.cos(inputAngle-Math.PI), vitesse*Math.sin(inputAngle-Math.PI));
    }


    public void collisionFrappe(){
        boolean enMouvement = !ballesImmobiles();
        ArrayList<Portail> listePortails = controleur.getListePortails();
        if (listePortails.get(0) != null && listePortails.get(1) != null) {
            for (Portail portail : listePortails)
                for (Boule boule : controleur.getListeBoules())
                    if (boule.getBoundingBox().checkCollision(portail.getBoundingBox())
                            && boule.getDernierPortailSorti() != portail)
                        collisionBoulePortail(boule, portail);
            for (Boule boule : controleur.getListeBoules())
                if (boule.getDernierPortailSorti() != null
                        && !boule.getBoundingBox().checkCollision(boule.getDernierPortailSorti().getBoundingBox()))
                    boule.setDernierPortailSorti(null);
        }

        ArrayList<Boule> listeBoules = controleur.getListeBoules();
        ArrayList<Poche> listePoches = controleur.getListePoches();
        for(int i = 0; i < listePoches.size(); i++)
            for(int j = 0; j < listeBoules.size(); j++) {
                if (controleur.isPochesRealistes()) {
                    if (listePoches.get(i).getPosition().distance(listeBoules.get(j).getPosition())
                            < listePoches.get(i).getBoundingBox().getRayon()) {
                        if(enMouvement) {
                            playSound(TypeObjetTable.Poche);
                        }
                        if (listeBoules.get(j) instanceof BouleBlanche)
                            controleur.bouleBlancheDansPoche((BouleBlanche) listeBoules.get(j));
                        else
                            controleur.removeBoule(listeBoules.get(j));
                    }
                }
                else {
                    if (listePoches.get(i).getBoundingBox().checkCollision(listeBoules.get(j).getBoundingBox())) {
                        if(enMouvement) {
                            playSound(TypeObjetTable.Poche);
                        }
                        if(listeBoules.get(j) instanceof BouleBlanche) {
                            controleur.bouleBlancheDansPoche((BouleBlanche) listeBoules.get(j));
                        }else{
                            controleur.removeBoule(listeBoules.get(j));
                        }
                    }
                }
            }
        ArrayList<Mur> listeMurs = controleur.getListeMurs();
        for(int i = 0; i < listeMurs.size(); i++){
            for (int j = 0; j < listeBoules.size(); j++) {
                if (listeMurs.get(i).getBoundingBox().checkCollision(listeBoules.get(j).getBoundingBox())) {
                    if(enMouvement) {
                        playSound(TypeObjetTable.Mur);
                    }
                    controleur.collisionBouleMur(listeBoules.get(j), listeMurs.get(i));
                }
            }
        }
        ArrayList<Integer> done = new ArrayList<>();
        for(int i = 0; i < listeBoules.size(); i++){
            for (int j = 0; j < listeBoules.size(); j++) {
                if(i != j && (!done.contains(i) || !done.contains(j))){
                    if (listeBoules.get(i).getBoundingBox().checkCollision(listeBoules.get(j).getBoundingBox())) {
                        if(enMouvement) {
                            playSound(TypeObjetTable.Boule);
                        }
                        controleur.collision2Boules(listeBoules.get(i), listeBoules.get(j));
                        done.add(i);
                        done.add(j);
                    }
                }
            }
        }
    }

    public ArrayList<ArrayList<Point2D>> getListePositionsRebondissement() {
        ArrayList<ArrayList<Point2D>> listePositionsRebondissement = new ArrayList<>();
        ArrayList<Point2D> listePositionsRebondissementBouleBlanche = new ArrayList<>();
        ArrayList<Point2D> listePositionsBouleCollision = new ArrayList<>();
        ObjetTable dernierObjet = new Poche(new Point2D.Double(0,0),0);
        double positionBouleX = controleur.getPositionBouleBlanche().getX();
        double positionBouleY = controleur.getPositionBouleBlanche().getY();
        double angleBoule = controleur.getAngleTir()+Math.PI;

        double positionInitialeListeX = positionBouleX+1.5*controleur.getRayonBoules()*Math.cos(angleBoule);
        double positionInitialeListeY = positionBouleY+1.5*controleur.getRayonBoules()*Math.sin(angleBoule);
        listePositionsRebondissementBouleBlanche.add(new Point2D.Double(positionInitialeListeX, positionInitialeListeY));

        int i = 0;
        boolean collisionBoule = false;
        int j = 0;
        while (i < (int)(Math.min(controleur.getHauteurTable(), controleur.getLargeurTable())*Math.sqrt(controleur.getForce()/30))
                && j < 100) {
            ObjetTable objetCollision = controleur.objetCollisionBouleBlancheImaginaire(positionBouleX, positionBouleY);
            if (objetCollision != null) {
                listePositionsRebondissementBouleBlanche.add(new Point2D.Double(positionBouleX, positionBouleY));

                if (objetCollision instanceof Poche) {
                    break;
                }
                else if (objetCollision instanceof Portail) {
                    break;
                }
                else if (objetCollision instanceof Boule) {
                    if(dernierObjet != objetCollision) {
                        if(dernierObjet instanceof Boule){
                            break;
                        }
                        dernierObjet = objetCollision;
                        Boule bouleCollision = (Boule) objetCollision;
                        Boule copieBouleCollision = new Boule(bouleCollision.getPosition(), bouleCollision.getBoundingBox().getRayon());

                        Boule bouleBlancheImaginaire = new Boule(new Point2D.Double(positionBouleX, positionBouleY), controleur.getRayonBoules());
                        bouleBlancheImaginaire.setVitesse(Math.cos(angleBoule), Math.sin(angleBoule));

                        collisionBouleBoule(bouleBlancheImaginaire, copieBouleCollision);

                        angleBoule = Math.atan2(bouleBlancheImaginaire.getVitesseY(), bouleBlancheImaginaire.getVitesseX());
                        collisionBoule = true;

                        double angleBouleCollision = Math.atan2(copieBouleCollision.getVitesseY(), copieBouleCollision.getVitesseX());
                        listePositionsBouleCollision = getListePositionsBouleCollision(copieBouleCollision.getPosition(), angleBouleCollision);
                    }
                }
                else if (objetCollision instanceof Mur) {
                    Mur murCollision = (Mur)objetCollision;

                    Boule bouleBlancheImaginaire = new Boule(new Point2D.Double(positionBouleX, positionBouleY), controleur.getRayonBoules());
                    bouleBlancheImaginaire.setVitesse(Math.cos(angleBoule), Math.sin(angleBoule));

                    collisionBouleMur(bouleBlancheImaginaire, murCollision);

                    angleBoule = Math.atan2(bouleBlancheImaginaire.getVitesseY(), bouleBlancheImaginaire.getVitesseX());
                    dernierObjet = objetCollision;
                }
                else throw new RuntimeException();
            }
            positionBouleX += Math.cos(angleBoule);
            positionBouleY += Math.sin(angleBoule);
            i++;
            if (collisionBoule) j++;
        }
        listePositionsRebondissementBouleBlanche.add(new Point2D.Double(positionBouleX, positionBouleY));

        listePositionsRebondissement.add(listePositionsRebondissementBouleBlanche);
        listePositionsRebondissement.add(listePositionsBouleCollision);
        return listePositionsRebondissement;
    }

    public ArrayList<Point2D> getListePositionsBouleCollision(Point2D positionBoule, double angleBoule) {
        ArrayList<Point2D> listePositions = new ArrayList<>();

        double positionBouleX = positionBoule.getX();
        double positionBouleY = positionBoule.getY();

        positionBouleX += 1.5*controleur.getRayonBoules()*Math.cos(angleBoule);
        positionBouleY += 1.5*controleur.getRayonBoules()*Math.sin(angleBoule);
        listePositions.add(new Point2D.Double(positionBouleX, positionBouleY));

        positionBouleX += 50*Math.cos(angleBoule);
        positionBouleY += 50*Math.sin(angleBoule);
        listePositions.add(new Point2D.Double(positionBouleX, positionBouleY));

        return listePositions;
    }

    public void collisionBoulePortail(Boule boule, Portail portail) {
        if (boule.getDernierPortailSorti() == portail)
            throw new RuntimeException();
        ArrayList<Portail> listePortails = controleur.getListePortails();
        if (listePortails.get(0) == null || listePortails.get(1) == null)
            throw new RuntimeException();

        if (portail == listePortails.get(0)) {
            double positionOriginaleX = boule.getPosition().getX();
            double positionOriginaleY = boule.getPosition().getY();
            double nouvellePositionX = listePortails.get(1).getPosition().getX();
            double nouvellePositionY = listePortails.get(1).getPosition().getY();
            boule.setPosition(new Point2D.Double(nouvellePositionX, nouvellePositionY));
            if (controleur.isTeleportPortailInvalide(boule))
                boule.setPosition(new Point2D.Double(positionOriginaleX, positionOriginaleY));
            else
                boule.setDernierPortailSorti(listePortails.get(1));
        }
        else {
            double positionOriginaleX = boule.getPosition().getX();
            double positionOriginaleY = boule.getPosition().getY();
            double nouvellePositionX = listePortails.get(0).getPosition().getX();
            double nouvellePositionY = listePortails.get(0).getPosition().getY();
            boule.setPosition(new Point2D.Double(nouvellePositionX, nouvellePositionY));
            if (controleur.isTeleportPortailInvalide(boule))
                boule.setPosition(new Point2D.Double(positionOriginaleX, positionOriginaleY));
            else
                boule.setDernierPortailSorti(listePortails.get(0));
        }
    }
}
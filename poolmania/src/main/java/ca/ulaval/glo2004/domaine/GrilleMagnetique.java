package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.utils.Polygon2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class GrilleMagnetique {
    private static double tailleCase = 30;
    private boolean tailleCaseChanged = false;
    public static double getTailleCase() {
        return tailleCase;
    }
    private ArrayList<Rectangle2D> grilleMagnet = new ArrayList<Rectangle2D>();
    private ArrayList<Rectangle2D> grilleMagnetOld = new ArrayList<Rectangle2D>();
    private ControleurPoolMania controleur;

    public GrilleMagnetique(ControleurPoolMania controleur) {
        this.controleur = controleur;
    }

    public void setTailleCase(double nouvelleTaille){
        if(tailleCase != nouvelleTaille){
            tailleCase = nouvelleTaille;
            tailleCaseChanged = true;
            this.grilleMagnet = new ArrayList<Rectangle2D>();

        }
    }

    public boolean getTailleCaseChanged(){
        return tailleCaseChanged;
    }

    public void setGrilleMagnet(Rectangle2D rect, boolean workerDone, Rectangle2D g2Bounds){

        if (workerDone) {
            tailleCaseChanged = false;
            grilleMagnetOld = grilleMagnet;
            grilleMagnet = new ArrayList<Rectangle2D>();
        } else {
            if(g2Bounds.contains(rect)){
                grilleMagnet.add(rect);
            }
        }

    }



    public Point2D getGrilleMagnet(Point2D points){
        if(controleur.getBoolGrilleActive()) {
            for (Rectangle2D rect : grilleMagnetOld) {
                rect = new Rectangle2D.Double(rect.getX() + tailleCase/2, rect.getY()+ tailleCase/2, rect.getWidth(), rect.getHeight());
                if (rect.contains(points)) {
                    points.setLocation(rect.getBounds().getCenterX(), rect.getBounds().getCenterY());
                    break;
                }
            }
        }
        return points;
    }


}

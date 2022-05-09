package ca.ulaval.glo2004.domaine;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Cercle extends BoundingBox
{
    private Point2D position;
    private double rayon;

    public Cercle(Point2D position, double rayon){
        this.position = position;
        this.rayon = rayon;
    }

    public boolean checkCollision(BoundingBox secondBox) {
        boolean boolCollision = false;

        if(secondBox instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) secondBox;
            double[] pointsX = rectangle.getForme().getPolygon().xpoints;
            double[] pointsY = rectangle.getForme().getPolygon().ypoints;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (i != j) {
                        Line2D line = new Line2D.Double(pointsX[i], pointsY[i], pointsX[j], pointsY[j]);
                        if (line.ptSegDist(this.position) < this.rayon)
                            boolCollision = true;
                    }
                }
            }
        } else if (secondBox instanceof Cercle) {
            Cercle cercle = (Cercle) secondBox;
            if (distanceEntrePoints(this.position, cercle.position) <=
                    (this.rayon + cercle.rayon)) {
                boolCollision = true;
            }
        }
        return boolCollision;
    }

    // TODO vérifier s'il y a des collisions
    @Override
    public void updatePosition(Point2D ancienPoint, Point2D nouvPosition) {
        this.position = nouvPosition;
    }

    public double getRayon() {
        return rayon;
    }
    public Point2D getPosition(){
        return position;
    }

    public void setRayon(double rayon) { this.rayon = rayon; }
}

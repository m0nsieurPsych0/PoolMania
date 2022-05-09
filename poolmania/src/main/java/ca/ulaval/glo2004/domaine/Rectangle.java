package ca.ulaval.glo2004.domaine;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import ca.ulaval.glo2004.utils.Polygon2D;

public class Rectangle extends BoundingBox{

    private Polygon2D forme;

    public Rectangle(Polygon2D polygon) {
        this.forme = polygon;
    }

    public boolean checkCollision(BoundingBox secondBox) {
        boolean boolCollision = false;

        //https://stackoverflow.com/questions/15690846/java-collision-detection-between-two-shape-objects
        if(secondBox instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) secondBox;
            Area secondArea = new Area(rectangle.getForme().getPolygon());
            secondArea.intersect(new Area(this.getForme().getPolygon()));
            if (!secondArea.isEmpty()) {
                boolCollision = true;
            }
        } else if (secondBox instanceof Cercle) {
            Cercle cercle = (Cercle) secondBox;
            double[] pointsX = forme.getPolygon().xpoints;
            double[] pointsY = forme.getPolygon().ypoints;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (i != j) {
                        Line2D line = new Line2D.Double(pointsX[i], pointsY[i], pointsX[j], pointsY[j]);
                        if (line.ptSegDist(cercle.getPosition()) < cercle.getRayon())
                            boolCollision = true;
                    }
                }
            }
        }
        return boolCollision;
    }

    // TODO vérifier s'il y a des collisions
    @Override
    public void updatePosition(Point2D ancienPoint, Point2D nouvPosition) {
        this.forme.translate((float)(nouvPosition.getX() - ancienPoint.getX()), (float)(nouvPosition.getY() - ancienPoint.getY()));
    }

    public Polygon2D getForme() {
        return forme;
    }

}

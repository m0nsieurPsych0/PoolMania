package ca.ulaval.glo2004.domaine;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class BoundingBox  implements java.io.Serializable {

    public abstract boolean checkCollision(BoundingBox secondBox);

    public static double distanceEntrePoints(Point2D point1, Point2D point2){
        return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) +
                Math.pow(point1.getY() - point2.getY(), 2));
    }


    public abstract void updatePosition(Point2D anciennePosition, Point2D nouvPosition);
}

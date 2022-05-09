package ca.ulaval.glo2004.domaine;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class ObjetTable implements java.io.Serializable {

    protected Point2D position;
    protected BoundingBox boundingBox;

    public Point2D getPosition() { return position; }
    public BoundingBox getBoundingBox() { return boundingBox; }

    public void setPosition(Point2D nouvPoint) {
        //TODO test
        boundingBox.updatePosition(position, nouvPoint);
        this.position = nouvPoint;
    };
}

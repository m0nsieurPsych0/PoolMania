package ca.ulaval.glo2004.domaine.DTO;

import java.awt.geom.Point2D;

public class BrisureMurDTO {

    private Point2D position;
    private boolean pourMurContour;

    public BrisureMurDTO(Point2D position, boolean pourMurContour) {
        this.position = position;
        this.pourMurContour = pourMurContour;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public boolean isPourMurContour() { return pourMurContour; }

    public void setPourMurContour(boolean pourMurContour) { this.pourMurContour = pourMurContour; }
}

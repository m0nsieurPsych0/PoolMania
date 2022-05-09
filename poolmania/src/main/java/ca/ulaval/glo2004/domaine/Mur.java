package ca.ulaval.glo2004.domaine;

import java.awt.geom.Point2D;
import ca.ulaval.glo2004.utils.Polygon2D;

public class Mur extends ObjetTable {

    private double angle;
    private double longueur;
    private double epaisseur;

    public Mur(Point2D position, double angle, double longueur, double epaisseur) {
        this.position = position;
        this.angle = angle;
        while (this.angle < 0)
            this.angle += 2*Math.PI;
        while (this.angle > 2*Math.PI)
            this.angle -= 2*Math.PI;
        this.longueur = longueur;
        this.epaisseur = epaisseur;
        this.boundingBox = new Rectangle(getPolygon());
    }

    public Polygon2D getPolygon() {
        double xcoin1 = position.getX() - epaisseur/2 * Math.cos(angle+Math.PI/2);
        double ycoin1 = position.getY() - epaisseur/2 * Math.sin(angle+Math.PI/2);
        double xcoin2 = position.getX() + epaisseur/2 * Math.cos(angle+Math.PI/2);
        double ycoin2 = position.getY() + epaisseur/2 * Math.sin(angle+Math.PI/2);
        double xcoin3 = position.getX() + epaisseur/2 * Math.cos(angle+Math.PI/2) + longueur * Math.cos(angle);
        double ycoin3 = position.getY() + epaisseur/2 * Math.sin(angle+Math.PI/2) + longueur * Math.sin(angle);
        double xcoin4 = position.getX() - epaisseur/2 * Math.cos(angle+Math.PI/2) + longueur * Math.cos(angle);
        double ycoin4 = position.getY() - epaisseur/2 * Math.sin(angle+Math.PI/2) + longueur * Math.sin(angle);
        double[] x = {xcoin1, xcoin2, xcoin3, xcoin4};
        double[] y = {ycoin1, ycoin2, ycoin3, ycoin4};
        return new Polygon2D(x, y,4);
    }

    /*
     * Appelé en boucle pour chaque mur de contour qui
     * superpose un autre pour tronquer le BoundingBox
     * Inspiré de : https://rosettacode.org/wiki/Find_the_intersection_of_two_lines#Java
     */
    public void updateBoundingBoxMurSansIntersection(Mur murIntersection) {
        if (this.angle != murIntersection.angle) {
            double[] xpointsMur1 = getBoundingBox().getForme().getPolygon().xpoints;
            double[] ypointsMur1 = getBoundingBox().getForme().getPolygon().ypoints;
            double[] xpointsMur2 = murIntersection.getBoundingBox().getForme().getPolygon().xpoints;
            double[] ypointsMur2 = murIntersection.getBoundingBox().getForme().getPolygon().ypoints;

            double diffEpaisseurMurX = (epaisseur/2)*Math.cos(angle+Math.PI/2);
            double diffEpaisseurMurY = (epaisseur/2)*Math.sin(angle+Math.PI/2);

            double a1 = ypointsMur1[3] - ypointsMur1[0];
            double b1 = xpointsMur1[0] - xpointsMur1[3];
            double c1 = a1 * xpointsMur1[0] + b1 * ypointsMur1[0];

            double a2 = ypointsMur2[3] - ypointsMur2[0];
            double b2 = xpointsMur2[0] - xpointsMur2[3];
            double c2 = a2 * xpointsMur2[0] + b2 * ypointsMur2[0];

            double delta = a1 * b2 - a2 * b1;
            double intersectionX = (b2 * c1 - b1 * c2) / delta;
            double intersectionY = (a1 * c2 - a2 * c1) / delta;

            double positionXExtremiteMur = position.getX()+longueur*Math.cos(angle)-diffEpaisseurMurX;
            double positionYExtremiteMur = position.getY()+longueur*Math.sin(angle)-diffEpaisseurMurY;

            if (Math.sqrt(Math.pow(intersectionX-(position.getX()-diffEpaisseurMurX),2)
                    + Math.pow(intersectionY-(position.getY()-diffEpaisseurMurY),2))
                    < Math.sqrt(Math.pow(intersectionX-positionXExtremiteMur,2)
                    + Math.pow(intersectionY-positionYExtremiteMur,2))) {
                getBoundingBox().getForme().xpoints[0] = intersectionX;
                getBoundingBox().getForme().ypoints[0] = intersectionY;
            }
            else {
                getBoundingBox().getForme().xpoints[3] = intersectionX;
                getBoundingBox().getForme().ypoints[3] = intersectionY;
            }

            a1 = ypointsMur1[2] - ypointsMur1[1];
            b1 = xpointsMur1[1] - xpointsMur1[2];
            c1 = a1 * xpointsMur1[1] + b1 * ypointsMur1[1];

            a2 = ypointsMur2[2] - ypointsMur2[1];
            b2 = xpointsMur2[1] - xpointsMur2[2];
            c2 = a2 * xpointsMur2[1] + b2 * ypointsMur2[1];

            delta = a1 * b2 - a2 * b1;
            intersectionX = (b2 * c1 - b1 * c2) / delta;
            intersectionY = (a1 * c2 - a2 * c1) / delta;

            positionXExtremiteMur = position.getX()+longueur*Math.cos(angle)+diffEpaisseurMurX;
            positionYExtremiteMur = position.getY()+longueur*Math.sin(angle)+diffEpaisseurMurY;

            if (Math.sqrt(Math.pow(intersectionX-(position.getX()+diffEpaisseurMurX),2)
                    + Math.pow(intersectionY-(position.getY()+diffEpaisseurMurY),2))
                    < Math.sqrt(Math.pow(intersectionX-positionXExtremiteMur,2)
                    + Math.pow(intersectionY-positionYExtremiteMur,2))) {
                getBoundingBox().getForme().xpoints[1] = intersectionX;
                getBoundingBox().getForme().ypoints[1] = intersectionY;
            }
            else {
                getBoundingBox().getForme().xpoints[2] = intersectionX;
                getBoundingBox().getForme().ypoints[2] = intersectionY;
            }
        }
    }

    public double getAngle() { return angle; }

    public double getLongueur() { return longueur; }

    public double getEpaisseur() { return epaisseur; }

    public Rectangle getBoundingBox() { return (Rectangle) boundingBox; }

    public void setAngle(double angle) {
        this.angle = angle;
        while (this.angle < 0)
            this.angle += 2*Math.PI;
        while (this.angle > 2*Math.PI)
            this.angle -= 2*Math.PI;
        boundingBox = new Rectangle(getPolygon());
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
        boundingBox = new Rectangle(getPolygon());
    }

    public void setEpaisseur(double epaisseur) {
        this.epaisseur = epaisseur;
        boundingBox = new Rectangle(getPolygon());
    }

    public void resetBoundingBoxAvantUpdate() { boundingBox = new Rectangle(getPolygon()); }
}

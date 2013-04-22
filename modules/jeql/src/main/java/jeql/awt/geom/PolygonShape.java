package jeql.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;


public class PolygonShape implements Shape {
    private java.awt.Polygon shell;
    private ArrayList holes = new ArrayList();

    /**
     * @param shellVertices in view coordinates
     * @param holeVerticesCollection a Coordinate[] for each hole, in view coordinates
     */
    public PolygonShape(Coordinate[] shellVertices,
        Collection holeVerticesCollection) {
        shell = toPolygon(shellVertices);

        for (Iterator i = holeVerticesCollection.iterator(); i.hasNext();) {
            Coordinate[] holeVertices = (Coordinate[]) i.next();
            holes.add(toPolygon(holeVertices));
        }
    }

    private java.awt.Polygon toPolygon(Coordinate[] coordinates) {
        java.awt.Polygon polygon = new java.awt.Polygon();

        for (int i = 0; i < coordinates.length; i++) {
            polygon.addPoint((int) coordinates[i].x, (int) coordinates[i].y);
        }

        return polygon;
    }

    public Rectangle getBounds() {
        /**@todo Implement this java.awt.Shape method*/
        throw new java.lang.UnsupportedOperationException(
            "Method getBounds() not yet implemented.");
    }

    public Rectangle2D getBounds2D() {
        return shell.getBounds2D();
    }

    public boolean contains(double x, double y) {
        /**@todo Implement this java.awt.Shape method*/
        throw new java.lang.UnsupportedOperationException(
            "Method contains() not yet implemented.");
    }

    public boolean contains(Point2D p) {
        /**@todo Implement this java.awt.Shape method*/
        throw new java.lang.UnsupportedOperationException(
            "Method contains() not yet implemented.");
    }

    public boolean intersects(double x, double y, double w, double h) {
        /**@todo Implement this java.awt.Shape method*/
        throw new java.lang.UnsupportedOperationException(
            "Method intersects() not yet implemented.");
    }

    public boolean intersects(Rectangle2D r) {
        /**@todo Implement this java.awt.Shape method*/
        throw new java.lang.UnsupportedOperationException(
            "Method intersects() not yet implemented.");
    }

    public boolean contains(double x, double y, double w, double h) {
        /**@todo Implement this java.awt.Shape method*/
        throw new java.lang.UnsupportedOperationException(
            "Method contains() not yet implemented.");
    }

    public boolean contains(Rectangle2D r) {
        /**@todo Implement this java.awt.Shape method*/
        throw new java.lang.UnsupportedOperationException(
            "Method contains() not yet implemented.");
    }

    public PathIterator getPathIterator(AffineTransform at) {
        ArrayList rings = new ArrayList();
        rings.add(shell);
        rings.addAll(holes);

        return new ShapeCollectionPathIterator(rings, at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        // since we don't support curved geometries, can simply delegate to the simple method
        return getPathIterator(at);
    }
}

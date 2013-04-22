package jeql.awt.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Collection;
import java.util.Iterator;

public class ShapeCollectionPathIterator implements PathIterator {
    private Iterator shapeIterator;
    private PathIterator currentPathIterator = new PathIterator() {
            public int getWindingRule() {
                throw new UnsupportedOperationException();
            }

            public boolean isDone() {
                return true;
            }

            public void next() {
            }

            public int currentSegment(float[] coords) {
                throw new UnsupportedOperationException();
            }

            public int currentSegment(double[] coords) {
                throw new UnsupportedOperationException();
            }
        };

    private AffineTransform affineTransform;
    private boolean done = false;

    public ShapeCollectionPathIterator(Collection shapes,
        AffineTransform affineTransform) {
        shapeIterator = shapes.iterator();
        this.affineTransform = affineTransform;
        next();
    }

    public int getWindingRule() {
        return PathIterator.WIND_EVEN_ODD;
    }

    public boolean isDone() {
        return done;
    }

    public void next() {
        currentPathIterator.next();

        if (currentPathIterator.isDone() && !shapeIterator.hasNext()) {
            done = true;

            return;
        }

        if (currentPathIterator.isDone()) {
            currentPathIterator = ((Shape) shapeIterator.next()).getPathIterator(affineTransform);
        }
    }

    public int currentSegment(float[] coords) {
        return currentPathIterator.currentSegment(coords);
    }

    public int currentSegment(double[] coords) {
        return currentPathIterator.currentSegment(coords);
    }
}

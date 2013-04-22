package jeql.std.function.geommatch;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Computes the Euclidean distance (L2 metric) from a Point to a Geometry.
 * Also computes two points which are separated by the distance.
 */
public class EuclideanDistanceToPoint {

  // used for point-line distance calculation
  private static LineSegment tempSegment = new LineSegment();

  public EuclideanDistanceToPoint() {
  }

  public static void computeDistance(Geometry geom, Coordinate pt, PointPairDistance ptDist)
  {
    if (geom instanceof LineString) {
      computeDistance((LineString) geom, pt, ptDist);
    }
    else if (geom instanceof Polygon) {
      computeDistance((Polygon) geom, pt, ptDist);
    }
    else if (geom instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geom;
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        Geometry g = gc.getGeometryN(i);
        computeDistance(g, pt, ptDist);
      }
    }
    else { // assume geom is Point
      ptDist.setMinimum(geom.getCoordinate(), pt);
    }
  }
  public static void computeDistance(LineString line, Coordinate pt, PointPairDistance ptDist)
  {
    Coordinate[] coords = line.getCoordinates();
    for (int i = 0; i < coords.length - 1; i++) {
      tempSegment.setCoordinates(coords[i], coords[i + 1]);
      // this is somewhat inefficient - could do better
      Coordinate closestPt = tempSegment.closestPoint(pt);
      ptDist.setMinimum(closestPt, pt);
    }
  }

  public static void computeDistance(LineSegment segment, Coordinate pt, PointPairDistance ptDist)
  {
    Coordinate closestPt = segment.closestPoint(pt);
    ptDist.setMinimum(closestPt, pt);
  }

  public static void computeDistance(Polygon poly, Coordinate pt, PointPairDistance ptDist)
  {
    computeDistance(poly.getExteriorRing(), pt, ptDist);
    for (int i = 0; i < poly.getNumInteriorRing(); i++) {
      computeDistance(poly.getInteriorRingN(i), pt, ptDist);
    }
  }
}

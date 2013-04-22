package jeql.io.shapefile;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class ShapeGeometryBuilder 
{

  /**
   * reverses the order of points in lr (is CW -> CCW or CCW->CW)
   */
  static LinearRing reverseRing(LinearRing lr) {
    int numPoints = lr.getNumPoints();
    Coordinate[] newCoords = new Coordinate[numPoints];
    for (int t = 0; t < numPoints; t++) {
      newCoords[t] = lr.getCoordinateN(numPoints - t - 1);
    }
    return new LinearRing(newCoords, new PrecisionModel(), 0);
  }

  /**
   * make sure outer ring is CCW and holes are CW
   * 
   * @param p
   *          polygon to check
   */
  static Polygon createShpPolygon(Polygon p) {
    LinearRing outer;
    LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
    Coordinate[] coords;
  
    if (p.isEmpty())
      return p;
  
    coords = p.getExteriorRing().getCoordinates();
  
    if (CGAlgorithms.isCCW(coords)) {
      outer = reverseRing((LinearRing) p.getExteriorRing());
    } else {
      outer = (LinearRing) p.getExteriorRing();
    }
  
    for (int t = 0; t < p.getNumInteriorRing(); t++) {
      coords = p.getInteriorRingN(t).getCoordinates();
  
      if (!(CGAlgorithms.isCCW(coords))) {
        holes[t] = reverseRing((LinearRing) p.getInteriorRingN(t));
      } else {
        holes[t] = (LinearRing) p.getInteriorRingN(t);
      }
    }
  
    return new Polygon(outer, holes, new PrecisionModel(), 0);
  }

  /**
   * make sure outer ring is CCW and holes are CW for all the polygons in the
   * Geometry
   * 
   * @param mp
   *          set of polygons to check
   */
  static MultiPolygon createShpMultiPolygon(MultiPolygon mp) {
    MultiPolygon result;
    Polygon[] ps = new Polygon[mp.getNumGeometries()];
  
    // check each sub-polygon
    for (int t = 0; t < mp.getNumGeometries(); t++) {
      ps[t] = createShpPolygon((Polygon) mp.getGeometryN(t));
    }
  
    result = new MultiPolygon(ps, new PrecisionModel(), 0);
  
    return result;
  }

  static Geometry buildGeometry(Geometry geom, int geomtype) {
    switch (geomtype) {
    case Shapefile.POINT:
      if ((geom instanceof Point)) {
        return geom;
      } else {
        // create empty Point
        return new Point(null, new PrecisionModel(), 0);
      }
    case Shapefile.MULTIPOINT:
      if ((geom instanceof Point)) {
        // good!
        Point[] p = new Point[1];
        p[0] = (Point) geom;
  
        return new MultiPoint(p, new PrecisionModel(), 0);
      } else if (geom instanceof MultiPoint) {
        return geom;
      } else {
        return new MultiPoint(null, new PrecisionModel(), 0);
      }
    case Shapefile.ARC: // line
      if ((geom instanceof LineString)) {
        LineString[] l = new LineString[1];
        l[0] = (LineString) geom;
  
        return new MultiLineString(l, new PrecisionModel(), 0);
      } else if (geom instanceof MultiLineString) {
        return geom;
      } else {
        return new MultiLineString(null, new PrecisionModel(), 0);
      }
    case Shapefile.POLYGON: // polygon
  
      if (geom instanceof Polygon) {
        // good!
        Polygon[] p = new Polygon[1];
        p[0] = (Polygon) geom;
  
        return createShpMultiPolygon(new MultiPolygon(p,
            new PrecisionModel(), 0));
      } else if (geom instanceof MultiPolygon) {
        return createShpMultiPolygon((MultiPolygon) geom);
      } else {
        return new MultiPolygon(null, new PrecisionModel(), 0);
      }
    }
    // should never reach here
    throw new IllegalStateException("Unhandled geometry type: " + geom.getGeometryType());
  }

}

package jeql.jts.geodetic;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;

public class GeodeticShape
{

  /**
   * Computes a circle of a given radius around a given point,
   * using Vincenty's formula on the WGS84 ellipsoid.
   *  
   * @param centrePt the geodetic center point, in decimal degrees
   * @param radiusInMetres radius in metres
   * @param sides the number of sides to compute
   * @return a Polygon in the shape of a circle
   */
  public static final Geometry circle(Geometry centrePt, double radiusInMetres, int sides)
  {
      Coordinate[] pts = new Coordinate[sides+1];
      for (int i = 0; i < sides; i++) {
          double angle = i * 360 / sides;
          Coordinate p = GeodeticMetrics.destinationVincenty(centrePt.getCoordinate(), angle, radiusInMetres);
          pts[i] = p;
      }
      // closing point
      pts[sides] = new Coordinate(pts[0]);
      LinearRing ring = centrePt.getFactory().createLinearRing(pts);
      return centrePt.getFactory().createPolygon(ring, null);
  }

}

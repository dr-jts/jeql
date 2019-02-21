package jeql.jts.geodetic;

import jeql.std.function.MathFunction;

import org.locationtech.jts.geom.Coordinate;

public class GeodeticCoord {

  static Coordinate toCartesian(Coordinate geo)
  {
    Coordinate dc = new Coordinate();
    double lat = MathFunction.toRadians(geo.y);
    double lon = MathFunction.toRadians(geo.x);
    double cosphi = Math.cos(lat);
    dc.x = cosphi * Math.cos(lon);
    dc.y = cosphi * Math.sin(lon);
    dc.z = Math.sin(lat);
    return dc;
  }

  static Coordinate toGeodetic(Coordinate dc)
  {
    Coordinate geo = new Coordinate();
    double len = Math.sqrt(dc.x * dc.x + dc.y * dc.y);
    double latRad = Math.atan2(dc.z, len);
    double lonRad = Math.atan2(dc.y, dc.x);
    geo.x = MathFunction.toDegrees(lonRad);
    geo.y = MathFunction.toDegrees(latRad);
    return geo;
  }

  static Coordinate midPtDC(Coordinate dc1, Coordinate dc2)
  {
    Coordinate avg = new Coordinate();
    avg.x = (dc1.x + dc2.x)/2;
    avg.y = (dc1.y + dc2.y)/2;
    avg.z = (dc1.z + dc2.z)/2;
    GeodeticCoord.normalizeDC(avg);
    return avg;
  }

  static void normalizeDC(Coordinate dc)
  {
    double mag = Math.sqrt(dc.x*dc.x + dc.y*dc.y + dc.z*dc.z);
    dc.x /= mag;
    dc.y /= mag;
    dc.z /= mag;
  }

  static double distance3D(Coordinate p0, Coordinate p1)
  {
    double dx = p1.x - p0.x;
    double dy = p1.y - p0.y;
    double dz = p1.z - p0.z;
    return Math.sqrt(dx*dx + dy*dy + dz*dz);
  }

}

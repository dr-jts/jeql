package jeql.jts.geodetic;

import org.locationtech.jts.geom.Coordinate;

public class GeodeticMetrics
{
  public static final double WGS84_MAJOR_RADIUS = 6378137.0; 
  public static final double WGS84_MINOR_RADIUS =  6356752.314245; 
  public static final double WGS84_RECIPROCAL_FLATTENING = 298.257223563; 
  public static final double WGS84_FLATTENING = 1.0 / WGS84_RECIPROCAL_FLATTENING; 
  
  public static final double WGS84_A = WGS84_MAJOR_RADIUS;
  public static final double WGS84_B = WGS84_MINOR_RADIUS;
  public static final double WGS84_F = WGS84_FLATTENING;
  public static final double WGS84_R = WGS84_RECIPROCAL_FLATTENING;
  
  public static final double EARTH_MEAN_RADIUS = 6371000.7900;
  
  public static double distanceVincenty(double lat1, double lon1,
      double lat2, double lon2)
  {
    double L = Math.toRadians(lon2 - lon1);
    double U1 = Math.atan((1 - WGS84_F) * Math.tan(Math.toRadians(lat1)));
    double U2 = Math.atan((1 - WGS84_F) * Math.tan(Math.toRadians(lat2)));
    double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
    double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

    double lambda = L;
    double lambdaPrev;
    int nIter = 100;

    double sigma;
    double cosSqAlpha;
    double sinSigma;
    double cosSigma;
    double cos2SigmaM;
    do {
      double sinLambda = Math.sin(lambda);
      double cosLambda = Math.cos(lambda);
      sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
          + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
          * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
      if (sinSigma == 0)
        return 0; // co-incident points
      cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
      sigma = Math.atan2(sinSigma, cosSigma);
      
      double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
      cosSqAlpha = 1 - sinAlpha * sinAlpha;
      cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
      if (Double.isNaN(cos2SigmaM))
        cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
      double C = WGS84_F / 16 * cosSqAlpha * (4 + WGS84_F * (4 - 3 * cosSqAlpha));
      lambdaPrev = lambda;
      lambda = L
          + (1 - C)
          * WGS84_F
          * sinAlpha
          * (sigma + C
              * sinSigma
              * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
    } while (Math.abs(lambda - lambdaPrev) > 1e-12 && --nIter > 0);

    if (nIter == 0)
      return Double.NaN; // formula failed to converge

    double uSq = cosSqAlpha * (WGS84_A * WGS84_A - WGS84_B * WGS84_B) / (WGS84_B * WGS84_B);
    double A = 1 + uSq / 16384
        * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
    double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
    double deltaSigma = B * sinSigma * (cos2SigmaM + B
            / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6
                * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma)
                * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
    double s = WGS84_B * A * (sigma - deltaSigma);

    return s;
  }
 
  public static double centralAngleSphere(double lat1, double lon1,
      double lat2, double lon2)
  {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lon2 - lon1);
    double sin2 = Math.sin(dLat/2);
    double sinLng2 = Math.sin(dLng/2);
    double a = sin2 * sin2 +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               sinLng2 * sinLng2;
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return c;
  }

  private static double reducedLatitude(double lat, double r)
  {
    double redLatRad = Math.atan((r - 1) / r * Math.tan(Math.toRadians(lat)));
    return Math.toDegrees(redLatRad);
  }

  /**
   * Computes the distance in metres between two geodetic points
   * on the sphere defined by the WGS84 major axis.
   * <p>
   * Uses the Haversine formula
   * (R. W. Sinnott, "Virtues of the Haversine", <i>Sky and Telescope</i>, vol 68, no 2, 1984).
   * 
   * @param p0 a geodetic location in decimal degrees
   * @param p1 a geodetic coordinate in decimal degrees
   * @return the distance between the points in metres
   */
  public static double distanceSphere(Coordinate p0, Coordinate p1)
  {
    double centAng = centralAngleSphere(p0.y, p0.x, p1.y, p1.x);
    return EARTH_MEAN_RADIUS * centAng;
  }
  
  public static double distanceLambert(Coordinate p1, Coordinate p2)
  {
    double redLat1 = reducedLatitude(p1.y, WGS84_R);
    double redLat2 = reducedLatitude(p2.y, WGS84_R);
    
    double sigma = centralAngleSphere(redLat1, p1.x, redLat2, p2.x);
    
    double P = (redLat1 + redLat2)/2;
    double Q = (redLat2 - redLat1)/2;
    double sinP = Math.sin(P);
    double cosP = Math.cos(P);
    double sinQ = Math.sin(Q);
    double cosQ = Math.cos(Q);
    double cosSigma2 = Math.cos(sigma/2);
    double sinSigma2 = Math.sin(sigma/2);
    
    double X = (3 * Math.sin(sigma) - sigma) * 
      (sinP * sinP * cosQ * cosQ)
        / (cosSigma2 * cosSigma2);
    double Y = (3 * Math.sin(sigma) + sigma) * 
      (cosP * cosP * sinQ * sinQ)
        / (sinSigma2 * sinSigma2);
    
    double distAng = sigma + (X - Y / (2 * WGS84_R));
    double dist = WGS84_MAJOR_RADIUS * distAng;
    return dist;
  }
  
  public static double distanceLambertWiki(Coordinate p1, Coordinate p2)
  {
    double redLat1 = reducedLatitude(p1.y, WGS84_R);
    double redLat2 = reducedLatitude(p2.y, WGS84_R);
    
    double sigma = centralAngleSphere(redLat1, p1.x, redLat2, p2.x);
    
    double P = (redLat1 + redLat2)/2;
    double Q = (redLat2 - redLat1)/2;
    double sinP = Math.sin(P);
    double cosP = Math.cos(P);
    double sinQ = Math.sin(Q);
    double cosQ = Math.cos(Q);
    double cosSigma2 = Math.cos(sigma/2);
    double sinSigma2 = Math.sin(sigma/2);
    
    double X = (sigma - Math.sin(sigma)) * 
    (sinP * sinP * cosQ * cosQ)
    /  (cosSigma2 * cosSigma2);
    double Y = (sigma + Math.sin(sigma)) * 
    (cosP * cosP * sinQ * sinQ)
    /  (sinSigma2 * sinSigma2);
    
    double distAng = sigma - (X + Y / (2 * WGS84_R));
    double dist = WGS84_MAJOR_RADIUS * distAng;
    return dist;
  }
  
  /**
   * Calculates the destination point given a geodetic start point (decimal degrees),
   * bearing (decimal degrees) & distance (metres),
   * using Vincenty's formula on the WGS84 ellipsoid.
   * <p>
   * Bearings are specified with true North at 0 degrees, 
   * oriented clockwise (so that East is 90 degrees)
   * <p>
   * Adapted from
   * http://www.movable-type.co.uk/scripts/latlong-vincenty-direct.html
   *
   * @param startPt the geodetic start point (lon/lat in decimal degrees)
   * @param bearing bearing CW from North, in decimal degrees
   * @param distance distance to destination in metres
   * @result the destination point
   */
  public static Coordinate destinationVincenty(Coordinate startPt, double bearing, double distance) {
      double lon1 = startPt.x;
      double lat1 = startPt.y;

      double s = distance;
      double alpha1 = Math.toRadians(bearing);
      double sinAlpha1 = Math.sin(alpha1);
      double cosAlpha1 = Math.cos(alpha1);

      double tanU1 = (1-WGS84_F) * Math.tan(Math.toRadians(lat1));
      double cosU1 = 1 / Math.sqrt((1 + tanU1*tanU1)), sinU1 = tanU1*cosU1;
      double sigma1 = Math.atan2(tanU1, cosAlpha1);
      double sinAlpha = cosU1 * sinAlpha1;
      double cosSqAlpha = 1 - sinAlpha*sinAlpha;
      double uSq = cosSqAlpha * (WGS84_A*WGS84_A - WGS84_B*WGS84_B) / (WGS84_B*WGS84_B);
      double A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
      double B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));

      double sigma = s / (WGS84_B*A), sigmaP = 2*Math.PI;
      double sinSigma = 0.0;
      double cosSigma = 0.0;
      double cos2SigmaM = 0.0;
      while (Math.abs(sigma-sigmaP) > 1e-12) {
          cos2SigmaM = Math.cos(2*sigma1 + sigma);
          sinSigma = Math.sin(sigma);
          cosSigma = Math.cos(sigma);
          double deltaSigma = B*sinSigma*(cos2SigmaM+B/4*(cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)-
              B/6*cos2SigmaM*(-3+4*sinSigma*sinSigma)*(-3+4*cos2SigmaM*cos2SigmaM)));
          sigmaP = sigma;
          sigma = s / (WGS84_B*A) + deltaSigma;
      }

      double tmp = sinU1*sinSigma - cosU1*cosSigma*cosAlpha1;
      double lat2 = Math.atan2(sinU1*cosSigma + cosU1*sinSigma*cosAlpha1,
          (1-WGS84_F)*Math.sqrt(sinAlpha*sinAlpha + tmp*tmp));
      double lambda = Math.atan2(sinSigma*sinAlpha1, cosU1*cosSigma - sinU1*sinSigma*cosAlpha1);
      double C = WGS84_F/16*cosSqAlpha*(4+WGS84_F*(4-3*cosSqAlpha));
      double L = lambda - (1-C) * WGS84_F * sinAlpha *
          (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));

      //double revAz = Math.atan2(sinAlpha, -tmp);  // final (reverse) bearing

      return new Coordinate(lon1 + Math.toDegrees(L), Math.toDegrees(lat2));
  };
  
  /**
   * Calculates the destination point given a geodetic start point (decimal degrees),
   * bearing (decimal degrees) & distance (metres),
   * on the Earth mean sphere.
   * <p>
   * Bearings are specified with true North at 0 degrees, 
   * oriented clockwise (so that East is 90 degrees)
   *
   * @param startPt the geodetic start point (lon/lat in decimal degrees)
   * @param bearing bearing CW from North, in decimal degrees
   * @param distance distance to destination in metres
   * @result the destination point
   */
  public static Coordinate destinationSphere(Coordinate startPt, double bearing, double distance) {
    double dist = distance/EARTH_MEAN_RADIUS;  // convert dist to angular distance in radians
    double brng = Math.toRadians(bearing);  
    double lat1 = Math.toRadians(startPt.y);
    double lon1 = Math.toRadians(startPt.x);

    double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dist) + 
                          Math.cos(lat1)*Math.sin(dist)*Math.cos(brng) );
    double lon2 = lon1 + Math.atan2(Math.sin(brng)*Math.sin(dist)*Math.cos(lat1), 
                                 Math.cos(dist)-Math.sin(lat1)*Math.sin(lat2));
    lon2 = (lon2+3*Math.PI) % (2*Math.PI) - Math.PI;  // normalise to -180..+180º

    return new Coordinate(Math.toDegrees(lon2), Math.toDegrees(lat2));
  }
}

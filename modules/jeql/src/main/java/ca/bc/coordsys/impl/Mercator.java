package ca.bc.coordsys.impl;

import ca.bc.coordsys.Geographic;
import ca.bc.coordsys.Planar;
import ca.bc.coordsys.Projection;

/**
 * This class implements the Mercator projection.
 * 
 *
 */

public class Mercator extends Projection {

  double L0;// central meridian
  double X0;// false Easting
  double Y0;// false Northing
  Geographic q = new Geographic();

  public Mercator() {
    super();
  }

  /**
   *@param  centralMeridian  in degrees
   *@param  falseEasting     in metres
   *@param  falseNorthing    in metres
   */
  public void setParameters(double centralMeridian,
      double falseEasting,
      double falseNorthing) {
    L0 = centralMeridian / 180.0 * Math.PI;
    X0 = falseEasting;
    Y0 = falseNorthing;
  }

  public Planar asPlanar(Geographic q0, Planar p) {
    q.lat = q0.lat / 180.0 * Math.PI;
    q.lon = q0.lon / 180.0 * Math.PI;
    forward(q, p);
    return p;
  }

  public Geographic asGeographic(Planar p, Geographic q) {
    inverse(p, q);
    q.lat = q.lat * 180.0 / Math.PI;
    q.lon = q.lon * 180.0 / Math.PI;
    return q;
  }

  void forward(Geographic q, Planar p) {
    double a;
    double e;
    a = currentSpheroid.getA();
    e = currentSpheroid.getE();
    p.x = a * (q.lon - L0);
    p.y = (a / 2.0) * Math.log(
        ((1.0 + Math.sin(q.lat)) / (1.0 - Math.sin(q.lat)))
         * Math.pow(((1.0 - e * Math.sin(q.lat)) / (1.0 + e * Math.sin(q.lat))), e));
  }

  void inverse(Planar p, Geographic q) {
    double t;
    double delta;
    double phiI;
    double phi;
    double lambda;
    double a;
    double e;
    a = currentSpheroid.getA();
    e = currentSpheroid.getE();
    t = Math.exp(-p.y / a);
    //phi = Math.PI / 2.0 - 2.0 * Math.tan(t); -- transcription error
    phi = Math.PI / 2.0 - 2.0 * Math.atan(t);
    delta = 10000.0;
    do {
      phiI = Math.PI / 2.0 - 2.0 * Math.atan(
          t * Math.pow(((1.0 - e * Math.sin(phi)) / (1.0 + e * Math.sin(phi))),
          (e / 2.0)));
      delta = Math.abs(phiI - phi);
      phi = phiI;
    } while (delta > 1.0e-014);
    lambda = p.x / a + L0;
    q.lat = phi;
    q.lon = lambda;
  }

}


package ca.bc.coordsys.impl;
import ca.bc.coordsys.Geographic;
import ca.bc.coordsys.Planar;
import ca.bc.coordsys.Projection;

/**
 * Implements the Polyconic projection.
 * *
 */

public class Polyconic extends Projection {

// private:
  double L0;// central meridian
  double k0;// scale factor
  double phi1;// 1st standard parallel
  double phi2;// 2nd standard parallel
  double phi0;// Latitude of projection
  double X0;// false Easting
  double Y0;// false Northing
  int zone;// UTMzone
  MeridianArcLength S = new MeridianArcLength();
  Geographic q = new Geographic();

  public Polyconic() {
    super();
  }

  public void setParameters(double originLatitude, double originLongitude) {
    // Polyconic projection
    L0 = originLongitude / 180.0 * Math.PI;
    phi0 = originLatitude / 180.0 * Math.PI;
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


  public void forward(Geographic q, Planar p) {
    double M;
    double M0;
    S.compute(currentSpheroid, q.lat, 0);
    M = S.s;
    S.compute(currentSpheroid, phi0, 0);
    M0 = S.s;
    double a;
    double e;
    double e2;
    a = currentSpheroid.a;
    e = currentSpheroid.e;
    e2 = e * e;
    double N;
    double t;
    t = Math.sin(q.lat);
    N = a / Math.sqrt(1.0 - e2 * t * t);
    double E;
    E = (q.lon - L0) * Math.sin(q.lat);
    t = 1.0 / Math.tan(q.lat);
    p.x = N * t * Math.sin(E);
    p.y = M - M0 + N * t * (1.0 - Math.cos(E));
  }


  public void inverse(Planar p, Geographic q) {
    double a;
    double e;
    double es;
    a = currentSpheroid.getA();
    e = currentSpheroid.getE();
    es = e * e;
    double A;
    double B;
    double M0;
    S.compute(currentSpheroid, phi0, 0);
    M0 = S.s;
    A = (M0 + p.y) / a;
    B = (p.x * p.x) / (a * a) + A * A;
    double C;
    double phiN;
    double M;
    double Mp;
    double Ma;
    double Ma2;
    double s2p;
    q.lat = A;
    int count = 0;
    do {
      phiN = q.lat;
      C = Math.sqrt(1.0 - es * Math.sin(phiN) * Math.sin(phiN)) * Math.tan(phiN);
      S.compute(currentSpheroid, phiN, 0);
      M = S.s;
      Ma = M / a;
      Ma2 = Ma * Ma;
      S.compute(currentSpheroid, phiN, 1);
      Mp = S.s;
      s2p = Math.sin(2.0 * phiN);
      q.lat = q.lat - (A * (C * Ma + 1.0) - Ma - 0.5 * (Ma2 + B) * C) /
          (es * s2p * (Ma2 + B - 2.0 * A * Ma) / 4.0 * C + (A - Ma) * (C * Mp - 2.0 / s2p) - Mp);
    } while (Math.abs(q.lat - phiN) > 1.0e-6 && count++ < 100);//1.0e-12);
    q.lon = Math.asin(p.x * C / a) / Math.sin(q.lat) + L0;
  }

}

package ca.bc.coordsys.impl;

import ca.bc.coordsys.Geographic;
import ca.bc.coordsys.Planar;
import ca.bc.coordsys.Projection;
import ca.bc.coordsys.Spheroid;

import com.vividsolutions.jts.util.Assert;

/**
 * Implements the Universal Transverse Mercator Projection.
  */
public class UniversalTransverseMercator extends Projection {

  private final static double SCALE_FACTOR = 0.9996;
  private final static double FALSE_EASTING = 500000.0;
  private final static double FALSE_NORTHING = 0.0;

  private TransverseMercator transverseMercator = new TransverseMercator();

  public UniversalTransverseMercator() { }

  private int zone = -1;

  /**
   * @param utmZone must be between 7 and 11
   */
  public void setParameters(int zone) {

    Assert.isTrue(zone >= 7, "UTM zone " + zone + " not supported");
    Assert.isTrue(zone <= 11, "UTM zone " + zone + " not supported");

    switch (zone) {
      case 7:
        transverseMercator.setParameters(-141.0);
        break;
      case 8:
        transverseMercator.setParameters(-135.0);
        break;
      case 9:
        transverseMercator.setParameters(-129.0);
        break;
      case 10:
        transverseMercator.setParameters(-123.0);
        break;
      case 11:
        transverseMercator.setParameters(-117.0);
        break;
      case 12:
        transverseMercator.setParameters(-111.0);
        break;
      default:
        Assert.shouldNeverReachHere();
    }
    this.zone = zone;
  }

  public void setSpheroid(Spheroid s) {
    transverseMercator.setSpheroid(s);
  }

  public Geographic asGeographic(Planar p, Geographic q) {

    Assert.isTrue(zone != -1, "Call #setParameters first");

    p.x = (p.x - FALSE_EASTING) / SCALE_FACTOR;
    p.y = (p.y - FALSE_NORTHING) / SCALE_FACTOR;
    transverseMercator.asGeographic(p, q);
    return q;
  }

  public Planar asPlanar(Geographic q0, Planar p) {

    Assert.isTrue(zone != -1, "Call #setParameters first");

    transverseMercator.asPlanar(q0, p);
    p.x = SCALE_FACTOR * p.x + FALSE_EASTING;
    p.y = SCALE_FACTOR * p.y + FALSE_NORTHING;
    return p;
  }

}

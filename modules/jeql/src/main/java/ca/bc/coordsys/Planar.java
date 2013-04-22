package ca.bc.coordsys;

/** 
 * A base class for planar coordinate systems.
 */
public class Planar {

  public double x, y, z;

  public Planar() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Planar(double _x, double _y) {
    x = _x;
    y = _y;
    z = 0;
  }

  public String toString() {
    return x + ", " + y;
  }
}


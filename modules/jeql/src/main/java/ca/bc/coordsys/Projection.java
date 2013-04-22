package ca.bc.coordsys;

/** 
 * This is the abstract base class for all projections.
 */
public abstract class Projection {

  protected Spheroid currentSpheroid;

  public void setSpheroid(Spheroid s) {
    currentSpheroid = s;
  }

  public abstract Planar asPlanar(Geographic q0, Planar p);

  public abstract Geographic asGeographic(Planar p, Geographic q);

}

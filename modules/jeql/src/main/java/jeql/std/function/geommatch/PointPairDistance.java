package jeql.std.function.geommatch;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Contains a pair of points and the distance between them.
 * Provides methods to update with a new point pair with
 * either maximum or minimum distance.
 */
public class PointPairDistance {

  private Coordinate[] pt = { new Coordinate(), new Coordinate() };
  private double distance = Double.NaN;
  private boolean isNull = true;

  public PointPairDistance()
  {
  }

  public void initialize() { isNull = true; }

  public void initialize(Coordinate p0, Coordinate p1)
  {
    pt[0].setCoordinate(p0);
    pt[1].setCoordinate(p1);
    distance = p0.distance(p1);
    isNull = false;
  }

  /**
   * Initializes the points, avoiding recomputing the distance.
   * @param p0
   * @param p1
   * @param distance the distance between p0 and p1
   */
  private void initialize(Coordinate p0, Coordinate p1, double distance)
  {
    pt[0].setCoordinate(p0);
    pt[1].setCoordinate(p1);
    this.distance = distance;
    isNull = false;
  }

  public double getDistance() { return distance; }

  public Coordinate[] getCoordinates() { return pt; }

  public Coordinate getCoordinate(int i) { return pt[i]; }

  public void setMaximum(PointPairDistance ptDist)
  {
    setMaximum(ptDist.pt[0], ptDist.pt[1]);
  }

  public void setMaximum(Coordinate p0, Coordinate p1)
  {
    if (isNull) {
      initialize(p0, p1);
      return;
    }
    double dist = p0.distance(p1);
    if (dist > distance)
      initialize(p0, p1, dist);
  }

  public void setMinimum(PointPairDistance ptDist)
  {
    setMinimum(ptDist.pt[0], ptDist.pt[1]);
  }

  public void setMinimum(Coordinate p0, Coordinate p1)
  {
    if (isNull) {
      initialize(p0, p1);
      return;
    }
    double dist = p0.distance(p1);
    if (dist < distance)
      initialize(p0, p1, dist);
  }
}

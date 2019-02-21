package jeql.std.function.geommatch;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;

public class CentroidAligner 
{
  private Geometry target;
  
  public CentroidAligner(Geometry target) {
    this.target = target;
  }
  
  /**
   * Aligns a geometry to the target geometry.
   * 
   * @param original
   * @return
   */
  public Geometry align(Geometry original) {
    Geometry aligned = (Geometry) original.clone();
    Coordinate targetCentroid = target.getCentroid().getCoordinate();
    Coordinate origCentroid = original.getCentroid().getCoordinate();
    Coordinate delta = subtract(targetCentroid, origCentroid);
    translate(aligned, delta);
    return aligned;
  }
  
  /**
   * Moves g so that c is at (0,0).
   * @param g the Geometry to modify
   * @param c the point to move to the origin
   */
  public void translate(Geometry g, final Coordinate delta) {
    g.apply(new CoordinateFilter() {
      public void filter(Coordinate coordinate) {
        coordinate.x += delta.x;
        coordinate.y += delta.y;
      }
    });
  }
    
  private Coordinate subtract(Coordinate p0, Coordinate p1)
  {
    return new Coordinate(p0.x - p1.x, p0.y - p1.y);
  }
}

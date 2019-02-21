package jeql.std.function.geommatch;

import org.locationtech.jts.geom.Geometry;

public class GeomMatchFunction 
{
  /**
   * Tests how well two geometries match based on the area of
   * their symmetric difference relative to the geometry areas.
   * The value computed is:
   * <pre>
   * 1 - area(symDiff(a, b)) / (area(a) + area(b)
   * </pre>
   * This value will lie between 0 and 1, inclusive.
   * Larger values indicate better matches, with 1 indicating a perfect match.
   * It is probably desirable to align the geometries 
   * based on their centroid first (using {@link #alignToCentroid} )
   * 
   * @param a a Geometry
   * @param b a Geometry
   * @return a value between 0 and 1
   */
  public static double symDiffArea(Geometry a, Geometry b) 
  { 
    if (a.isEmpty() || b.isEmpty()) {
      return 0; 
    }
    double symDiffArea = a.symDifference(b).getArea();
    return 1.0 - (symDiffArea / (a.getArea() + b.getArea()));
  }

  public static double hausdorffDistance(Geometry a, Geometry b) 
  { 
    return new VertexHausdorffDistance(a, b).distance(); 
  }

  public static double centroidDistance(Geometry a, Geometry b) 
  { 
    return a.getCentroid().distance(b.getCentroid()); 
  }

  public static double compactness(Geometry a, Geometry b) 
  { 
    double score = 1 - Math.abs(compactness(a) - compactness(b)); 
    return score;
  }

  /**
   * Uses (4 x pi x Area) / (Perimeter^2) as a measure of compactness. 
   * The maximum value is 1 (attained by circles).
   * The minimum value is 0 (attained by lines)
   */
  private static double compactness(Geometry g)
  {
    double len = g.getLength();
    return 4 * Math.PI * g.getArea() / (len * len);
  }
  
  /**
   * Translates a geometry so its centroid is the same as the centroid
   * of a target geometry.
   * 
   * @param src
   * @param target
   */
  public static Geometry alignToCentroid(Geometry src, Geometry target)
  {
    CentroidAligner aligner = new CentroidAligner(target);
    return aligner.align(src);
  }
}

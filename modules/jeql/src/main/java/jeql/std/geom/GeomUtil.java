package jeql.std.geom;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class GeomUtil {

  /**
   * Optimizes isWithinDistance by recursing into GeometryCollections 
   * 
   * @param g1
   * @param g2
   * @param dist
   * @return
   */
  public static boolean isWithinDistance(Geometry g1, Geometry g2, double dist) 
  { 
    if (g2.getNumGeometries() <= 1) {
      return g1.isWithinDistance(g2, dist);
    }
    for (int i = 0; i < g2.getNumGeometries(); i++) {   
      if (isWithinDistance(g1, g2.getGeometryN(i), dist))
        return true;
    }  
    return false;
  }
  

  public static String shortString(Geometry geom)
  {
    if (geom == null) return "";
    
    if (geom.getNumPoints() <= 5)
      return geom.toString();
    
    // TODO: improve retrieval of points for efficiency
    // TODO: show # of elements, points
    Coordinate[] pts = geom.getCoordinates();
    
    String elementCount = "";
    if (geom.getNumGeometries() > 1) {
      elementCount = geom.getNumGeometries() + " elts, ";
    }
    return geom.getGeometryType().toUpperCase() + " ( " 
    + ptsString(pts) + " )"
    +  "  --- " + elementCount + pts.length + " pts";
  }
  
  private static String ptsString(Coordinate[] pts) {
    return ptStr(pts[0]) + ", " + ptStr(pts[1])
    + " ... " + ptStr(pts[pts.length - 1]);
  }
  
  private static String ptStr(Coordinate p)
  {
    return p.x + " " + p.y;
  }
}

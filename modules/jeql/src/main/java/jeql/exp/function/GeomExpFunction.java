package jeql.exp.function;

import jeql.api.function.FunctionClass;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;

public class GeomExpFunction 
implements FunctionClass
{

  private static Geometry intersectsCacheGeom;
  private static PreparedGeometry intersectsCacheGeomPrep;
  
  public static boolean intersectsPrep(Geometry g1, Geometry g2) 
  { 
    if (intersectsCacheGeom == null
        || intersectsCacheGeom != g1) {
      intersectsCacheGeom = g1;
      intersectsCacheGeomPrep = PreparedGeometryFactory.prepare(g1);
    }
    return intersectsCacheGeomPrep.intersects(g2); 
  }

  private static Geometry containsCacheGeom;
  private static PreparedGeometry containsCacheGeomPrep;
  static int hitCount = 0;
  
  public static boolean containsPrep(Geometry g1, Geometry g2) 
  { 
    if (containsCacheGeom == null
        || containsCacheGeom != g1) {
      containsCacheGeom = g1;
      containsCacheGeomPrep = PreparedGeometryFactory.prepare(g1);
      
//      System.out.println(hitCount);
      hitCount = 0;
    }
    hitCount++;
    return containsCacheGeomPrep.contains(g2); 
  }

  private static Geometry intersectionCacheGeom;
  private static PreparedGeometry intersectionCacheGeomPrep;
  
  public static Geometry intersectionPrep(Geometry g1, Geometry g2) 
  { 
    if (intersectionCacheGeom == null
        || intersectionCacheGeom != g1) {
      intersectionCacheGeom = g1;
      intersectionCacheGeomPrep = PreparedGeometryFactory.prepare(g1);
    }
    hitCount++;
    if (! intersectionCacheGeomPrep.intersects(g2))
      return g2.getFactory().createGeometryCollection(null);
    if (intersectionCacheGeomPrep.contains(g2))
      return g2;
    return g1.intersection(g2);
  }


}

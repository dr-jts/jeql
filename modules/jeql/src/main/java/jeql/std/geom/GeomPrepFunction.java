package jeql.std.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jeql.api.function.FunctionClass;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;
import com.vividsolutions.jts.noding.FastSegmentSetIntersectionFinder;
import com.vividsolutions.jts.noding.SegmentStringUtil;

public class GeomPrepFunction 
implements FunctionClass
{
  private static Geometry intersectsCacheGeom;
  private static PreparedGeometry intersectsCacheGeomPrep;
  
  public static boolean intersects(Geometry cachedGeom, Geometry g2) 
  { 
    if (intersectsCacheGeom == null
        || intersectsCacheGeom != cachedGeom) {
      intersectsCacheGeom = cachedGeom;
      intersectsCacheGeomPrep = PreparedGeometryFactory.prepare(cachedGeom);
    }
    return intersectsCacheGeomPrep.intersects(g2); 
  }

  private static Geometry containsCacheGeom;
  private static PreparedGeometry containsCacheGeomPrep;
  static int hitCount = 0;
  
  public static boolean contains(Geometry cachedGeom, Geometry g2) 
  { 
    if (containsCacheGeom == null
        || containsCacheGeom != cachedGeom) {
      containsCacheGeom = cachedGeom;
      containsCacheGeomPrep = PreparedGeometryFactory.prepare(cachedGeom);
      
//      System.out.println(hitCount);
      hitCount = 0;
    }
    hitCount++;
    return containsCacheGeomPrep.contains(g2); 
  }

  private static Geometry intersectionCacheGeom;
  private static PreparedGeometry intersectionCacheGeomPrep;
  
  public static Geometry intersection(Geometry cachedGeom, Geometry g2) 
  { 
    if (intersectionCacheGeom == null
        || intersectionCacheGeom != cachedGeom) {
      intersectionCacheGeom = cachedGeom;
      intersectionCacheGeomPrep = PreparedGeometryFactory.prepare(cachedGeom);
    }
    hitCount++;
    if (! intersectionCacheGeomPrep.intersects(g2))
      return g2.getFactory().createGeometryCollection(null);
    if (intersectionCacheGeomPrep.contains(g2))
      return g2;
    return cachedGeom.intersection(g2);
  }

  public static Geometry intersectionSim(Geometry cachedGeom, Geometry g2) 
  { 
    if (intersectionCacheGeom == null
        || intersectionCacheGeom != cachedGeom) {
      intersectionCacheGeom = cachedGeom;
      intersectionCacheGeomPrep = PreparedGeometryFactory.prepare(cachedGeom);
    }
    hitCount++;
    if (! intersectionCacheGeomPrep.intersects(g2))
      return g2.getFactory().createGeometryCollection(null);
    if (intersectionCacheGeomPrep.contains(g2))
      return g2;
    return intersectionSim(intersectionCacheGeomPrep, g2);
  }

  private static Geometry intersectionSim(
      PreparedGeometry pg, Geometry g2)
  {
    PreparedPolygon ppoly = (PreparedPolygon) pg;
    FastSegmentSetIntersectionFinder intf = ppoly.getIntersectionFinder();
    
    Coordinate[] pts = g2.getCoordinates();
    
    List segStrings = SegmentStringUtil.extractSegmentStrings(g2);
    intf.intersects(segStrings );
    return g2;
  }


  
}

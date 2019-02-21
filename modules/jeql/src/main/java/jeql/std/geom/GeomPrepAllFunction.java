package jeql.std.geom;

import java.util.HashMap;
import java.util.Map;

import jeql.api.function.FunctionClass;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;

/**
 * Prepares all LHS geometries used, and caches
 * them for future use.
 * 
 * @author Martin Davis
 *
 */
public class GeomPrepAllFunction 
implements FunctionClass
{
  private static Map<Geometry, PreparedGeometry> cache = new HashMap<Geometry, PreparedGeometry>();
  
  private static PreparedGeometry cacheFind(Geometry g)
  {
    PreparedGeometry pg = cache.get(g);
    if (pg == null) {
      pg = PreparedGeometryFactory.prepare(g);
      cache.put(g, pg);
      //System.out.println("cache size = " + cache.size());
    }
    return pg;
  }
  
  public static boolean contains(Geometry g1, Geometry g2) 
  { 
    return cacheFind(g1).contains(g2); 
  }


}

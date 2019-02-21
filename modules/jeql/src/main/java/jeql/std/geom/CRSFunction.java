package jeql.std.geom;

import java.util.HashMap;
import java.util.Map;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;

public class CRSFunction 
{
  public static double DMStoDD(String dmsStr)
  {
    String dms = dmsStr.trim();
    // Non-numeric substrings are assumed to be separators (including blanks)
    String[] comp = dms.split("[^0-9-.]+");
    if (comp.length <= 0)
      return 0.0;
    
    double dd = Double.parseDouble(comp[0]);
    int sgn = dd < 0 ? -1 : 1;
    
    if (comp.length >= 2)
      dd += sgn * Double.parseDouble(comp[1])/60;
    if (comp.length >= 3)
      dd += sgn * Double.parseDouble(comp[2])/3600;
    
    return dd;
  }
  
  /*
  public static String DDtoDMS(double dd, String degreeSym, String minSym, String secSym)
  {
    
  }
  */
  
  public static String projectionName(String projSpec)
  {
    return createCRS(projSpec).toString();
  }
  
  public static String proj4Description(String projSpec)
  {
    try {
      CoordinateReferenceSystem crs = createCRS(projSpec);
      if (crs == null) return null;
      return crs.getParameterString();
    }
    catch (UnsupportedOperationException e) {
      return null; // e.getMessage();
    }
  }

  /**
   * Transforms from one projection to another.
   * 
   * @param g
   * @param projSpec1
   * @param projSpec2
   * @return
   */
  public static Geometry transform(Geometry g, String projSpec1, String projSpec2)
  {
    Geometry g2 = (Geometry) g.clone();
    
    // TODO: cache this as well
    g2.apply(new CoordinateTransformFilter(
        getCRS(projSpec1),
        getCRS(projSpec2)
               ));
    return g2;
  }

  /**
   * Transforms from lon/lat in decimal degrees to a project coordinate system.
   * 
   * @param g
   * @param projSpec
   * @return
   */
  public static Geometry project(Geometry g, String projSpec)
  {
    Geometry g2 = (Geometry) g.clone();
    
    CoordinateReferenceSystem crs = getCRS(projSpec);
    CoordinateReferenceSystem geo = crs.createGeographic();
    // TODO: cache this as well
    g2.apply(new CoordinateTransformFilter(
        geo,
        crs
               ));
    return g2;
  }

  /**
   * Transforms from a projected coordinate system to lon/lat in decimal degrees.
   * 
   * @param g
   * @param projSpec
   * @return
   */
  public static Geometry inverseProject(Geometry g, String projSpec)
  {
    Geometry g2 = (Geometry) g.clone();
    
    CoordinateReferenceSystem crs = getCRS(projSpec);
    CoordinateReferenceSystem geo = crs.createGeographic();
    // TODO: cache this as well
    g2.apply(new CoordinateTransformFilter(
        crs,
        geo
               ));
    return g2;
  }

  private static Map projCache = new HashMap();
  private static CRSFactory crsFactory = new CRSFactory();
  
  private static CoordinateReferenceSystem getCRS(String projStr)
  {
    CoordinateReferenceSystem proj = (CoordinateReferenceSystem) projCache.get(projStr);
    if (proj == null) {
      proj = createCRS(projStr);
      projCache.put(projStr, proj);
    }
    if (proj == null) 
      throw new IllegalArgumentException("Can't create CRS for '" + projStr + "'");
    return proj;
  }
  
  private static CoordinateReferenceSystem createCRS(String projStr)
  {
    // TODO: split argsc into array
    if (projStr.indexOf('=') >= 0) {
      return crsFactory.createFromParameters("Anon", projStr);
    }
    return crsFactory.createFromName(projStr);
  }
  
  private static class CoordinateTransformFilter
  implements CoordinateFilter
  {
    private CoordinateReferenceSystem src;
    private CoordinateReferenceSystem dest;
    private ProjCoordinate srcPt = new ProjCoordinate();
    private ProjCoordinate destPt = new ProjCoordinate();
    private CoordinateTransform transform = null;
    
    public CoordinateTransformFilter(CoordinateReferenceSystem src, CoordinateReferenceSystem dest)
    {
      this.src = src;
      this.dest = dest;
      CoordinateTransformFactory ctf = new CoordinateTransformFactory();
      transform = ctf.createTransform(src, dest);
    }
    
    public void filter(Coordinate coord) 
    {
      srcPt.x = coord.x;
      srcPt.y = coord.y;
      transform.transform(srcPt, destPt);
      coord.x = destPt.x;
      coord.y = destPt.y;
    }

  }
}



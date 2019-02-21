package jeql.jts.geodetic;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

public class GeodeticSplitter 
{
  private Geometry line;
  
  public GeodeticSplitter(Geometry line)
  {
    this.line = line;
  }

  public Geometry split()
  {
    MultiCoordinateList mcl = new MultiCoordinateList(false);
    Coordinate[] pts = line.getCoordinates();
    mcl.add(pts[0]);
    for (int i = 1; i < pts.length; i++) {
      Coordinate p0 = pts[i-1];
      Coordinate p1 = pts[i];
      if (crossesDateLine(p0, p1)) {
        double latCross = latitudeOfCrossing(p0, p1);
        if (p0.x < 0) {
          mcl.add(new Coordinate(-180, latCross));
          mcl.finish();
          mcl.add(new Coordinate(180, latCross));
        }
        else {
          mcl.add(new Coordinate(180, latCross));
          mcl.finish();
          mcl.add(new Coordinate(-180, latCross));          
        }
      }
      mcl.add(p1);
    }
    return toMultiLineString(mcl);
  }
  
  private static boolean crossesDateLine(Coordinate p0, Coordinate p1)
  {
    if (p0.x < -90 && p1.x > 90) return true;
    if (p1.x < -90 && p0.x > 90) return true;
    return false;
  }
  
  private static double latitudeOfCrossing(Coordinate p0, Coordinate p1)
  {
    // compute in the lon-lat plane - which isn't accurate for long lines
    double x0 = p0.x;
    double y0 = p0.y;
    double x1 = p1.x + 360;;
    double y1 = p1.y;
    if (p0.x < 0) {
      x0 = p1.x;
      y0 = p1.y;
      x1 = p0.x + 360;
      y1 = p0.y;
    }
    
    double dx = x1 - x0;
    double dy = y1 - y0;
    
    double x = 180 - x0;
    double y = dy/dx * x;
    
    return y + y0;
  }
  
  private MultiLineString toMultiLineString(MultiCoordinateList mcl)
  {
    Coordinate[][] pts = mcl.toCoordinateArrays();
    LineString[] lines = new LineString[pts.length];
    for (int i = 0; i < pts.length; i++) {
      lines[i] = toLineString(pts[i]);
    }
    return line.getFactory().createMultiLineString(lines);
  }
  
  private LineString toLineString(Coordinate[] pts)
  {
    // prevent lines of length 1
    if (pts.length == 1) {
      pts = new Coordinate[] { pts[0], new Coordinate(pts[0]) };
    }
    return line.getFactory().createLineString(pts);
  }
}
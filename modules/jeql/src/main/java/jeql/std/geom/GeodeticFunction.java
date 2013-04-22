package jeql.std.geom;

import jeql.api.function.FunctionClass;
import jeql.jts.geodetic.GeodeticDensifier;
import jeql.jts.geodetic.GeodeticSplitter;

import com.vividsolutions.jts.geom.Geometry;

public class GeodeticFunction 
implements FunctionClass
{
  public static Geometry arc(double x0, double y0, double x1, double y1, double maxSegLen)
  {
    Geometry line = GeomFunction.createLine(x0, y0, x1, y1);
    GeodeticDensifier gd = new GeodeticDensifier(line);
    return gd.densify(maxSegLen);
  }
  
  public static Geometry densify(Geometry line, double maxSegLen)
  {
    GeodeticDensifier gd = new GeodeticDensifier(line);
    return gd.densify(maxSegLen);
  }
  
  public static Geometry split180(Geometry line)
  {
    GeodeticSplitter splitter = new GeodeticSplitter(line);
    return splitter.split();
  }
  
}
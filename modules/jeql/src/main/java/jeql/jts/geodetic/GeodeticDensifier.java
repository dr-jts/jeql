package jeql.jts.geodetic;

import jeql.std.function.MathFunction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;

public class GeodeticDensifier
{
  private Geometry line;
  
  public GeodeticDensifier(Geometry line)
  {
    this.line = line;
  }
  
  public Geometry densify(double maxSegLenDeg)
  {
    double maxSegLenRad = MathFunction.toRadians(maxSegLenDeg);
    
    Coordinate p0 = line.getCoordinates()[0];
    Coordinate p1 = line.getCoordinates()[1];
    
    CoordinateList coords = new CoordinateList();
    
    Coordinate dc1 = GeodeticCoord.toCartesian(p1);
    //coords.add(dc0);
    densify(GeodeticCoord.toCartesian(p0), GeodeticCoord.toCartesian(p1), maxSegLenRad, coords);
    coords.add(dc1);
    
    // convert back to geo
    Coordinate[] dcPts = coords.toCoordinateArray();
    for (int i = 0; i < dcPts.length; i++) {
      dcPts[i] = GeodeticCoord.toGeodetic(dcPts[i]);
    }
    return line.getFactory().createLineString(dcPts);
  }
  
  private void densify(Coordinate dc0, Coordinate dc1, double maxSegLen, CoordinateList coords)
  {
    Coordinate mid = GeodeticCoord.midPtDC(dc0, dc1);
    double dist = GeodeticCoord.distance3D(dc0, mid);
    if (dist < maxSegLen) {
      coords.add(dc0, false);
      coords.add(mid, false);
      return;
    }
    // else recursively densify
    densify(dc0, mid, maxSegLen, coords);
    densify(mid, dc1, maxSegLen, coords);
  }
  
  
}
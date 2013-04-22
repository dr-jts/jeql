package jeql.std.geom;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import jeql.api.function.FunctionClass;

public class GridFunction
implements FunctionClass
{
  // hexagonal constant = height of hexagon of side 1
  private static final double HEX_HGT = Math.sqrt(3.0);
  
  public static int hexIndex(Geometry pt, Geometry extent, int nCellsX)
  {
    double envW = extent.getEnvelopeInternal().getWidth();
    double cellSide = envW / (nCellsX -1) / 1.5;
    Coordinate coord = pt.getCoordinate();
    
    // transform coordinates so hex cell side length is 1
    double x1 = (coord.x - extent.getEnvelopeInternal().getMinX()) / cellSide;
    double y1 = (coord.y - extent.getEnvelopeInternal().getMinY()) / cellSide;
    
    // transform so hex squares have side = 1
    // and compute base index for hex square
    double xp = (x1 + 1) / 1.5;
    int ixp = (int) xp;
    boolean isxpOdd = (ixp % 2) == 1;
    double yp = y1 / HEX_HGT;
    if (! isxpOdd) {
      yp += 0.5;
    }
    int iyp = (int) yp;
    
    // internal coordinates for hex square
    double u = xp - ixp;
    double v = yp - iyp;
    
    // index adjustment values inside hex square
    int dx = 0;
    int dy = 0;
    
    // compute adjustment values for hex square left upper and lower corners
    if (3 * u <= 1.0) {
      // lower left corner
      if (v < 0.5 
          && 3.0 * u + 2.0 * v < 1.0) {
        dx = -1;
        dy = isxpOdd ? 0 : -1;
      }
      // upper left corner
      else if (v > 0.5 
          && 3.0 * u < 2.0 * (v - 0.5)) {
        dx = -1;
        dy = isxpOdd ? 1 : 0;
      }
    }
    // adjust to get final hex cell coordinates
    int I = ixp + dx;
    int J = iyp + dy;
    
    // convert to single numeric index (row-major order)
    int index = I + nCellsX * J;
    return index;
  }
  
  public static Geometry hexGeom(int index, Geometry extent, int nCellsX)
  {
    double envW = extent.getEnvelopeInternal().getWidth();
    double minx = extent.getEnvelopeInternal().getMinX();
    double miny = extent.getEnvelopeInternal().getMinY();
    double cellSide = envW / (nCellsX -1) / 1.5;

    int J = index / nCellsX;
    int I = index % nCellsX;
    double xp = -1 + 1.5 * I;
    double yp = HEX_HGT * J;
    if (I % 2 == 0) 
      yp -= HEX_HGT / 2.0;
    
    double x = minx + cellSide * xp; 
    double y = miny + cellSide * yp;
    
    double hh = cellSide * HEX_HGT;
    Coordinate[] pts = new Coordinate[] {
        new Coordinate(x + 0.5 * cellSide, y),
        new Coordinate(x, y + hh / 2),
        new Coordinate(x + 0.5 * cellSide, y + hh),
        new Coordinate(x + 1.5 * cellSide, y + hh),
        new Coordinate(x + 2 * cellSide, y + hh / 2),
        new Coordinate(x + 1.5 * cellSide, y),
        new Coordinate(x + 0.5 * cellSide, y)
    };
        
    return GeomFunction.geomFactory.createPolygon(
        GeomFunction.geomFactory.createLinearRing(pts), null);
  }
}

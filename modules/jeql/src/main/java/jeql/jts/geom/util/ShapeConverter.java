package jeql.jts.geom.util;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class ShapeConverter 
{
  private Shape shp;
  
  public ShapeConverter(Shape shp) {
    this.shp = shp;
  }

  private Geometry toPolygon(GeometryFactory geomFact, double flatness)
  {
    PathIterator pathIt = shp.getPathIterator(new AffineTransform(), flatness);
    List coordArrays = PathConverter.convert(pathIt);
    Coordinate[] pts = (Coordinate[]) coordArrays.get(0);
    LinearRing shell = geomFact.createLinearRing(pts);
    return geomFact.createPolygon(shell, null);
  }
  
}

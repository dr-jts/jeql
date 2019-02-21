package jeql.jts.geom.util;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

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

package jeql.jts.geom.util;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

public class ShapeUtil 
{
  // emprically determined to provide good result
  private static final double FLATNESS_FACTOR = 4000;
  
  public static Geometry fromFont(String str, int pointSize, GeometryFactory geomFact)
  {
    double flatness = pointSize / FLATNESS_FACTOR;

    char[] chs = str.toCharArray();
    FontRenderContext fontContext = new FontRenderContext(null, false, false);
    Font font = new Font("Serif", Font.PLAIN, pointSize);
    GlyphVector gv = font.createGlyphVector(fontContext, chs);
    List polys = new ArrayList();
    for (int i = 0; i < gv.getNumGlyphs(); i++) {
      Geometry geom = toPolygonal(gv.getGlyphOutline(i), flatness, geomFact);
      for (int j = 0; j < geom.getNumGeometries(); j++) {
        polys.add(geom.getGeometryN(j));
      }
    }
    return geomFact.buildGeometry(polys);
  }
  
  private static Geometry toPolygonal(Shape shp, double flatness, GeometryFactory geomFact)
  {
    PathIterator pathIt = shp.getPathIterator(AffineTransform.getScaleInstance(1, -1), flatness);
    return PathConverter.convert(pathIt, geomFact);
  }
    
  private static Geometry OLDtoPolygon(Shape shp, double flatness, GeometryFactory geomFact)
  {
    PathIterator pathIt = shp.getPathIterator(AffineTransform.getScaleInstance(1, -1), flatness);
    List pathPtSeq = PathConverter.convert(pathIt);
    Coordinate[] pts = (Coordinate[]) pathPtSeq.get(0);
    LinearRing shell = geomFact.createLinearRing(pts);
    LinearRing[] holes = new LinearRing[pathPtSeq.size() - 1];
    for (int i = 1; i < pathPtSeq.size(); i++) {
      holes[i-1] = geomFact.createLinearRing((Coordinate[]) pathPtSeq.get(i));
    }
    return geomFact.createPolygon(shell, holes);
  }
}

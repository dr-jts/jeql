package jeql.syntax.util;

import java.util.List;

import jeql.std.geom.GeomFunction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * Utilities for building geometries during parsing
 * 
 * @author Martin Davis
 *
 */
public class GeometryBuilder 
{

  public static Geometry createPoint(Coordinate seq)
  {
    return GeomFunction.geomFactory.createPoint(seq);
  }
  
  public static Geometry createLineString(Coordinate[] seq)
  {
    return GeomFunction.geomFactory.createLineString(seq);
  }
  
  public static Geometry createLinearRing(Coordinate[] seq)
  {
    return GeomFunction.geomFactory.createLinearRing(seq);
  }
  
  public static Geometry createPolygon(Coordinate[] shell, List holeCoords)
  {
    if (shell == null)
      return GeomFunction.geomFactory.createPolygon(null, null);
    
    LinearRing shellRing = GeomFunction.geomFactory.createLinearRing(shell);
    
    LinearRing[] holeRings = new LinearRing[holeCoords.size()];
    for (int i = 0; i < holeRings.length; i++) {
      holeRings[i] = GeomFunction.geomFactory
            .createLinearRing(((Coordinate[]) holeCoords.get(i))); 
    }
    return GeomFunction.geomFactory.createPolygon(shellRing, holeRings);
  }
  public static Geometry createMultiPoint(List geoms)
  {
    return GeomFunction.geomFactory.createMultiPoint(
        GeometryFactory.toPointArray(geoms));
  }
  public static Geometry createMultiLineString(List geoms)
  {
    return GeomFunction.geomFactory.createMultiLineString(
        GeometryFactory.toLineStringArray(geoms));
  }
  public static Geometry createMultiPolygon(List geoms)
  {
    return GeomFunction.geomFactory.createMultiPolygon(
        GeometryFactory.toPolygonArray(geoms));
  }
  public static Geometry createGeometryCollection(List geoms)
  {
    return GeomFunction.geomFactory.createGeometryCollection(
        GeometryFactory.toGeometryArray(geoms));
  }
  public static Geometry createBox(Coordinate p1, Coordinate p2)
  {
    Envelope env = new Envelope(p1.x, p2.x, p1.y, p2.y);
    return GeomFunction.geomFactory.toGeometry(env);
  }
}

package jeql.std.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollectionIterator;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Lineal;
import org.locationtech.jts.geom.OctagonalEnvelope;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.Puntal;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.geom.util.LinearComponentExtracter;
import org.locationtech.jts.geom.util.SineStarFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.io.gml2.GMLReader;
import org.locationtech.jts.io.gml2.GMLWriter;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.linemerge.LineSequencer;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;
import org.locationtech.jts.util.GeometricShapeFactory;

import jeql.api.function.FunctionClass;
import jeql.jts.geom.util.GeometricShapeFactoryExt;
import jeql.jts.geom.util.ShapeUtil;

public class GeomFunction 
implements FunctionClass
{
  public static GeometryFactory geomFactory = new GeometryFactory();
  
  public static double area(Geometry geom) { return geom.getArea(); }
  public static double length(Geometry geom) { return geom.getLength(); }
  
  public static int numPoints(Geometry geom) { return geom.getNumPoints(); }
  public static int numGeometries(Geometry geom) { return geom.getNumGeometries(); }
  public static int numInteriorRings(Geometry geom) 
  { 
    int intRingCount = 0;
    GeometryCollectionIterator it = new GeometryCollectionIterator(geom);
    while (it.hasNext()) {
      Geometry g = (Geometry) it.next();
      if (g instanceof Polygon) {
        intRingCount += ((Polygon) g).getNumInteriorRing();
      }
    }
    return intRingCount; 
  }
  
  /**
   * Gets an interior ring of a Polygon.
   * The index of Interior rings is 0-based.
   * 
   * @param geom a Polygon
   * @param i the index of an interior ring (0-based)
   * @return the i'th interior ring (LinearRing)
   */
  public static Geometry interiorRing(Geometry geom, int i)
  {
    return ((Polygon) geom).getInteriorRingN(i);
  }
  
  /**
   * Gets the exterior LinearRing of a Polygon
   * @param geom a Polygon
   * @return the exterior ring (LinearRing)
   */
  public static Geometry exteriorRing(Geometry geom)
  {
    return ((Polygon) geom).getExteriorRing();
  }
  
  public static int dimension(Geometry geom) { return geom.getDimension(); }
  
  public static String geometryType(Geometry geom) { return geom.getGeometryType(); }
  public static boolean isPolygonal(Geometry geom) { return geom instanceof Polygonal; }
  public static boolean isLineal(Geometry geom) { return geom instanceof Lineal; }
  public static boolean isPuntal(Geometry geom) { return geom instanceof Puntal; }
  
  public static Geometry envelope(Geometry geom)  { return geom.getEnvelope(); }
  public static Geometry boundary(Geometry geom)  { return geom.getBoundary(); }
  public static Geometry centroid(Geometry geom)  { return geom.getCentroid(); }
  public static Geometry interiorPoint(Geometry geom) { return geom.getInteriorPoint(); }
  public static Geometry buffer(Geometry geom, double dist) { return geom.buffer(dist); }
  public static Geometry buffer(Geometry geom, double dist, int quadSegs) { return geom.buffer(dist, quadSegs); }
  public static Geometry convexHull(Geometry geom) { return geom.convexHull(); }
  public static Geometry intersection(Geometry g1, Geometry g2) { return g1.intersection(g2); }
  public static Geometry union(Geometry g1, Geometry g2) { return g1.union(g2); }
  public static Geometry union(Geometry g1) { return g1.union(); }
  public static Geometry difference(Geometry g1, Geometry g2) { return g1.difference(g2); }
  public static Geometry symDifference(Geometry g1, Geometry g2) 
  { 
    try {
     return g1.symDifference(g2);
    }
    catch (TopologyException ex) {
      System.out.println(g1);
    }
    return null;
  }

  public static boolean isValid(Geometry g) { return g.isValid(); }
  public static boolean isEmpty(Geometry g) { return g.isEmpty(); }
  public static boolean isSimple(Geometry g) { return g.isSimple(); }
  
  public static boolean intersects(Geometry g1, Geometry g2) { return g1.intersects(g2); }
  public static boolean contains(Geometry g1, Geometry g2) { return g1.contains(g2); }
  public static boolean crosses(Geometry g1, Geometry g2) { return g1.crosses(g2); }
  public static boolean disjoint(Geometry g1, Geometry g2) { return g1.disjoint(g2); }
  public static boolean equals(Geometry g1, Geometry g2) { return g1.equals(g2); }
  public static boolean overlaps(Geometry g1, Geometry g2) { return g1.overlaps(g2); }
  public static boolean touches(Geometry g1, Geometry g2) { return g1.touches(g2); }
  public static boolean covers(Geometry g1, Geometry g2) { return g1.covers(g2); }
  public static boolean coveredBy(Geometry g1, Geometry g2) { return g1.coveredBy(g2); }
  public static boolean within(Geometry g1, Geometry g2) { return g1.within(g2); }
  public static boolean relate(Geometry g1, Geometry g2, String relatePattern) { return g1.relate(g2, relatePattern); }
  public static String relatePattern(Geometry g1, Geometry g2) { return g1.relate(g2).toString(); }
  
  /**
   * Tests whether the geometry envelopes intersect.
   * 
   * @param g1
   * @param g2
   * @return true if the envelopes intersect
   */
  public static boolean envelopesIntersect(Geometry g1, Geometry g2)
  {
    return g1.getEnvelopeInternal().intersects(g2.getEnvelopeInternal());
  }
  
  public static double distance(Geometry g1, Geometry g2) { return g1.distance(g2); }
  
  public static boolean isWithinDistance(Geometry g1, Geometry g2, double dist) 
  { 
    return GeomUtil.isWithinDistance(g1, g2, dist);
//    return g1.isWithinDistance(g2, dist); 
  }
  
  public static String toString(Geometry g) { return g.toString(); }

  public static String toString(Geometry geom, int coordsPerLine)
  {
      WKTWriter writer = new WKTWriter();
      writer.setMaxCoordinatesPerLine(coordsPerLine);
      return writer.writeFormatted(geom);
  }

  public static String toWKT(Geometry g) {
    return g.toString();
  }
  
  public static Geometry fromWKT(String wkt) throws ParseException {
    WKTReader rdr = new WKTReader();
    Geometry g = rdr.read(wkt);
    return g;
  }

  public static String toWKB(Geometry g) {
    WKBWriter writer = new WKBWriter();
    return WKBWriter.bytesToHex(writer.write(g));
  }
  
  public static Geometry fromWKB(String wkbHex) throws ParseException {
    byte[] wkb = WKBReader.hexToBytes(wkbHex);
    WKBReader rdr = new WKBReader();
    Geometry g = rdr.read(wkb);
    return g;
  }

  public static String toWKB(Geometry g, int dimension) {
    WKBWriter writer = new WKBWriter(dimension);
    return WKBWriter.bytesToHex(writer.write(g));
  }
  
  public static Geometry fromGML2(String gml) throws Exception
  {
    String gmlTrim = gml.trim();
    if (gmlTrim.length() <= 0) return null;
    
    GMLReader rdr = new GMLReader();
    return rdr.read(gml, geomFactory);
  }
  
  public static String toGML2(Geometry g)
  {
    GMLWriter writer = new GMLWriter();
    return writer.write(g);
  }
  
  public static Geometry normalize(Geometry g)
  {
    Geometry g2 = (Geometry) g.clone();
    g2.normalize();
    return g2;
  }
  
  public static double x(Geometry g) { return g.getCoordinate().x; }
  public static double y(Geometry g) { return g.getCoordinate().y; }
  public static double z(Geometry g) { return g.getCoordinate().z; }
  
  public static Geometry setZ(Geometry g, double z)
  {
    if (g == null) return null;
    Geometry gz = (Geometry) g.clone();
    gz.apply(new SetZCoordinateFilter(z));
    return gz;
  }
  
  private static class SetZCoordinateFilter
  implements CoordinateFilter
  {
    private double z;
    
    public SetZCoordinateFilter(double z)
    {
      this.z = z;
    }
    public void filter(Coordinate p)
    {
      p.z = z;
    }
  }
  
  public static double maxX(Geometry g) { return g.getEnvelopeInternal().getMaxX(); }
  public static double minX(Geometry g) { return g.getEnvelopeInternal().getMinX(); }
  public static double maxY(Geometry g) { return g.getEnvelopeInternal().getMaxY(); }
  public static double minY(Geometry g) { return g.getEnvelopeInternal().getMinY(); }

  public static double width(Geometry g)  { return g.getEnvelopeInternal().getWidth(); }
  public static double height(Geometry g) { return g.getEnvelopeInternal().getHeight(); } 
  
  public static double deltaX(Geometry g)  { return g.getEnvelopeInternal().getWidth(); }
  public static double deltaY(Geometry g) { return g.getEnvelopeInternal().getHeight(); } 
  
  public static Geometry extractPoint(Geometry geom, int index)
  {
    Coordinate[] pts = geom.getCoordinates();
    Coordinate pt = pts[index];
    return geomFactory.createPoint(pt);  
  }
  
  public static Geometry extractComponent(Geometry geom, int index)
  {
    return geom.getGeometryN(index);  
  }
  
  public static Geometry createPoint(double x, double y)
  {
    return geomFactory.createPoint(new Coordinate(x, y));
  }
  
  public static Geometry createPoint(double x, double y, double z)
  {
    return geomFactory.createPoint(new Coordinate(x, y, z));
  }
  
  public static Geometry createLine(double x0, double y0, double x1, double y1)
  {
    return geomFactory.createLineString(new Coordinate[]
          { new Coordinate(x0, y0), new Coordinate(x1, y1) });
  }

  public static Geometry createLine(Geometry geom)
  {
    return geomFactory.createLineString(connectPts(geom, false));
  }

  public static Geometry createLineFromPoints(Point p0, Point p1)
  {
    return geomFactory.createLineString(
        new Coordinate[] {
            p0.getCoordinate(),
            p1.getCoordinate()
        });
  }

  public static Geometry createLineFromPoints(Point p0, Point p1, Point p2)
  {
    return geomFactory.createLineString(
        new Coordinate[] {
            p0.getCoordinate(),
            p1.getCoordinate(),
            p2.getCoordinate()
        });
  }

  public static Geometry createPolygon(Point p0, Point p1, Point p2)
  {
    return geomFactory.createPolygon(
        geomFactory.createLinearRing(
        new Coordinate[] {
            p0.getCoordinate(),
            p1.getCoordinate(),
            p2.getCoordinate(),
            p0.getCoordinate()
        }), null);
  }

  public static Geometry createPolygon(Geometry geom)
  {
    return geomFactory.createPolygon(connectPts(geom, true));
  }

  public static Geometry createPolygon(Point p0, Point p1, Point p2, Point p3)
  {
    return geomFactory.createPolygon(
        geomFactory.createLinearRing(
        new Coordinate[] {
            p0.getCoordinate(),
            p1.getCoordinate(),
            p2.getCoordinate(),
            p3.getCoordinate(),
            p0.getCoordinate()
        }), null);
  }

  /*
  public static Geometry createPolygonFromPoints(Point p0, Point p1, Point p2)
  {
    return geomFactory.createPolygon(
        geomFactory.createLinearRing(
        new Coordinate[] {
            p0.getCoordinate(),
            p1.getCoordinate(),
            p2.getCoordinate(),
            p0.getCoordinate()
        }), null);
  }

  public static Geometry createPolygonFromPoints(Point p0, Point p1, Point p2, Point p3)
  {
    return geomFactory.createPolygon(
        geomFactory.createLinearRing(
        new Coordinate[] {
            p0.getCoordinate(),
            p1.getCoordinate(),
            p2.getCoordinate(),
            p3.getCoordinate(),
            p0.getCoordinate()
        }), null);
  }
*/

  public static Geometry createBox(double xMin, double yMin, double xMax, double yMax)
  {
    return geomFactory.toGeometry(new Envelope(xMin, xMax, yMin, yMax));
  }
  
  public static Geometry createBoxExtent(double x, double y, double dx, double dy)
  {
    return geomFactory.toGeometry(new Envelope(x, x + dx, y, y + dy));
  }
  
  public static Geometry createBoxOrigin(double x, double y, double dx, double dy)
  {
    return geomFactory.toGeometry(new Envelope(x, x + dx, y, y + dy));
  }
  
  public static Geometry createBoxCenter(double x, double y, double dx, double dy)
  {
    return geomFactory.toGeometry(new Envelope(x - dx/2, x + dx/2, y - dy/2, y + dy/2));
  }
  
  public static Geometry createBoxFromPoints(Point p0, Point p1)
  {
    return geomFactory.toGeometry(new Envelope(
            p0.getCoordinate(),
            p1.getCoordinate()
        ));
  }

  public static Geometry createCircle(Point p0, double radius, int nSides)
  {
    GeometricShapeFactory shape = new GeometricShapeFactory(geomFactory);
    shape.setCentre(p0.getCoordinate());
    shape.setSize(2 * radius);
    shape.setNumPoints(nSides);
    return shape.createCircle();
  }
  
  public static Geometry createCircleArcPolygon(Point p0, double startRad, double sizeRad, double radius, int nSides)
  {
    GeometricShapeFactory shape = new GeometricShapeFactory(geomFactory);
    shape.setCentre(p0.getCoordinate());
    shape.setSize(2 * radius);
    shape.setNumPoints(nSides);
    return shape.createArcPolygon(startRad, sizeRad);
  }
  
  public static Geometry createEllipticalArcPolygon(Point p0, double startRad, double sizeRad, double width, double height, int nSides)
  {
    GeometricShapeFactory shape = new GeometricShapeFactory(geomFactory);
    shape.setCentre(p0.getCoordinate());
    shape.setWidth(width);
    shape.setHeight(height);
    shape.setNumPoints(nSides);
    return shape.createArcPolygon(startRad, sizeRad);
  }
  
  public static Geometry createStar(Point p0, double radius, double armRatio, int nArms)
  {
    GeometricShapeFactoryExt shape = new GeometricShapeFactoryExt(geomFactory);
    shape.setCentre(p0.getCoordinate());
    shape.setSize(2 * radius);
    shape.setNumPoints(nArms * 2);
    shape.setNumArms(nArms);
    shape.setArmLengthRatio(armRatio);
    return shape.createStar();
  }
  
  public static Geometry createSineStar(Point p0, double radius, double armRatio, int nArms, int nPts)
  {
    SineStarFactory shape = new SineStarFactory(geomFactory);
    shape.setCentre(p0.getCoordinate());
    shape.setSize(2 * radius);
    shape.setNumPoints(nPts);
    shape.setNumArms(nArms);
    shape.setArmLengthRatio(armRatio);
    return shape.createSineStar();
  }
  
  public static Geometry createText(String s, int pointSize)
  {
    return ShapeUtil.fromFont(s, pointSize, geomFactory);
  }
  
  public static Geometry octagonalEnvelope(Geometry g)
  {
    if (g == null) return null;
    OctagonalEnvelope oct = new OctagonalEnvelope(g);
    return oct.toGeometry(g.getFactory());
  }
  
  public static Geometry minimumCircle(Geometry g)
  {
    if (g == null) return null;
    MinimumBoundingCircle mbc = new MinimumBoundingCircle(g);
    return mbc.getCircle();
  }
  
  public static Geometry minimumRectangle(Geometry g)
  {
    if (g == null) return null;
    MinimumDiameter md = new MinimumDiameter(g);
    return md.getMinimumRectangle();
  }
  
  /**
   * Creates a GeometryCollection from two geometries A and B.
   * 
   *  
   * @param a
   * @param b
   * @return
   */
  // TODO: flatten GeomColls which are input
  public static Geometry collect(Geometry a, Geometry b)
  {
    Geometry[] gs = new Geometry[] { a, b };
    return geomFactory.createGeometryCollection(gs);
  }
  
  public static Geometry collect(Geometry a, Geometry b, Geometry c)
  {
    Geometry[] gs = new Geometry[] { a, b, c };
    return geomFactory.createGeometryCollection(gs);
  }
  
  /**
   * Converts geometries to equivalent MultiGeometries
   * 
   * @param geom
   * @return
   */
  public static Geometry toMulti(Geometry geom)
  {
      if (geom instanceof Point) {
          return geom.getFactory().createMultiPoint(new Point[] { (Point) geom} );
      }
      else if (geom instanceof LineString) {
          return geom.getFactory().createMultiLineString(new LineString[] { (LineString) geom} );
      }
      else if (geom instanceof Polygon) {
          return geom.getFactory().createMultiPolygon(new Polygon[] { (Polygon) geom} );
      }
      // assume geom is already a multi (including GeometryCollections)
      return geom;
  }
  
  //=========== Line Handling  ====================
  
  public static Geometry lineMerge(Geometry g)
  {
    LineMerger merger = new LineMerger();
    merger.add(g);
    Collection lines = merger.getMergedLineStrings();
    return g.getFactory().buildGeometry(lines);
  }

  public static Geometry lineSequence(Geometry geom)
  {
    LineSequencer sequencer = new LineSequencer();
    sequencer.add(geom);
    return sequencer.getSequencedLineStrings();
  }
 
  public static Geometry lineConnect(Geometry geom)
  {
    CoordinateList pts = new CoordinateList();
    pts.add(geom.getCoordinates(), true);
    return geom.getFactory().createLineString(
          CoordinateArrays.toCoordinateArray(pts));
  }
 
  static Coordinate[] connectPts(Geometry geom, boolean close)
  {
    CoordinateList pts = new CoordinateList();
    pts.add(geom.getCoordinates(), true);
    if (close) pts.closeRing();
    return CoordinateArrays.toCoordinateArray(pts);
  }
 
  public static Geometry lineConnectNoRepeated(Geometry geom)
  {
    CoordinateList pts = new CoordinateList();
    pts.add(geom.getCoordinates(), false);
    return geom.getFactory().createLineString(
          CoordinateArrays.toCoordinateArray(pts));
  }
 
  //=========== Simplification  ====================
  
  public static Geometry simplifyDP(Geometry g, double tolerance)
  {
    return DouglasPeuckerSimplifier.simplify(g, tolerance);
  }
  
  public static Geometry simplifyDPSafe(Geometry g, double tolerance)
  {
    return TopologyPreservingSimplifier.simplify(g, tolerance);
  }
  
  //===========  Affine transformations  ==============
  
  // MD - caching may not make much difference here, according to testing
  
  public static Geometry translate(Geometry g, double x, double y)
  {
    AffineTransformation trans = AffineTransformation.translationInstance(x, y);
    
    Geometry newG = (Geometry) g.clone();
    newG.apply(trans);
    return newG;
  }

  /**
   * Rotates the geometry around the centre of its envelope.
   * 
   * @param g
   * @param angle
   * @return
   */
  public static Geometry rotate(Geometry g, double angle)
  {
    Coordinate p = g.getEnvelopeInternal().centre();
    return rotate(g, angle, p.x, p.y);
  }
  
  /**
   * Rotates the geometry around the point (x,y)
   * @param g
   * @param angle
   * @param x
   * @param y
   * @return
   */
  public static Geometry rotate(Geometry g, double angle, double x, double y)
  {
    AffineTransformation trans = AffineTransformation.translationInstance(-x, -y);
    trans.rotate(angle);
    trans.translate(x, y);
    
    Geometry newG = (Geometry) g.clone();
    newG.apply(trans);
    return newG;
  }
  
  public static Geometry rotateAt(Geometry g, double angle, Geometry ptGeom)
  {
    Coordinate pt = ptGeom.getCoordinate();
    return rotate(g, angle, pt.x, pt.y);
  }

  /**
   * Reflects the geometry around the line x=y.
   * 
   * @param g
   * @param angle
   * @return
   */
  public static Geometry reflect(Geometry g)
  {
    AffineTransformation trans = AffineTransformation.reflectionInstance(1, 1);
    
    Geometry newG = (Geometry) g.clone();
    newG.apply(trans);
    return newG;
  }

  public static Geometry swapXY(Geometry g)
  {
    Geometry newG = (Geometry) g.clone();
    newG.apply(new SwapXYFilter());
    return newG;
  }

  private static class SwapXYFilter implements CoordinateFilter
  {
    public void filter(Coordinate coord) {
      double tmp = coord.x;
      coord.x = coord.y;
      coord.y = tmp;
    }
  }
  
  public static Geometry nearestPoints(Geometry a, Geometry b)
  {
    Coordinate[] pts = DistanceOp.nearestPoints(a, b);
    return geomFactory.createLineString(
        new Coordinate[] { pts[0], pts[1] });
  }
  
  //===========  Split functions  ==============
  
  public static List<Geometry> splitByMember(Geometry geom) {
    List<Geometry> items = new ArrayList<Geometry>();
    for (int i = 0; i < geom.getNumGeometries(); i++) {
      items.add(geom.getGeometryN(i));
    }
    return items;
  }
  
  public static List<Geometry> splitByVertex(Geometry geom) {
    List<Geometry> items = new ArrayList<Geometry>();
    Coordinate[] pts = geom.getCoordinates();
    for (int i = 0; i < pts.length; i++) {
      Point pt = geomFactory.createPoint(pts[i]);
      items.add(pt);
    }
    return items;
  }
  
  public static List<Geometry> splitBySegment(Geometry geom) {
    List<Geometry> items = new ArrayList<Geometry>();
    List lines = new ArrayList();

    LinearComponentExtracter.getLines(geom, lines);
    for (int icomp = 0; icomp < lines.size(); icomp++) {
      
      Coordinate[] pts = ((LineString) lines.get(icomp)).getCoordinates();
      for (int i = 0; i < pts.length - 1; i++) {
        LineString line = geomFactory.createLineString(
            new Coordinate[] { new Coordinate(pts[i]), new Coordinate(pts[i+1]) });
        items.add(line);
      }
    }
    return items;
  }
  
  //===========  Font functions  ==============
  
  public static Geometry toGeometry(String str)
  {
    return null;
  }
}

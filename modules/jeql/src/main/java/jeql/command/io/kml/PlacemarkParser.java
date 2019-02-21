package jeql.command.io.kml;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jeql.command.io.xml.XMLParseUtil;
import jeql.std.geom.GeomFunction;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class PlacemarkParser {

  private XMLStreamReader xmlRdr;
  
  public PlacemarkParser(XMLStreamReader xmlRdr) {
    this.xmlRdr = xmlRdr;
  }
  
  public Placemark parse(DocumentModel model)
  throws XMLStreamException
  {
    Placemark pm = new Placemark();
    XMLParseUtil.consumeStart(xmlRdr, KMLConstants.PLACEMARK);
    
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, KMLConstants.PLACEMARK)) {
        //System.out.println(pm);
        xmlRdr.next();
        break;
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.NAME)) {
        pm.setName(XMLParseUtil.parseValue(xmlRdr));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.DESCRIPTION)) {
        pm.setDescription(XMLParseUtil.parseValue(xmlRdr));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.STYLEURL)) {
        pm.setStyleUrl(XMLParseUtil.parseValue(xmlRdr));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.EXTENDED_DATA)) {
        parseExtendedData(pm);
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.POINT)) {
        pm.setGeometry(parsePoint(pm));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.LINESTRING)) {
        pm.setGeometry(parseLineString(pm));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.LINEARRING)) {
        pm.setGeometry(parseLinearRing(pm));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.POLYGON)) {
        pm.setGeometry(parsePolygon(pm));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.MULTIGEOMETRY)) {
        pm.setGeometry(parseMultiGeometry(pm));
      }
      else {
        // skip any unrecognized elements
        xmlRdr.next();
      }
    }
    return pm;
  }

  private void parseExtendedData(Placemark pm) throws XMLStreamException {
    XMLParseUtil.consumeStart(xmlRdr, KMLConstants.EXTENDED_DATA);
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, KMLConstants.EXTENDED_DATA)) {
        //System.out.println(pm);
        xmlRdr.next();
        break;
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.DATA)) {
        pm.setData(XMLParseUtil.readElement(xmlRdr));
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.SCHEMA_DATA)) {
        pm.setSchemaData(XMLParseUtil.readElement(xmlRdr));
      }
      else {
        // skip any unrecognized elements
        xmlRdr.next();
      }
    }
  }

  private Polygon parsePolygon(Placemark pm)
  throws XMLStreamException
  {
    XMLParseUtil.consumeStart(xmlRdr, KMLConstants.POLYGON);
    
    LinearRing shell = null;
    List holes = new ArrayList();
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, KMLConstants.POLYGON)) {
        xmlRdr.next();
        break;
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.OUTERBOUNDARYIS)) {
        xmlRdr.next();
        shell = parseLinearRing(pm);
        XMLParseUtil.consumeEnd(xmlRdr, KMLConstants.OUTERBOUNDARYIS);
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.INNERBOUNDARYIS)) {
        xmlRdr.next();
        Geometry hole = parseLinearRing(pm);
        holes.add(hole);
        XMLParseUtil.consumeEnd(xmlRdr, KMLConstants.INNERBOUNDARYIS);
      }
      else {
        xmlRdr.next();
      }
    }
    return GeomFunction.geomFactory.createPolygon(shell, 
        GeomFunction.geomFactory.toLinearRingArray(holes));
    
  }
  private LineString parseLineString(Placemark pm)
  throws XMLStreamException
  {
    XMLParseUtil.consumeStart(xmlRdr, KMLConstants.LINESTRING);

    Coordinate[] pts = null;
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, KMLConstants.LINESTRING)) {
        xmlRdr.next();
        break;
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.COORDINATES)) {
        pts = parseCoordinates(XMLParseUtil.parseValue(xmlRdr), true);
      }
      else {
        xmlRdr.next();
      }
    }
    return GeomFunction.geomFactory.createLineString(pts);
  }
  
  private LinearRing parseLinearRing(Placemark pm)
  throws XMLStreamException
  {
    XMLParseUtil.consumeStart(xmlRdr, KMLConstants.LINEARRING);

    Coordinate[] pts = null;
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, KMLConstants.LINEARRING)) {
        xmlRdr.next();
        break;
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.COORDINATES)) {
        pts = parseCoordinates(XMLParseUtil.parseValue(xmlRdr), true);
      }
      else {
        xmlRdr.next();
      }
    }
    return GeomFunction.geomFactory.createLinearRing(pts);
  }
  
  private Point parsePoint(Placemark pm)
  throws XMLStreamException
  {
    XMLParseUtil.consumeStart(xmlRdr, KMLConstants.POINT);

    Coordinate[] pts = null;
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, KMLConstants.POINT)) {
        xmlRdr.next();
        break;
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.COORDINATES)) {
        pts = parseCoordinates(XMLParseUtil.parseValue(xmlRdr), true);
      }
      else {
        xmlRdr.next();
      }
    }
    return GeomFunction.geomFactory.createPoint(pts[0]);
  }
  
  private static String[] geometryTag = new String[] { KMLConstants.MULTIGEOMETRY,
    KMLConstants.POLYGON,
    KMLConstants.LINEARRING,
    KMLConstants.LINESTRING,
    KMLConstants.POINT
  };
  
  private GeometryCollection parseMultiGeometry(Placemark pm)
  throws XMLStreamException
  {
    XMLParseUtil.consumeStart(xmlRdr, KMLConstants.MULTIGEOMETRY);
    
    List geoms = new ArrayList();
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, KMLConstants.MULTIGEOMETRY)) {
        xmlRdr.next();
        break;
      }
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.MULTIGEOMETRY))
        geoms.add(parseMultiGeometry(pm));
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.POLYGON))
        geoms.add(parsePolygon(pm));
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.LINEARRING))
        geoms.add(parseLinearRing(pm));
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.LINESTRING))
        geoms.add(parseLineString(pm));
      else if (XMLParseUtil.isStartElement(xmlRdr, KMLConstants.POINT))
        geoms.add(parsePoint(pm));
      else {
        xmlRdr.next();
      }
    }
    return GeomFunction.geomFactory.createGeometryCollection(
        GeomFunction.geomFactory.toGeometryArray(geoms));
    
  }
  
  private static Coordinate[] array = new Coordinate[0];

  private Coordinate[] parseCoordinates(String coordStr, boolean closeRing) {
    StreamTokenizer st = new StreamTokenizer(new StringReader(coordStr));
    st.parseNumbers();

    CoordinateList coordinates = new CoordinateList();
    try {
      coordinates.add(parseCoordinate(st));
      while (hasMoreTokens(st)) {
        coordinates.add(parseCoordinate(st));
      }
    } catch (IOException ex) {
      // should never happen - throw illegal state exception
      throw new IllegalStateException(
          "IOException during coordinate string parsing");
    }
    // close ring if required
    if (closeRing) 
      coordinates.closeRing();
    
    return coordinates.toCoordinateArray();
    /*
     * System.out.println(coordStr); // TODO: parse it! return new Coordinate[] {
     * new Coordinate(),new Coordinate(),new Coordinate(),new Coordinate() };
     */
  }
  
  private Coordinate parseCoordinate(StreamTokenizer st)
  throws IOException
  {
    Coordinate coord = new Coordinate();
    coord.x = parseNumber(st);
    parseComma(st);
    coord.y = parseNumber(st);
    if (isCommaNext(st)) {
      parseComma(st);
      coord.z = parseNumber(st);
    }
    return coord;
  }
  
  private double parseNumber(StreamTokenizer st)
  throws IOException
  {
    int type = st.nextToken();
    if (type == StreamTokenizer.TT_NUMBER)
      return st.nval;
    // TODO: throw parse exception here
    return 0.0;
  }
  
  private void parseComma(StreamTokenizer st)
  throws IOException
  {
    int type = st.nextToken();
    if (type == ',')
        return;
    // TODO: throw parse exception here
  }
  
  private boolean isCommaNext(StreamTokenizer st) 
  throws IOException {
    int type = st.nextToken();
    st.pushBack();
    return type == ',';
  }

  private boolean hasMoreTokens(StreamTokenizer st) 
  throws IOException {
    int type = st.nextToken();
    st.pushBack();
    return type != StreamTokenizer.TT_EOF;
  }

  
}

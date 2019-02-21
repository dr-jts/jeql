package jeql.command.io.kml;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Writes a formatted string containing the KML
 * representation of a JTS Geometry.
 * Supports a user-defined line prefix and a user-defined maximum number of coordinates per line.
 * Indents components of Geometries to provide a nicely-formatted representation.
 */
public class KMLGeometryWriter
{
  public static String writeGeometry(Geometry g, double z)
  {
    KMLGeometryWriter writer = new KMLGeometryWriter();
    writer.setZ(z);
    return writer.write(g);
  }
  
  public static String writeGeometry(Geometry g, double z, int precision, boolean extrude, String altitudeMode)
  {
    KMLGeometryWriter writer = new KMLGeometryWriter();
    writer.setZ(z);
    writer.setPrecision(precision);
    writer.setExtrude(extrude);
    writer.setAltitudeMode(altitudeMode);
    return writer.write(g);
  }
  
  /**
   *  Returns a <code>String</code> of repeated characters.
   *
   *@param  ch     the character to repeat
   *@param  count  the number of times to repeat the character
   *@return        a <code>String</code> of characters
   */
  private static String stringOfChar(char ch, int count) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < count; i++) {
      buf.append(ch);
    }
    return buf.toString();
  }

  private final int INDENT_SIZE = 2;
  // these could be make settable
  private static final String coordinateSeparator = ",";
  private static final String tupleSeparator = " ";

  private String linePrefix = null;
  private int maxCoordinatesPerLine = 5;
  private double zVal = Double.NaN;
  private boolean extrude = false;
  private String altitudeMode = null;
  private int precision = -1;
  private DecimalFormat numberFormatter = null;

  public KMLGeometryWriter() {
  }

  public void setLinePrefix(String linePrefix)
  {
    this.linePrefix = linePrefix;
  }

  public void setMaximumCoordinatesPerLine(int maxCoordinatesPerLine)
  {
    if (maxCoordinatesPerLine <= 0) {
      maxCoordinatesPerLine = 1;
      return;
    }
    this.maxCoordinatesPerLine = maxCoordinatesPerLine;
  }

  public void setZ(double zVal)
  {
    this.zVal = zVal;
  }
  
  public void setExtrude(boolean extrude) {
    this.extrude = extrude;
  }
  
  public void setAltitudeMode(String altitudeMode)
  {
    this.altitudeMode = altitudeMode;
  }
  
  public void setPrecision(int precision)
  {
    this.precision = precision;
    if (precision >= 0)
      numberFormatter = createFormatter(precision);
  }
  
  public String write(Geometry geom)
  {
    StringBuffer buf = new StringBuffer();
    write(geom, buf);
    return buf.toString();
  }

  public void write(Geometry geometry, Writer writer)
    throws IOException
  {
    writer.write(write(geometry));
  }



  /**
   * Generates the GML representation of a JTS Geometry.
   * @param g Geometry to output
   */
  public void write(Geometry g, StringBuffer buf)
  {
    writeGeometry(g, 0, buf);
  }

  /**
   * Generates the GML representation of a JTS Geometry.
   * @param g Geometry to output
   */
  private void writeGeometry(Geometry g, int level, StringBuffer buf) {
    /*
     * order is important in this if-else list.
     * E.g. homogeneous collections need to come before GeometryCollection
    */
    String attributes = "";
      if (g instanceof Point) {
          writePoint((Point) g, attributes, level, buf);
      } else if (g instanceof LinearRing) {
          writeLinearRing((LinearRing) g, attributes, level, buf);
      } else if (g instanceof LineString) {
          writeLineString((LineString) g, attributes, level, buf);
      } else if (g instanceof Polygon) {
          writePolygon((Polygon) g, attributes, level, buf);
      } 
      // KML only supports the MultiGeometry element
      /*
      else if (g instanceof MultiPoint) {
          writeMultiPoint((MultiPoint) g, attributes, level, buf);
      } else if (g instanceof MultiLineString) {
          writeMultiLineString((MultiLineString) g, attributes, level, buf);
      } else if (g instanceof MultiPolygon) {
          writeMultiPolygon((MultiPolygon) g, attributes, level, buf);
      }
      */ 
      else if (g instanceof GeometryCollection) {
        writeGeometryCollection((GeometryCollection) g, attributes, level, buf);
      }
      // throw an error for an unknown type?
  }

  private void startLine(StringBuffer buf, int level, String text)
  {
    if (linePrefix != null) buf.append(linePrefix);
    buf.append(stringOfChar(' ', INDENT_SIZE * level));
    buf.append(text);
  }

  private String geometryTag(String geometryName, String attributes)
  {
    StringBuffer buf = new StringBuffer();
    buf.append("<");
    buf.append(geometryName);
    if (attributes != null && attributes.length() > 0) {
      buf.append(" ");
      buf.append(attributes);
    }
    buf.append(">");
    
    // this is cheesy...  AND WRONG! (because these get written in geom sub-components too
    if (extrude) buf.append("\n    <extrude>1</extrude>");
    if (altitudeMode != null) buf.append("\n    <altitudeMode>" + altitudeMode + "</altitudeMode>");
    return buf.toString();
  }

  //<Point><coordinates>1195156.78946687,382069.533723461</coordinates></Point>
  private void writePoint(Point p, String attributes, int level, StringBuffer buf) {
      startLine(buf, level, geometryTag("Point", attributes) + "\n");
      write(new Coordinate[] { p.getCoordinate() }, level + 1, buf);
      startLine(buf, level, "</Point>\n");
  }

  //<LineString><coordinates>1195123.37289257,381985.763974674 1195120.22369473,381964.660533343 1195118.14929823,381942.597718511</coordinates></LineString>
  private void writeLineString(LineString ls, String attributes, int level, StringBuffer buf) {
    startLine(buf, level, geometryTag("LineString", attributes) + "\n");
    write(ls.getCoordinates(), level + 1, buf);
    startLine(buf, level, "</LineString>\n");
  }

  //<LinearRing><coordinates>1226890.26761027,1466433.47430292 1226880.59239079,1466427.03208053...></coordinates></LinearRing>
  private void writeLinearRing(LinearRing lr, String attributes, int level, StringBuffer buf) {
    startLine(buf, level, geometryTag("LinearRing", attributes) + "\n");
    //startLine(buf, level, "  <tessellate>1</tessellate>\n");
    write(lr.getCoordinates(), level + 1, buf);
    startLine(buf, level, "</LinearRing>\n");
  }

  private void writePolygon(Polygon p, String attributes, int level, StringBuffer buf) {
    startLine(buf, level, geometryTag("Polygon", attributes) + "\n");

    startLine(buf, level, "  <outerBoundaryIs>\n");
    writeLinearRing((LinearRing) p.getExteriorRing(), null, level + 1, buf);
    startLine(buf, level, "  </outerBoundaryIs>\n");

    for (int t = 0; t < p.getNumInteriorRing(); t++) {
      startLine(buf, level, "  <innerBoundaryIs>\n");
      writeLinearRing((LinearRing) p.getInteriorRingN(t), null, level + 1, buf);
      startLine(buf, level, "  </innerBoundaryIs>\n");
    }

    startLine(buf, level, "</Polygon>\n");
  }

  /*
  private void writeMultiPoint(MultiPoint mp, String attributes, int level, StringBuffer buf) {
    startLine(buf, level, geometryTag("MultiGeometry", attributes) + "\n");
    for (int t = 0; t < mp.getNumGeometries(); t++) {
      writePoint((Point) mp.getGeometryN(t), null, level + 1, buf);
    }
    startLine(buf, level, "</MultiGeometry>\n");
  }

  private void writeMultiLineString(MultiLineString mls, String attributes, int level, StringBuffer buf) {
    startLine(buf, level, geometryTag("MultiGeometry", attributes) + "\n");
    for (int t = 0; t < mls.getNumGeometries(); t++) {
      writeLineString((LineString) mls.getGeometryN(t), null, level + 1, buf);
    }
    startLine(buf, level, "</MultiGeometry>\n");
  }

  private void writeMultiPolygon(MultiPolygon mp, String attributes, int level, StringBuffer buf) {
    startLine(buf, level, geometryTag("MultiGeometry", attributes) + "\n");
    for (int t = 0; t < mp.getNumGeometries(); t++) {
      writePolygon((Polygon) mp.getGeometryN(t), null, level + 1, buf);
    }
    startLine(buf, level, "</MultiGeometry>\n");
  }
*/
  
  private void writeGeometryCollection(GeometryCollection gc, String attributes, int level, StringBuffer buf) {
    startLine(buf, level, "<MultiGeometry>\n");
    for (int t = 0; t < gc.getNumGeometries(); t++) {
      writeGeometry(gc.getGeometryN(t), level + 1, buf);
    }
    startLine(buf, level, "</MultiGeometry>\n");
  }

  /**
   * Takes a list of coordinates and converts it to GML.<br>
   * 2d and 3d aware.
   * Terminates the coordinate output with a newline.
   *@param cs array of coordinates
   */
  private void write(Coordinate[] coords, int level, StringBuffer buf) {
    startLine(buf, level, "<coordinates>");

    boolean isNewLine = false;
    for (int i = 0; i < coords.length; i++) {
      if (i > 0) {
        buf.append(tupleSeparator);
      }
      
      if (isNewLine) {
        startLine(buf, level, "  ");
        isNewLine = false;
      }

      write(coords[i], buf);

      // break output lines to prevent them from getting too long
      if ((i + 1) % maxCoordinatesPerLine == 0 && i < coords.length - 1) {
        buf.append("\n");
        isNewLine = true;
      }
    }
    buf.append("</coordinates>\n");
  }
  
  private void write(Coordinate p, StringBuffer buf)
  {
    write(p.x, buf);
    buf.append(coordinateSeparator);
    write(p.y, buf);
      
    double z = p.z;
    // if altitude was specified directly, use it
    if (! Double.isNaN(zVal))
      z = zVal;
      
    // only write if Z present
    // MD - is this right?  Or should it always be written?
    if (! Double.isNaN(z)) {
      buf.append(coordinateSeparator);
      write(z, buf);
    }
  }
  
  private void write(double num, StringBuffer buf)
  {
    if (numberFormatter != null) 
      buf.append(numberFormatter.format(num));
    else
      buf.append(num);
  }
  
  /**
   *  Creates the <code>DecimalFormat</code> used to write <code>double</code>s
   *  with a sufficient number of decimal places.
   *
   *@param  precisionModel  the <code>PrecisionModel</code> used to determine
   *      the number of decimal places to write.
   *@return                 a <code>DecimalFormat</code> that write <code>double</code>
   *      s without scientific notation.
   */
  private static DecimalFormat createFormatter(int precision) {
    // specify decimal separator explicitly to avoid problems in other locales
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    DecimalFormat format = new DecimalFormat("0." + stringOfChar('#', precision), symbols);
    format.setDecimalSeparatorAlwaysShown(false);
    return format;
  }

}


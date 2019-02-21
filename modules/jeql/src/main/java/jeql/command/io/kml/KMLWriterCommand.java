package jeql.command.io.kml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jeql.JeqlVersion;
import jeql.api.command.Command;
import jeql.api.error.ExecutionException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.row.RowUtil;
import jeql.api.table.Table;
import jeql.engine.EngineContext;
import jeql.engine.Scope;
import jeql.util.StringUtil;

import org.locationtech.jts.geom.Geometry;

public class KMLWriterCommand implements Command
{
  private static final String DEFAULT_ICON_HREF = "http://maps.google.com/mapfiles/kml/shapes/donut.png";

  private String filename = null;
  private List<Table> dataTables = new ArrayList<Table>();
  private PrintWriter writer = null;
  private RowSchema schema;
  private Table styleTbl = null;
  private String docComment = null;
  private String docName = null;
  private String docDescription = null;
  private boolean addLabelPoint = false;
  private int precision = -1;
  private boolean hasStyleColumns;

  // Geometry attributes
  private boolean defaultExtrude = false;
  private double defaultAltitude = Double.NaN;
  private String defaultAltitudeMode = null;

  private KMLStyle defaultStyle = new KMLStyle(KMLCol.DEFAULT_STYLE_ID, "ffffffff");

  private int nameIndex = -1;

  private int descriptionIndex = -1;
  private int styleUrlIndex = -1;
  private int timeSpanBeginIndex = -1;
  private int timeSpanEndIndex = -1;
  private int timeStampWhenIndex = -1;
  private StyleMapWriter styleMapWriter = null;
  private FolderWriter folderWriter = null;

  // this will be non-null if a KMZ file was written
  // it needs to be closed after writing
  private ZipOutputStream zipos = null;

  public KMLWriterCommand()
  {
    defaultStyle = createDefaultStyle();
  }

  private static KMLStyle createDefaultStyle()
  {
    KMLStyle style = new KMLStyle(KMLCol.DEFAULT_STYLE_ID, "ffffffff", 1, "a0a0a0a0");
    style.setPolyColorMode(KMLCol.KML_RANDOM);
    style.setIconHref(DEFAULT_ICON_HREF);
    style.setIconColor("00ff00");
    style.setLabelColor("ffffff");
    return style;
  }

  public void setFile(String filename)
  {
    this.filename = filename;
  }

  public void setDefault(Table tbl)
  {
    dataTables.add(tbl);
  }

  public void setData(Table tbl)
  {
    dataTables.add(tbl);
  }

  public void setStyles(Table styles)
  {
    this.styleTbl = styles;
  }

  public void setComment(String comment)
  {
    docComment = comment;
  }

  public void setName(String name)
  {
    docName = name;
  }

  public void setDescription(String desc)
  {
    docDescription = desc;
  }

  public void setStyleId(String id)
  {
    defaultStyle.setId(id);
  }

  public void setPrecision(int precision)
  {
    this.precision = precision;
  }

  public void setLineStyleColor(String color)
  {
    defaultStyle.setLineColor(color);
  }

  public void setLineStyleColorMode(String mode)
  {
    defaultStyle.setLineColorMode(mode);
  }

  public void setLineStyleWidth(int width)
  {
    defaultStyle.setLineWidth(width);
  }

  public void setPolyStyleColor(String color)
  {
    defaultStyle.setPolyColor(color);
  }

  public void setPolyStyleColorMode(String mode)
  {
    defaultStyle.setPolyColorMode(mode);
  }

  public void setPolyStyleFill(int fill)
  {
    defaultStyle.setPolyFill(fill);
  }

  public void setPolyStyleOutline(int outline)
  {
    defaultStyle.setPolyOutline(outline);
  }

  public void setLabelStyleColor(String color)
  {
    defaultStyle.setLabelColor(color);
  }

  public void setLabelStyleColorMode(String mode)
  {
    defaultStyle.setLabelColor(mode);
  }

  public void setLabelStyleScale(double scale)
  {
    defaultStyle.setLabelScale(scale);
  }

  public void setIconStyleColor(String color)
  {
    defaultStyle.setIconColor(color);
  }

  public void setIconStyleColorMode(String mode)
  {
    defaultStyle.setIconColorMode(mode);
  }

  public void setIconStyleScale(double scale)
  {
    defaultStyle.setIconScale(scale);
  }

  public void setIconStyleHref(String href)
  {
    defaultStyle.setIconHref(href);
  }

  public void setExtrude(int extrude)
  {
    defaultExtrude = extrude == 1;
  }

  public void setAltitude(double altitude)
  {
    defaultAltitude = altitude;
  }

  public void setAltitudeMode(String altitudeMode)
  {
    defaultAltitudeMode = altitudeMode;
  }

  /**
   * Sets whether to add a label point to non-point features.
   * 
   * @param addLabelPoint
   */
  public void setAddLabelPoint(boolean addLabelPoint)
  {
    this.addLabelPoint = addLabelPoint;
  }

  protected PrintWriter getKmlWriter(String filename) throws Exception
  {
    if (StringUtil.endsWithIgnoreCase(filename, ".kmz")) {
      return getKmzWriter(filename);
    }
    return getWriter(filename);
  }

  private static PrintWriter getWriter(String filename) throws IOException
  {
    if (filename != null) {
      return new PrintWriter(new FileWriter(new File(filename)));
    }
    return EngineContext.OUTPUT_WRITER;
  }

  private static final String KMZ_CONTENT_FILE = "doc.kml";

  private static PrintWriter getKmzWriter(String filename) throws Exception
  {
    ZipOutputStream zipos = new ZipOutputStream(new FileOutputStream(new File(filename)));
    zipos.putNextEntry(new ZipEntry(KMZ_CONTENT_FILE));
    return new PrintWriter(zipos);
  }

  public void execute(Scope scope) throws Exception
  {
    write();
  }

  private void write() throws Exception
  {
    // writer = getWriter(); // OLD
    writer = getKmlWriter(filename);

    try {
      writePrologue();

      styleMapWriter = new StyleMapWriter(styleTbl);
      // styles & styleMaps
      styleMapWriter.write(writer);
      if (defaultStyle != null)
        defaultStyle.write(writer);
      if (styleTbl != null)
        writeStyles(styleTbl);

      for (Table tbl : dataTables) {
        write(tbl);
      }
      writeEpilogue();
    }
    finally {
      writer.close();
    }
  }

  private void write(Table tbl) throws Exception
  {
    RowIterator ri = tbl.getRows().iterator();
    schema = ri.getSchema();
    folderWriter = new FolderWriter(schema);
    hasStyleColumns = KMLStyle.hasStyleColumns(schema);

    nameIndex = schema.getColIndexIgnoreCase(KMLCol.KML_NAME);
    descriptionIndex = schema.getColIndexIgnoreCase(KMLCol.KML_DESCRIPTION);
    styleUrlIndex = schema.getColIndexIgnoreCase(KMLCol.KML_STYLEURL);
    timeSpanBeginIndex = schema.getColIndexIgnoreCase(KMLCol.KML_TIMESPAN_BEGIN);
    timeSpanEndIndex = schema.getColIndexIgnoreCase(KMLCol.KML_TIMESPAN_END);
    timeStampWhenIndex = schema.getColIndexIgnoreCase(KMLCol.KML_TIMESTAMP_WHEN);

    write(ri);
  }

  private void write(RowIterator ri) throws Exception
  {
    // placemarks (also folders if specified)
    while (true) {
      Row row = ri.next();
      if (row == null) break;
      writePlacemark(row);
    }
    folderWriter.finishedRows(writer);
  }

  private void writeStyles(Table styleTbl) throws IOException
  {
    RowIterator it = styleTbl.getRows().iterator();
    RowSchema schema = it.getSchema();
    while (true) {
      Row row = it.next();
      if (row == null)
        break;
      KMLStyle style = new KMLStyle(schema, row, true);
      style.write(writer);
    }
  }

  private void writePlacemark(Row row) throws IOException
  {
    folderWriter.nextRow(row, writer);

    writer.println();
    writer.println("<Placemark>");

    XMLWriter.writeElement(writer, 2, "name", nameIndex, row, null, true);
    XMLWriter.writeElement(writer, 2, "description", descriptionIndex, row, null, true);
    writeStyleUrl(writer, 2, row);
    if (hasStyleColumns) {
      KMLStyle style = new KMLStyle(schema, row, true);
      style.write(writer);
    }

    writeTime(writer, 2, row);

    // Geometry attributes
    double altitude = RowUtil.getDouble(schema, KMLCol.KML_ALTITUDE, row,
        defaultAltitude);
    boolean extrude = KMLUtil.toBooleanLoose(schema, KMLCol.KML_EXTRUDE, row, defaultExtrude);
    String altitudeMode = RowUtil.getString(schema, KMLCol.KML_ALTITUDE_MODE, row,
        defaultAltitudeMode);
    writeGeometry(KMLUtil.getGeometry(schema, row), altitude, extrude, altitudeMode);

    writer.println("</Placemark>");
  }

  private static String formatStyleUrl(String url)
  {
    // if link assume correct!
    if (url.startsWith("http:"))
      return url;
    // add a # if not present
    if (url.indexOf("#") < 0)
      return "#" + url;
    return url;
  }

  private void writeStyleUrl(PrintWriter writer, int indent, Row row)
  {
    String indentStr = XMLWriter.indentStr(indent);
    String styleName = "#" + defaultStyle.getId();
    if (styleUrlIndex >= 0) {
      styleName = formatStyleUrl(row.getValue(styleUrlIndex).toString());
    }

    writer.println(indentStr + "<styleUrl>" + styleName + "</styleUrl>");
  }

  private void writeTime(PrintWriter writer, int indent, Row row)
  {
    String indentStr = XMLWriter.indentStr(indent);
    if (timeStampWhenIndex >= 0) {
      writer.println(indentStr + "<TimeStamp>");
      writer.println(indentStr + "  <when>" + KMLUtil.formatDate(row.getValue(timeStampWhenIndex))
          + "</when>");
      writer.println(indentStr + "</TimeStamp>");
      return;
    }
    if (timeSpanBeginIndex >= 0 || timeSpanEndIndex >= 0) {
      writer.println(indentStr + "<TimeSpan>");
      if (timeSpanBeginIndex >= 0) {
        writer.println(indentStr + "  <begin>" + KMLUtil.formatDate(row.getValue(timeSpanBeginIndex))
            + "</begin>");
      }
      if (timeSpanEndIndex >= 0) {
        writer.println(indentStr + "  <end>" + KMLUtil.formatDate(row.getValue(timeSpanEndIndex))
            + "</end>");
      }
      writer.println(indentStr + "</TimeSpan>");
    }
  }

  private void writePrologue() throws IOException
  {
    writer.println("<?xml version='1.0' encoding='UTF-8'?>");
    writer.println("<!-- Generated by Jeql (ver. " + JeqlVersion.VERSION + ")" + "    "
        + (new Date()) + " -->");
    if (docComment != null) {
      writer.println();
      writer.println("<!-- " + docComment + " -->");
    }
    writer.println();
    writer.println("<kml xmlns='http://earth.google.com/kml/2.1'>");
    writer.println("<Document>");
    if (docName != null)
      writer.println("<name>" + docName + "</name>");
    if (docDescription != null)
      writer.println("<description>" + docDescription + "</description>");
    writer.println("");
  }

  private void writeEpilogue() throws IOException
  {
    writer.println("</Document>");
    writer.println("</kml>");
  }

  private void writeGeometry(Geometry g, double altitude, boolean extrude, String altitudeMode)
      throws IOException
  {
    Geometry gout = g;
    // TODO: check if geometry already contains a point
    if (addLabelPoint && g.getDimension() > 0) {
      gout = KMLUtil.geomWithLabelPoint(g);
    }
    writer.print(KMLGeometryWriter.writeGeometry(gout, altitude, precision, extrude, altitudeMode));
  }

}

package jeql.command.io.kml;

import java.io.IOException;
import java.io.PrintWriter;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.api.row.RowUtil;
import jeql.std.function.ColorFunction;

class KMLStyle
{
  public static boolean hasStyleColumns(RowSchema schema)
  {
    return schema.hasCol(KMLCol.STYLE_LINECOLOR)
    || schema.hasCol(KMLCol.STYLE_LINECOLOR)
    || schema.hasCol(KMLCol.STYLE_LINECOLORMODE)
    || schema.hasCol(KMLCol.STYLE_LINEWIDTH)
    || schema.hasCol(KMLCol.STYLE_POLYCOLOR)
    || schema.hasCol(KMLCol.STYLE_POLYCOLORMODE)
    || schema.hasCol(KMLCol.STYLE_POLYFILL)
    || schema.hasCol(KMLCol.STYLE_POLYOUTLINE)
    || schema.hasCol(KMLCol.STYLE_LABELCOLOR)
    || schema.hasCol(KMLCol.STYLE_LABELCOLORMODE)
    || schema.hasCol(KMLCol.STYLE_LABELSCALE)
    || schema.hasCol(KMLCol.STYLE_ICOMCOLOR)
    || schema.hasCol(KMLCol.STYLE_ICONCOLORMODE)
    || schema.hasCol(KMLCol.STYLE_ICONSCALE)
    || schema.hasCol(KMLCol.STYLE_ICONHREF)
    || schema.hasCol(KMLCol.STYLE_BALLOONBGCOLOR)
    || schema.hasCol(KMLCol.STYLE_BALLOONTEXTCOLOR)
    || schema.hasCol(KMLCol.STYLE_BALLOONTEXT)
    || schema.hasCol(KMLCol.STYLE_BALLOONDISPLAY)
    ;
  }
  
  //private boolean showLabel = false;
  
  private String id = null;
  private String lineColor = null;
  private String lineColorMode = null;
  private int lineWidth = -1;
  private String polyColor = null;
  private String polyColorMode = null;
  private int polyFill = 1;
  private int polyOutline = 1;
  private String labelColor = null;
  private String labelColorMode = null;
  // init negative to indicate not set
  private double labelScale = -1.0;
  private String iconColor = null;
  private String iconColorMode = null;
  // init negative to indicate not set
  private double iconScale = -1.0;
  private String iconHref = null;
  private String balloonBgColor = null;
  private String balloonTextColor = null;
  private String balloonText = null;
  private String balloonDisplay = null;
  
  public KMLStyle()
  {
    
  }
  
  public KMLStyle(String id, String lineColor)
  {
    this(id, lineColor, -1, null);
  }
  
  public KMLStyle(RowSchema schema, Row row, boolean useId)
  {
    init(schema, row, useId);
  }
  
  public KMLStyle(String id, String lineColor, int lineWidth, String polyColor)
  {
    this.id = id;
    this.lineColor = lineColor;
    this.lineWidth = lineWidth;
    this.polyColor = polyColor;
  }
  
  private void init()
  {
    if (id == null) id = KMLCol.DEFAULT_STYLE_ID;    
  }
  public String getId() { return id; }
  
  public void setId(String id) 
  { 
    this.id = id; 
  }
  public void setLineColor(String color) 
  { 
    init();
    this.lineColor = color; 
  }
  public void setLineColorMode(String lineColorMode) 
  { 
    init();
    this.lineColorMode = lineColorMode; 
  }
  public void setLineWidth(int width) 
  { 
    init();
    this.lineWidth = width; 
  }
  public void setPolyColor(String color) 
  { 
    init();
    this.polyColor = color; 
  }
  public void setPolyColorMode(String polyColorMode) 
  { 
    init();
    this.polyColorMode = polyColorMode; 
  }
  public void setPolyFill(int fill) 
  { 
    init();
    this.polyFill = fill == 0 ? 0 : 1; 
  }
  public void setPolyOutline(int outline) 
  { 
    init();
    this.polyOutline = outline == 0 ? 0 : 1; 
  }
  public void setLabelColor(String color) 
  { 
    init();
    this.labelColor = color; 
  }
  public void setLabelColorMode(String mode) 
  { 
    init();
    this.labelColorMode = mode; 
  }
  public void setLabelScale(double scale) 
  { 
    init();
    this.labelScale = scale; 
  }
  public void setIconColor(String color) 
  { 
    init();
    this.iconColor = color; 
  }
  public void setIconColorMode(String mode) 
  { 
    init();
    this.iconColorMode = mode; 
  }
  public void setIconScale(double scale) 
  { 
    init();
    this.iconScale = scale; 
  }
  public void setIconHref(String href) 
  { 
    init();
    this.iconHref = href; 
  }
  
  public void init(RowSchema schema, Row row, boolean useId)
  {
    if (useId)
      id = RowUtil.getString(schema, KMLCol.KML_ID, row);
    lineColor = RowUtil.getString(schema, "lineStyleColor", row);
    lineColorMode = RowUtil.getString(schema, "lineStyleColorMode", row);
    lineWidth = RowUtil.getInt(schema, "lineStyleWidth", row, -1);
    polyColor = RowUtil.getString(schema, "polyStyleColor", row);
    polyColorMode = RowUtil.getString(schema, "polyStyleColorMode", row);
    polyFill = RowUtil.getInt(schema, "polyStyleFill", row, -1);
    polyOutline = RowUtil.getInt(schema, "polyStyleOutline", row, -1);
    labelColor = RowUtil.getString(schema, KMLCol.STYLE_LABELCOLOR, row);
    labelColorMode = RowUtil.getString(schema, KMLCol.STYLE_LABELCOLORMODE, row);
    labelScale = RowUtil.getDouble(schema, KMLCol.STYLE_LABELSCALE, row, -1.0);
    
    iconColor = RowUtil.getString(schema, "iconStyleColor", row);
    iconColorMode = RowUtil.getString(schema, "iconStyleColorMode", row);
    iconScale = RowUtil.getDouble(schema, "iconStyleScale", row, -1.0);
    iconHref = RowUtil.getString(schema, "iconStyleHref", row);
    
    balloonBgColor = RowUtil.getString(schema, KMLCol.STYLE_BALLOONBGCOLOR, row);
    balloonTextColor = RowUtil.getString(schema, KMLCol.STYLE_BALLOONTEXTCOLOR, row);
    balloonText = RowUtil.getString(schema, KMLCol.STYLE_BALLOONTEXT, row);
    balloonDisplay = RowUtil.getString(schema, KMLCol.STYLE_BALLOONDISPLAY, row);
    
    /*
    showLabel = schema.hasColIgnoreCase(KMLConstants.STYLE_LABELCOLOR) 
      || schema.hasColIgnoreCase(KMLConstants.STYLE_LABELCOLORMODE)
        || schema.hasColIgnoreCase(KMLConstants.STYLE_LABELSCALE);
        */
  }
  
  private static boolean hasData(String s)
  {
    return s != null && s.length() > 0;
  }
  
  public void write(PrintWriter writer) throws IOException {
    if (id != null && id.length() <= 0)
      return;
    
    if (id != null)
      writer.println("<Style id='" + id + "'>");
    else 
      writer.println("<Style>");
    
    if (hasData(lineColor)
        || hasData(lineColorMode)
        || lineWidth >= 0) {
      writer.println("  <LineStyle>");
      XMLWriter.writeElementNonEmpty(writer, 4, "color", RGBAtoABGR(lineColor));
      XMLWriter.writeElementNonEmpty(writer, 4, "colorMode", lineColorMode);
      if (lineWidth >= 0)
        writer.println("    <width>" + lineWidth + "</width>");
      writer.println("  </LineStyle>");
    }
    
    if (hasData(polyColor)
        || hasData(polyColorMode)
        || polyFill >= 0
        || polyOutline >= 0) {
      writer.println("  <PolyStyle>");
      XMLWriter.writeElementNonEmpty(writer, 4, "color", RGBAtoABGR(polyColor));
      XMLWriter.writeElementNonEmpty(writer, 4, "colorMode", polyColorMode);
      if (polyFill >= 0)
        writer.println("    <fill>" + polyFill + "</fill>");
      if (polyOutline >= 0)
        writer.println("    <outline>" + polyOutline + "</outline>");
      writer.println("  </PolyStyle>");
    }
    
    if (hasData(labelColor)
        || hasData(labelColorMode)
        || labelScale >= 0) {
      writer.println("  <LabelStyle>");
      XMLWriter.writeElementNonEmpty(writer, 4, "color", RGBAtoABGR(labelColor));
      XMLWriter.writeElementNonEmpty(writer, 4, "colorMode", labelColorMode);
      writer.println("    <scale>" + Math.abs(labelScale) + "</scale>");
      writer.println("  </LabelStyle>");
    }
    
    if ( hasData(iconColor)
            || hasData(iconColorMode)
            || iconHref != null
            || iconScale >= 0) {
          writer.println("  <IconStyle>");
          XMLWriter.writeElementNonEmpty(writer, 4, "color", RGBAtoABGR(iconColor));
          XMLWriter.writeElementNonEmpty(writer, 4, "colorMode", iconColorMode);
          if (iconScale >= 0)
            writer.println("    <scale>" + Math.abs(iconScale) + "</scale>");
          if (hasData(iconHref)) {
            writer.println("    <Icon>");
            writer.println("      <href>" + iconHref + "</href>");
            writer.println("    </Icon>");
          }
          writer.println("  </IconStyle>");
        }
        
    if ( hasData(balloonBgColor)
            || hasData(balloonTextColor)
            || hasData(balloonText)
            || hasData(balloonDisplay)
            ) {
          writer.println("  <BalloonStyle>");
          XMLWriter.writeElementNonEmpty(writer, 4, "bgColor", RGBAtoABGR(balloonBgColor));
          XMLWriter.writeElementNonEmpty(writer, 4, "textColor", RGBAtoABGR(balloonTextColor));
          XMLWriter.writeElement(writer, 4, "text", balloonText, true);
          XMLWriter.writeElementNonEmpty(writer, 4, "displayMode", balloonDisplay);
          writer.println("  </BalloonStyle>");
        }
        
    writer.println("</Style>");  
    writer.println();
  }
  
  private static String RGBAtoABGR(String clr)
  {
    if (clr == null) return null;
    return ColorFunction.RGBAtoABGR(clr);
  }
}

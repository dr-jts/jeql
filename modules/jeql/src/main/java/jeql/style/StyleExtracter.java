package jeql.style;

import java.awt.Color;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.api.row.RowUtil;
import jeql.api.row.SchemaUtil;
import jeql.util.ColorUtil;
import jeql.util.TypeUtil;

public class StyleExtracter
{
  public static final String DEFAULT_FILL = "5555ff80";
  public static final String DEFAULT_STROKE = "0000ffff";
  public static final String DEFAULT_LABEL_CLR = "000000"; //"80ff80";
  public static final int DEFAULT_LABEL_SIZE = 12;

  private int strokeIndex;
  private int strokeWidthIndex;
  private int fillIndex;
  private int labelIndex = -1;
  private int labelColorIndex = -1;
  private int labelSizeIndex = -1;
  private int labelHaloRadiusIndex = -1;
  private int labelHaloColorIndex = -1;
  private int labelRotateIndex = -1;
  private int labelOffsetXIndex = -1;
  private int labelOffsetYIndex = -1;
  private int labelPointIndex;
  
  private String defaultFill = null;
  private String defaultStroke = null;
  

  public StyleExtracter(RowSchema schema)
  {
    strokeIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.STROKE, StyleConstants.COLOR);
    strokeWidthIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.STROKE_WIDTH);
    fillIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.FILL);
    
    labelIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL);
    labelColorIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_COLOR);
    labelSizeIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.FONT_SIZE);
    labelHaloRadiusIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.HALO_RADIUS);
    labelHaloColorIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.HALO_COLOR);
    labelOffsetXIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_OFFSET_X);
    labelOffsetYIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_OFFSET_Y);
    labelRotateIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_ROTATE);
    labelPointIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_POINT);

    if (! hasColor()) {
      defaultFill = DEFAULT_FILL;
      defaultStroke = DEFAULT_STROKE;
    }
  }

  public boolean hasColor()
  {
    return strokeIndex >= 0 || fillIndex >= 0;
  }
  
  public Color fill(Row row)
  {
    String fillClrStr = RowUtil.getString(fillIndex, row, defaultFill);    
    Color clr = ColorUtil.RGBAtoColor(fillClrStr);
    return clr;
  }

  public boolean hasStrokeWidth()
  {
    return strokeWidthIndex >= 0;
  }
  
  public double strokeWidth(Row row)
  {
    if (strokeWidthIndex >= 0)
      return RowUtil.getDouble(strokeWidthIndex, row, 1.0);
    return 1.0;
  }
  
  public Color stroke(Row row) {
    String lineClrStr = RowUtil.getString(strokeIndex, row, defaultStroke);
    Color clr = ColorUtil.RGBAtoColor(lineClrStr);
    return clr;
  }
  
  public boolean hasLabel() {
    return labelIndex >= 0;
  }

  public String label(Row row) {
    if (labelIndex < 0)
      return null;
    return row.getValue(labelIndex).toString();
  }
  
  public Color labelColor(Row row) {
    String labelClrStr = DEFAULT_LABEL_CLR;
    if (labelColorIndex >= 0)
      labelClrStr = (String) row.getValue(labelColorIndex);
    return ColorUtil.RGBAtoColor(labelClrStr);
  }
  
  public int labelSize(Row row) {
    return RowUtil.getInt(labelSizeIndex, row, DEFAULT_LABEL_SIZE);
  }
  
  public Color labelHaloColor(Row row) {
    if (isNoHalo()) return null;
    String clr = RowUtil.getString(labelHaloColorIndex, row, StyleConstants.DEFAULT_HALO_CLR);
    return ColorUtil.RGBAtoColor(clr);
  }
  
  private boolean isNoHalo() {
    return labelHaloColorIndex < 0 && labelHaloRadiusIndex < 0;
  }

  public int labelHaloRadius(Row row) {
    if (isNoHalo()) return 0;
    return RowUtil.getInt(labelHaloRadiusIndex, row, StyleConstants.DEFAULT_HALO_RADIUS);
  }
}

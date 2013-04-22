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
  private static final String DEFAULT_FILL = "5555ff80";
  private static final String DEFAULT_STROKE = "0000ffff";

  private int strokeIndex;
  private int strokeWidthIndex;
  private int fillIndex;
  private String defaultFill = null;
  private String defaultStroke;
  

  public StyleExtracter(RowSchema schema)
  {
    strokeIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.STROKE, StyleConstants.COLOR);
    strokeWidthIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.STROKE_WIDTH);
    fillIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.FILL);
    if (strokeIndex < 0 && fillIndex < 0) {
      defaultFill = DEFAULT_FILL;
      defaultStroke = DEFAULT_STROKE;
    }
  }

  public Color fill(Row row)
  {
    String fillClrStr = RowUtil.getString(fillIndex, row, defaultFill);    
    Color clr = ColorUtil.RGBAtoColor(fillClrStr);
    return clr;
  }

  public double strokeWidth(Row row)
  {
    if (strokeWidthIndex >= 0)
      return TypeUtil.toDouble(row.getValue(strokeWidthIndex));
    return 1.0;
  }
  
  public Color stroke(Row row)
  {
    String lineClrStr = RowUtil.getString(strokeIndex, row, defaultStroke);
    Color clr = ColorUtil.RGBAtoColor(lineClrStr);
    return clr;
  }
}

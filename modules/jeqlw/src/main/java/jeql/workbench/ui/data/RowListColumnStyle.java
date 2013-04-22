package jeql.workbench.ui.data;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.row.RowSchema;

public class RowListColumnStyle
{
  public static String columnKey(RowSchema schema)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < schema.size(); i++) {
      buf.append(schema.getName(i));
      buf.append("#");
    }
    return buf.toString();
  }

  public static final int BASIC_COL_WIDTH = 50;
  
  public static int defaultColumnWidth(Class clz)
  {
    if (clz == Geometry.class) {
      return 200;
    }
    if (clz == String.class) {
      return 75;
    }
    return BASIC_COL_WIDTH;
  }
  
  private RowSchema schema;
  private String key;
  private int[] colWidth;
  
  public RowListColumnStyle(String key, RowSchema schema)
  {
    this.key = key;
    colWidth = new int[schema.size()];
    for (int i = 0; i < colWidth.length; i++) {
      colWidth[i] = defaultColumnWidth(schema.getType(i));
    }
  }
  
  public String getKey() { return key; }
  
  public int width(int i)
  {
    return colWidth[i];
  }
  
  public void setWidth(int i, int width)
  {
    colWidth[i] = width;
  }
}

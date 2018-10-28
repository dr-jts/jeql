package jeql.workbench;

import java.awt.Color;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.api.row.SchemaUtil;
import jeql.monitor.MonitorRowList;
import jeql.style.StyleExtracter;
import jeql.util.ColorUtil;
import jeql.workbench.ui.geomview.GeometryList;
import jeql.workbench.ui.geomview.Label;
import jeql.workbench.ui.geomview.style.BasicStyle;
import jeql.workbench.ui.geomview.style.Style;


public class RowListGeometryList implements GeometryList

{
  private List rows;
  private RowSchema schema;
  private int geomCol = -1;
  private StyleExtracter styler;
  
  public RowListGeometryList(MonitorRowList rowList)
  {
    setRowList(rowList);
  }

  public void setRowList(MonitorRowList rowList)
  {
    this.rows = rowList.getRows();
    schema = rowList.getSchema();
    geomCol = SchemaUtil.getColumnWithType(schema, Geometry.class);
    styler = new StyleExtracter(schema);
  }
  
  public int size()
  {
    return rows.size();
  }
  
  public Geometry getGeometry(int i)
  {
    if (rows.size() <= 0 || geomCol < 0) return null;
    // TODO Auto-generated method stub
    Row row = (Row) rows.get(i);
    return (Geometry) row.getValue(geomCol);
  }

  public Style getStyle(int i)
  {
    Row row = (Row) rows.get(i);
    Color fill = styler.fill(row);
    if (fill == null)
      fill = ColorUtil.lighter(Color.BLUE);
    Color stroke = styler.stroke(row);
    if (stroke == null)
      stroke = ColorUtil.lighter(Color.BLUE);
    return new BasicStyle(stroke, fill, (float) styler.strokeWidth(row));
  }

  @Override
  public Label getLabel(int i) {
    Row row = (Row) rows.get(i);
    return new Label(styler.label(row), 
        styler.labelColor(row),
        styler.labelSize(row));
  }

}

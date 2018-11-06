package jeql.workbench.ui.geomview;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.api.row.SchemaUtil;
import jeql.workbench.ui.geomview.tool.Tool;
import jeql.workbench.ui.geomview.tool.ZoomTool;

public class GeometryViewController {
  private GeometryViewFrame frame;
  private GeometryViewPanel view;

  public GeometryViewController(GeometryViewFrame frame) {
    this.frame = frame;
    view = frame.geomView();
    view.setCurrentTool(ZoomTool.getInstance());
  }
  
  public void setVisible(boolean isVisible) {
    frame.setVisible(true);
  }
  
  public void setSource(LayerList lyrList) {
    view.setSource(lyrList);
  }
  
  public void setCurrentTool(Tool tool) {
    view.setCurrentTool(tool);
  }
  public void saveImageToClipboard() {
    frame.saveImageToClipboard();
  }
  
  public void zoom(int i) {
    view.zoomToGeometry(i);
  }
  
  public void zoom(Row row, RowSchema schema) {
    int geomCol = SchemaUtil.getColumnWithType(schema, Geometry.class);
    if (geomCol < 0) return;
    Geometry geom = (Geometry) row.getValue(geomCol);
    inspect(geom);
  }
  
  public void inspect(Object val)
  {
    if (val instanceof Geometry) {
      Geometry geom = (Geometry) val;
      view.inspect(geom);
    }
  }
  
  public void flash(Object val)
  {
    if (val instanceof Geometry) {
      Geometry geom = (Geometry) val;
      view.flash(geom);
    }
  }

  public GeometryViewPanel panel() {
    return view;
  }

  public void saveAsPNG() {
    frame.saveAsPNG();
  }

  public void zoomToFullExtent() {
    view.zoomToFullExtent();
  }
}

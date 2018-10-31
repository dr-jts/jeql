package jeql.workbench;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.row.Row;
import jeql.workbench.ui.geomview.GeometryViewPanel;
import jeql.workbench.ui.geomview.LayerList;

public class GeometryViewController {
  private GeometryViewFrame frame;
  private GeometryViewPanel view;

  public GeometryViewController(GeometryViewFrame frame) {
    this.frame = frame;
    view = frame.geomView();
  }
  
  public void setVisible(boolean isVisible) {
    frame.setVisible(true);
  }
  public void setSource(LayerList lyrList) {
    view.setSource(lyrList);
  }
  
  public void saveImageToClipboard() {
    frame.saveImageToClipboard();
  }
  
  public void zoomToRow(Row row) {
    
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
}

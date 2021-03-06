package jeql.workbench.ui.geomview;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.api.row.SchemaUtil;
import jeql.std.geom.GeomFunction;
import jeql.util.SwingUtil;
import jeql.workbench.RowListGeometryList;
import jeql.workbench.Workbench;
import jeql.workbench.ui.geomview.tool.Tool;
import jeql.workbench.ui.geomview.tool.ZoomTool;

public class GeometryViewController {
  private GeometryViewFrame frame;
  private GeometryViewPanel view;
  private LayerList lyrList;

  public GeometryViewController(GeometryViewFrame frame) {
    this.frame = frame;
    view = frame.geomView();
    view.setCurrentTool(ZoomTool.getInstance());
  }
  
  public void setVisible(boolean isVisible) {
    frame.setVisible(true);
  }
  
  public void setSource(LayerList lyrList) {
    this.lyrList = lyrList;
    frame.setTitleName(lyrList.getName());
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
  
  public void zoom(Geometry geom)
  {
     view.zoom(geom);
  }

  public void zoomObject(Object val)
  {
    if (val instanceof Geometry) {
      Geometry geom = (Geometry) val;
      view.zoom(geom);
    }
  }
  
  public void flash(Object val)
  {
    if (val instanceof Geometry) {
      Geometry geom = (Geometry) val;
      view.flash(geom);
    }
  }

  public void select(Geometry geom) {
    view.setSelection(geom);
  }
  
  public String getToolTip(Coordinate pt) {
    return lyrList.locateRowDesc(pt);
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

  public void inspect(Coordinate pt) {
    RowWithSchema row = lyrList.locateRow(pt);
    if (row == null) {
      Workbench.controller().selectDataRow(null);
      return;
    }
    Workbench.controller().inspect(row.schema(), row.row());
    Workbench.controller().selectDataRow(row.row());

  }

  public void pasteSelectionFromWKT() {
    Object obj = SwingUtil.getFromClipboard();
    Geometry g = null;
    if (obj instanceof String) {
        g = readGeometryText((String) obj);
    }
    else {
        g = (Geometry) obj;
    }
    select(g);
    zoom(g);
  }

  private Geometry readGeometryText(String wkt) {
    try {
      return GeomFunction.fromWKT(wkt);
    } catch (ParseException e) {
      // can't do much here
    }
    return null;
  }
}

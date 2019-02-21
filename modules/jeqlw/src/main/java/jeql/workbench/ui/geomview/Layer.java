package jeql.workbench.ui.geomview;

import java.awt.Color;

import jeql.api.row.Row;
import jeql.util.ColorUtil;
import jeql.workbench.RowListGeometryList;
import jeql.workbench.ui.geomview.style.BasicStyle;
import jeql.workbench.ui.geomview.style.Style;

import org.locationtech.jts.algorithm.PointLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

public class Layer 
{
  private String name = "";
  private GeometryList geomCont;
  private boolean isEnabled = true;
  
  private Style style;
  //private StyleList styleList;
  
  public Layer(String name) {
    this.name = name;
    setStyle(new BasicStyle(Color.BLUE, ColorUtil.lighter(Color.BLUE)));
  }

  public String getName() { return name; }
  
  public void setEnabled(boolean isEnabled)
  {
    this.isEnabled = isEnabled;
  }
  
  public void setSource(GeometryList geomCont)
  {
    this.geomCont = geomCont;
  }
  
  public GeometryList getSource()
  {
    return geomCont;
  }
  
  public boolean isEnabled()
  {
  	return isEnabled;
  }
  public Style getStyle()
  {
    return style;
  }

  public void setStyle(BasicStyle style)
  {
    this.style = style;
  }
  
  public Envelope getEnvelope()
  {
    Envelope env = new Envelope();
    for (int i = 0; i < geomCont.size(); i++) {
      Geometry geom = geomCont.getGeometry(i);
      if (geom == null) continue;
      env.expandToInclude(geom.getEnvelopeInternal());
    }
    return env;
  }
  
  public String locateRowDesc(Coordinate pt) {
    RowWithSchema row = locateRow(pt);
    if (row == null) return null;
    return row.row().getValue(0).toString();
  }
  
  public RowWithSchema locateRow(Coordinate pt) {
    RowListGeometryList rows = (RowListGeometryList) geomCont;
    PointLocator locator = new PointLocator();
    
    for (int i = 0; i < geomCont.size(); i++) {
      Geometry geom = geomCont.getGeometry(i);
      if (geom == null) continue;
      // check if pt is in Geom
      boolean isLocated = locator.intersects(pt, geom);
      if (isLocated)
        return new RowWithSchema(rows.getRow(i), rows.getSchema());
    }
    return null;
  }

  private boolean locate(Geometry geom, Coordinate pt) {
    if (! geom.getEnvelopeInternal().contains(pt)) return false;
    PointLocator locator = new PointLocator();
    return locator.intersects(pt, geom);
  }
}

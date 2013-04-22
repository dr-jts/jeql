package jeql.workbench.ui.geomview;

import jeql.workbench.ui.geomview.style.Style;

import com.vividsolutions.jts.geom.*;

public interface GeometryList 
{
  int size();
  Geometry getGeometry(int i);
  Style getStyle(int i);
}

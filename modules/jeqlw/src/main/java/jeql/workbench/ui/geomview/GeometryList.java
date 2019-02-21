package jeql.workbench.ui.geomview;

import jeql.workbench.ui.geomview.style.Style;

import org.locationtech.jts.geom.*;

public interface GeometryList 
{
  int size();
  Geometry getGeometry(int i);
  Style getStyle(int i);
  Label getLabel(int i);
}

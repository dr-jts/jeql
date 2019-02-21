package jeql.workbench.ui.geomview.style;

import java.awt.Graphics2D;

import jeql.workbench.ui.geomview.Viewport;

import org.locationtech.jts.geom.Geometry;

public interface Style {
  void paint(Geometry geom, Viewport viewport, Graphics2D g)
  throws Exception;
}

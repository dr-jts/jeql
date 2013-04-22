package jeql.workbench.ui.geomview.style;

import java.awt.*;

import jeql.workbench.ui.geomview.AppConstants;
import jeql.workbench.ui.geomview.GeometryPainter;
import jeql.workbench.ui.geomview.Viewport;

import com.vividsolutions.jts.geom.*;

public class BasicStyle implements Style
{
  private Color lineColor;
  private Color fillColor;
  private Stroke stroke  = null;
  
  public BasicStyle(Color lineColor, Color fillColor) {
    this.lineColor = lineColor;
    this.fillColor = fillColor;
  }

  public BasicStyle(Color lineColor, Color fillColor, float strokeWidth) {
    this.lineColor = lineColor;
    this.fillColor = fillColor;
    stroke = new BasicStroke(strokeWidth);
  }

  public BasicStyle() {
  }

  public void paint(Geometry geom, Viewport viewport, Graphics2D g)
  {
  	GeometryPainter.paint(geom, viewport, g, this);
  }
  
  public Color getLineColor() {
    return lineColor;
  }

  public Paint getFillColor() {
    return fillColor;
  }
  
  public Stroke getStroke() {
    return stroke;
  }
}

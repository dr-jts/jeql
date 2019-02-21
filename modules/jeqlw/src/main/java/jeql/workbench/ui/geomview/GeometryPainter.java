package jeql.workbench.ui.geomview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import jeql.workbench.ui.geomview.style.BasicStyle;
import jeql.workbench.ui.geomview.style.Style;

import org.locationtech.jts.awt.PointShapeFactory;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class GeometryPainter 
{
	private static Stroke GEOMETRY_STROKE = new BasicStroke();
	private static Stroke POINT_STROKE = new BasicStroke(AppConstants.POINT_SIZE);
	
  static Viewport viewportCache;
  static ShapeWriter converterCache;
  
  /**
   * Choose a fairly conservative decimation distance to avoid visual artifacts
   */
  private static final double DECIMATION_DISTANCE = 1.3;
  
  // TODO: is this a performance problem?
  // probably not - only called once for each geom painted
  public static ShapeWriter getConverter(Viewport viewport)
  {
    ShapeWriter sw = new ShapeWriter(viewport, new PointShapeFactory.Point());
    //sw.setRemoveDuplicatePoints(true);
    //sw.setDecimation(viewport.toModel(DECIMATION_DISTANCE));
    return sw;
  }
  
  /**
   * Paints a geometry onto a graphics context,
   * using a given Viewport.
   * 
   * @param geometry shape to paint
   * @param viewport
   * @param g the graphics context
   * @param lineColor line color (null if none)
   * @param fillColor fill color (null if none)
   */
  public static void paint(Geometry geometry, Viewport viewport, 
      Graphics2D g,
      BasicStyle style) 
  {
    ShapeWriter converter = getConverter(viewport);
    paint(geometry, converter, g, style);
  }
  
  private static void paint(Geometry geometry, ShapeWriter converter, Graphics2D g,
      BasicStyle style) 
  {
    if (geometry == null)
			return;

    if (geometry instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geometry;
      /**
       * Render each element separately.
       * Otherwise it is not possible to render both filled and non-filled
       * (1D) elements correctly
       */
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        paint(gc.getGeometryN(i), converter, g, style);
      }
      return;
    }

		Shape shape = converter.toShape(geometry);
    
		Color lineColor = style.getLineColor();
		Paint fillColor = style.getFillColor();
    // handle points in a special way for appearance and speed
		if (geometry instanceof Point) {
			g.setStroke(POINT_STROKE);
		  g.setPaint(lineColor);
      g.draw(shape);
      g.fill(shape);
			return;
		}

		Stroke stroke = style.getStroke();
    if (stroke == null)
		  g.setStroke(GEOMETRY_STROKE);
		else
		  g.setStroke(stroke);
		
    // Test for a polygonal shape and fill it if required
		if (geometry instanceof Polygon && fillColor != null) {
			g.setPaint(fillColor);
			g.fill(shape);
		}
		
		if (lineColor != null) {
		  g.setColor(lineColor);
		  try {
		    g.draw(shape);
		    
		  } 
		  catch (Throwable ex) {
		    System.out.println(ex);
		    // eat it!
		  }
		}
	}



}

package jeql.workbench.ui.geomview;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.awt.FontGlyphReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

import jeql.workbench.ui.geomview.style.Style;

public class LayerRenderer implements Renderer
{
  private static final double MIN_LABELLED_VIEW_SIZE = 20;
  private Layer layer;
  private GeometryList geomCont;
  private Viewport viewport;
  private boolean isCancelled = false;
  private List<Label> labels = new ArrayList<Label>();
  private Font font = null;

	public LayerRenderer(Layer layer, Viewport viewport)
	{
		this(layer, layer.getSource(), viewport);
	}
	
	public LayerRenderer(Layer layer, GeometryList geomCont, Viewport viewport)
	{
		this.layer = layer;
		this.geomCont = geomCont;
		this.viewport = viewport;
	}
	
  public void render(Graphics2D g)
  {
    if (! layer.isEnabled()) return;
    
    try {
      for (int i = 0; i < geomCont.size(); i++) {
      	Geometry geom = geomCont.getGeometry(i);
        if (geom == null) continue;
        
        render(g, viewport, geom, geomCont.getStyle(i), geomCont.getLabel(i));
      }
    } catch (Exception ex) {
      System.out.println(ex);
      // not much we can do about it - just carry on
    }
    
    renderLabels(g);
  }
  
  private void render(Graphics2D g, Viewport viewport, Geometry geometry, Style style, Label label)
  throws Exception
  {
    // cull non-visible geometries
  	// for maximum rendering speed this needs to be checked for each component
    if (! viewport.intersectsInModel(geometry.getEnvelopeInternal())) 
      return;
    
    if (label != null && label.hasText() && isLabellableSize(geometry, viewport)) {
      label.setPoint( computeLabelPoint(geometry, viewport));
      labels.add(label);
    }   
    
    if (geometry instanceof GeometryCollection) {
    	renderGeometryCollection(g, viewport, (GeometryCollection) geometry, style);
      return;
    }
    
    style.paint(geometry, viewport, g);
  }

  private boolean isLabellableSize(Geometry geometry, Viewport viewport2) {
    Envelope env = geometry.getEnvelopeInternal();
    double diagonalDist = diagonalSize(env);
    double viewSize = viewport.toView(diagonalDist);
    return viewSize > MIN_LABELLED_VIEW_SIZE;
  }

  private double diagonalSize(Envelope env) {
    double h = env.getHeight();
    double w = env.getWidth();
    return Math.sqrt(w * w + h * h);
  }

  private Point2D computeLabelPoint(Geometry geometry, Viewport viewport) {
    // TODO: use a better point
    Coordinate labelPt = geometry.getCentroid().getCoordinate();
    return viewport.toView(labelPt);
  }

  private void renderGeometryCollection(Graphics2D g, Viewport viewport, 
      GeometryCollection gc,
      Style style
      ) 
  throws Exception
  {
    /**
     * Render each element separately.
     * Otherwise it is not possible to render both filled and non-filled
     * (1D) elements correctly.
     * This also allows cancellation.
     */
    for (int i = 0; i < gc.getNumGeometries(); i++) {
    	render(g, viewport, gc.getGeometryN(i), style, null);
      if (isCancelled) return;
    }
  }

  private void renderLabels(Graphics2D g) {
    for (Label lbl : labels) {
      renderLabel(lbl, g);
    }
  }
  
  private void renderLabel(Label lbl, Graphics2D g) {
    //if (lbl.label == null || lbl.label.length() <= 0) return;
    
    if (font == null) {
      font = new Font(FontGlyphReader.FONT_SANSERIF, Font.PLAIN, lbl.fontSize);
      g.setFont(font);
    }
    
    g.setColor(lbl.color);
    Point2D pt = lbl.getPoint();
    g.drawString(lbl.label, (int) pt.getX(), (int) pt.getY());
  }

  public void cancel()
	{
		isCancelled = true;
	}

}

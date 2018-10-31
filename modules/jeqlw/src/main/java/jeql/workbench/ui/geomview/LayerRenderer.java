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

import jeql.util.GraphicsUtil;
import jeql.workbench.ui.geomview.style.Style;

public class LayerRenderer implements Renderer
{

  private Layer layer;
  private GeometryList geomCont;
  private Viewport viewport;
  private boolean isCancelled = false;
  private LabelRenderer labeller;


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
    
    labeller = new LabelRenderer(viewport);
    
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
    
    labeller.render(g);
  }
  
  private void render(Graphics2D g, Viewport viewport, Geometry geometry, Style style, Label label)
  throws Exception
  {
    // cull non-visible geometries
  	// for maximum rendering speed this needs to be checked for each component
    if (! viewport.intersectsInModel(geometry.getEnvelopeInternal())) 
      return;
      
    labeller.add(geometry, label);
    
    if (geometry instanceof GeometryCollection) {
    	renderGeometryCollection(g, viewport, (GeometryCollection) geometry, style);
      return;
    }
    
    style.paint(geometry, viewport, g);
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



  
  public void cancel()
	{
		isCancelled = true;
	}

}

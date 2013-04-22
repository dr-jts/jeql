package jeql.workbench.ui.geomview;
/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jeql.workbench.ui.geomview.tool.Tool;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
/**
 * Panel which displays rendered geometries.
 * 
 * @version 1.7
 */
public class GeometryViewPanel extends JPanel 
{
  private LayerList lyrList;
  
  private GridRenderer gridRenderer;

  private Tool currentTool = null;  

  private Viewport viewport = new Viewport(this);

  private RenderManager renderMgr;
  
  //----------------------------------------
  BorderLayout borderLayout1 = new BorderLayout();
  
  public GeometryViewPanel() {
    gridRenderer = new GridRenderer(viewport);
    try {
      initUI();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    renderMgr = new RenderManager(this);
  }

  void initUI() throws Exception {
    this.addComponentListener(new java.awt.event.ComponentAdapter() {

      public void componentResized(ComponentEvent e) {
        this_componentResized(e);
      }
    });
    this.setBackground(Color.white);
    this.setBorder(BorderFactory.createLoweredBevelBorder());
    this.setLayout(borderLayout1);
    
    setToolTipText("");
    setBorder(BorderFactory.createEmptyBorder());
    
  }

  public void setModel(LayerList model) {
    this.lyrList = model;
  }

  public LayerList getModel() {
    return lyrList;
  }

  public void setGridEnabled(boolean isEnabled) {
    gridRenderer.setEnabled(isEnabled);
  }

  public Viewport getViewport() { return viewport; }

  public void updateView()
  {
//    fireGeometryChanged(new GeometryEvent(this));
    forceRepaint();
  }
  
  public void forceRepaint() {
    renderMgr.setDirty(true);

    Component source = SwingUtilities.windowForComponent(this);
    if (source == null)
      source = this;
    source.repaint();
  }

  private LayerList getLayerList()
  {
    return lyrList;
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    renderMgr.render();
    renderMgr.copyImage(g);
  }
  
  /*
  public void flash(Geometry g)
  {
    Graphics2D gr = (Graphics2D) getGraphics();
    gr.setXORMode(Color.white);
    Stroke stroke = new BasicStroke(5);
    try {
      GeometryPainter.paint(g, viewport, gr, Color.RED, null, stroke);
      Thread.sleep(200);
      GeometryPainter.paint(g, viewport, gr, Color.RED, null, stroke);
    }
    catch (Exception ex) { 
      // nothing we can do
    }
    gr.setPaintMode();
  }
    */
  
  void this_componentResized(ComponentEvent e) {
  	renderMgr.componentResized();
    viewport.update();
  }

  public void setSource(LayerList lyrList)
  {
    boolean doZoom = this.lyrList == null;
    setModel(lyrList);
    if (doZoom) zoomToFullExtent();
    updateView();
  }

  public void setCurrentTool(Tool newTool) {
    removeMouseListener(currentTool);
    removeMouseMotionListener(currentTool);
    currentTool = newTool;
    currentTool.activate();
    setCursor(currentTool.getCursor());
    addMouseListener(currentTool);
    addMouseMotionListener(currentTool);
  }

  public void zoomToGeometry(int i) {
    Envelope g = lyrList.getEnvelope(i);
    if (g == null) return;
    zoom(g);
  }

  public void zoomToFullExtent() {
    zoom(lyrList.getEnvelopeAll());
  }

  public void zoom(Geometry geom) 
  {
    zoom(geom.getEnvelopeInternal());
  }
  
  public void zoom(Envelope zoomEnv) 
  {
    if (zoomEnv == null) return;
    
  	renderMgr.setDirty(true);
  	
    if (zoomEnv.isNull()) {
      viewport.zoomToInitialExtent();
      return;
    }

    double averageExtent = (zoomEnv.getWidth() + zoomEnv.getHeight()) / 2d;
    // fix to allow zooming to points
    if (averageExtent == 0.0)
      averageExtent = 1.0;
    double buffer = averageExtent * 0.03;
    
    zoomEnv.expandToInclude(zoomEnv.getMaxX() + buffer,
    		zoomEnv.getMaxY() + buffer);
    zoomEnv.expandToInclude(zoomEnv.getMinX() - buffer,
    		zoomEnv.getMinY() - buffer);
    viewport.zoom(zoomEnv);
  }

  public void zoom(Point center,
			double realZoomFactor) {

  	renderMgr.setDirty(true);

		double width = getSize().width / realZoomFactor;
		double height = getSize().height / realZoomFactor;
		double bottomOfNewViewAsPerceivedByOldView = center.y
				+ (height / 2d);
		double leftOfNewViewAsPerceivedByOldView = center.x
				- (width / 2d);
		Point bottomLeftOfNewViewAsPerceivedByOldView = new Point(
				(int) leftOfNewViewAsPerceivedByOldView,
				(int) bottomOfNewViewAsPerceivedByOldView);
		Point2D bottomLeftOfNewViewAsPerceivedByModel = viewport.toModel(bottomLeftOfNewViewAsPerceivedByOldView);
		viewport.setScale(getViewport().getScale() * realZoomFactor);
		viewport.setViewOrigin(bottomLeftOfNewViewAsPerceivedByModel.getX(), bottomLeftOfNewViewAsPerceivedByModel.getY());
	}

  public void zoomPan(double xDisplacement, double yDisplacement) 
  {
  	renderMgr.setDirty(true);
    getViewport().setViewOrigin(getViewport().getViewOriginX() - xDisplacement,
        getViewport().getViewOriginY() - yDisplacement);
  }

  public String cursorLocation(Point2D pView)
  {
    Point2D p = getViewport().toModel(pView);
    NumberFormat format = getViewport().getScaleFormat();
    return format.format(p.getX()) 
    + ", " 
    + format.format(p.getY());
  }
  

  public Renderer getRenderer()
  {
  	return new GeometryEditPanelRenderer();
  }
  
  class GeometryEditPanelRenderer implements Renderer
  {
  	private Renderer currentRenderer = null;
    
  	public GeometryEditPanelRenderer()
  	{
  	}
  	
    public void render(Graphics2D g)
    {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      
      gridRenderer.paint(g2);
      
      renderLayers(g2);
      
    }
    
    public void renderLayers(Graphics2D g)
    {
    	LayerList layerList = getLayerList();
    	int n = layerList.size();
    	for (int i = 0; i < n; i++) {
    			currentRenderer = new LayerRenderer(layerList.getLayer(i), viewport);
    			currentRenderer.render(g);
    	}
    	currentRenderer = null;
    }
    
  	public synchronized void cancel()
  	{
  		if (currentRenderer != null)
  			currentRenderer.cancel();
  	}

  }
}



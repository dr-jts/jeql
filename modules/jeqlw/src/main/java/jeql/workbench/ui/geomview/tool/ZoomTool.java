package jeql.workbench.ui.geomview.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import jeql.workbench.images.IconLoader;
import jeql.workbench.ui.geomview.AppConstants;
import jeql.workbench.ui.geomview.GeometryViewPanel;


public class ZoomTool extends BasicTool 
{
  private static ZoomTool singleton = null;

  public static ZoomTool getInstance() {
    if (singleton == null) {
      Cursor zoomInCursor = Toolkit.getDefaultToolkit().createCustomCursor(
          IconLoader.MAGNIFY_CURSOR.getImage(),
          new java.awt.Point(16, 16), "Zoom In");
      singleton = new ZoomTool(2, zoomInCursor);
    }
    return singleton;
  }

  private double zoomFactor = 2;
  private Cursor cursor = Cursor.getDefaultCursor();
  private Point zoomBoxStart = null;
  private Point zoomBoxEnd = null;
  private Point2D mouseStartModel;
  
  public ZoomTool() { }

  public ZoomTool(double zoomFactor, Cursor cursor) {
    this();
    this.zoomFactor = zoomFactor;
    this.cursor = cursor;
  }

  public Cursor getCursor() {
    return cursor;
  }

  public void mouseClicked(MouseEvent mouseEvent) 
  {
    // determine if zoom in (left) or zoom out (right)
    double realZoomFactor = isRight(mouseEvent)
         ? (1d / zoomFactor) : zoomFactor;
    Point center = mouseEvent.getPoint();
    panel().zoom(center, realZoomFactor);
  }

  public void mousePressed(MouseEvent mouseEvent)
  {
  	zoomBoxStart = mouseEvent.getPoint();
  	zoomBoxEnd= mouseEvent.getPoint();
  	mouseStartModel = null;
  	if (isRight(mouseEvent)) {
      mouseStartModel = toModel(mouseEvent.getPoint());
  	}
  }
  
  public void mouseReleased(MouseEvent mouseEvent) {
    
    // don't process this event if the mouse was clicked or dragged a very
    // short distance
    if (! isSignificantMouseMove())
      return;

    if (! isRight(mouseEvent)) {
      // left-drag does a zoom
      zoomToBox();
      return;
    } else {
      // right-drag does a pan
      doPan(mouseEvent);
    }

  }

  private void doPan(MouseEvent mouseEvent) {
    Point2D mouseEndModel = toModel(mouseEvent.getPoint());
    double dx = mouseEndModel.getX() - mouseStartModel.getX();
    double dy = mouseEndModel.getY() - mouseStartModel.getY();
    panel().zoomPan(dx, dy);
  }

  private boolean isRight(MouseEvent mouseEvent) {
    return SwingUtilities.isRightMouseButton(mouseEvent);
  }

  private void zoomToBox() {
    // zoom to extent box
    int centreX = (zoomBoxEnd.x + zoomBoxStart.x) / 2;
    int centreY = (zoomBoxEnd.y + zoomBoxStart.y) / 2;
    Point centre = new Point(centreX, centreY);

    int dx = Math.abs(zoomBoxEnd.x - zoomBoxStart.x);
    int dy = Math.abs(zoomBoxEnd.y - zoomBoxStart.y);
    // ensure deltas are valid
    if (dx <= 0)
      dx = 1;
    if (dy <= 0)
      dy = 1;

    GeometryViewPanel panel = panel();
    double widthFactor = panel.getSize().width / dx;
    double heightFactor = panel.getSize().height / dy;
    double zoomFactor = Math.min(widthFactor, heightFactor);

    // double zoomFactor = 2;
    panel().zoom(centre, zoomFactor);
  }

  public void mouseDragged(MouseEvent e)
  {
  	Graphics g = panel().getGraphics();
  	g.setColor(AppConstants.BAND_CLR);
  	g.setXORMode(Color.white);
  	// erase old band
  	drawBand(g);

  	// draw new band
  	Point currPoint = e.getPoint();
  	zoomBoxEnd = currPoint;
    drawBand(g);
  }
  
  private void drawBand(Graphics g) {
    if (isPan()) {
      drawLine(g);
    }
    else {
      drawRect(g);
    }
  }
  
  private boolean isPan() {
    return mouseStartModel != null;
  }

  public void activate() { }
  
  private static final int MIN_MOVEMENT = 3;
  
  private boolean isSignificantMouseMove()
  {
  	if (Math.abs(zoomBoxStart.x - zoomBoxEnd.x) < MIN_MOVEMENT)
  		return false;
  	if (Math.abs(zoomBoxStart.y - zoomBoxEnd.y) < MIN_MOVEMENT)
  		return false;
  	return true;
  }
  
  public void drawRect(Graphics g)
  {
  	Point base = new Point(Math.min(zoomBoxStart.x, zoomBoxEnd.x),
  			Math.min(zoomBoxStart.y, zoomBoxEnd.y));
  	int width = Math.abs(zoomBoxEnd.x - zoomBoxStart.x);
  	int height = Math.abs(zoomBoxEnd.y - zoomBoxStart.y);
  	g.drawRect(base.x, base.y, width, height);
  }
  public void drawLine(Graphics g)
  {
    g.drawLine(zoomBoxStart.x, zoomBoxStart.y, zoomBoxEnd.x, zoomBoxEnd.y);
  }
  
}


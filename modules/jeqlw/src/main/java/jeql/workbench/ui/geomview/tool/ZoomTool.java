package jeql.workbench.ui.geomview.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
  private Point mouseStart = null;
  private Point mouseEnd = null;
  private Point2D panStart;
  
  public ZoomTool() { }

  public ZoomTool(double zoomFactor, Cursor cursor) {
    this();
    this.zoomFactor = zoomFactor;
    this.cursor = cursor;
  }

  public Cursor getCursor() {
    return cursor;
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    double notches = e.getPreciseWheelRotation();
    double zoomFactor = Math.abs(notches) * 2;
    if (notches > 0 && zoomFactor > 0) zoomFactor = 1.0 / zoomFactor;
    panel().zoom(e.getPoint(), zoomFactor);
  }
  
  public void doClick(MouseEvent mouseEvent) 
  {
    if (mouseEvent.isShiftDown()) {
      panel().inspect(mouseEvent.getPoint());
      return;
    }
    // disable double-clicks for now
    //if (mouseEvent.getClickCount() > 1) return;
    
    // determine if zoom in (left) or zoom out (right)
    double realZoomFactor = SwingUtilities.isRightMouseButton(mouseEvent)
         ? (1d / zoomFactor) : zoomFactor;
    //Point center = mouseEvent.getPoint();
    panel().zoom(toModel(mouseEvent.getPoint()), realZoomFactor);
  }

  public void mousePressed(MouseEvent mouseEvent)
  {
  	mouseStart = mouseEvent.getPoint();
  	mouseEnd= mouseEvent.getPoint();
    panStart = isPanGesture(mouseEvent) ? toModel(mouseStart) : null;
  }
  
  /**
   * Always called, even on mouse click.
   * So handle clicks here rather than in mouseClick code
   */
  public void mouseReleased(MouseEvent mouseEvent) {
    // don't process if mouse was clicked or dragged a very short distance
    if (! isSignificantMouseMove(mouseEvent.getPoint())) {
      doClick(mouseEvent);
      return;
    }

    if (! isPanGesture(mouseEvent)) {
      // left-drag does a zoom
      zoomToBox();
    } else {
      // right-drag does a pan
      doPan(mouseEvent);
    }
  }

  private void doPan(MouseEvent mouseEvent) {
    Point2D mouseEndModel = toModel(mouseEvent.getPoint());
    double dx = mouseEndModel.getX() - panStart.getX();
    double dy = mouseEndModel.getY() - panStart.getY();
    panel().zoomPan(dx, dy);
  }
  private static boolean isPanGesture(MouseEvent e) {
    return e.isControlDown() || SwingUtilities.isRightMouseButton(e);
  }
  private boolean isPanning() {
    return panStart != null;
  }

  private void zoomToBox() {
    // zoom to extent box
    int centreX = (mouseEnd.x + mouseStart.x) / 2;
    int centreY = (mouseEnd.y + mouseStart.y) / 2;
    Point centre = new Point(centreX, centreY);

    int dx = Math.abs(mouseEnd.x - mouseStart.x);
    int dy = Math.abs(mouseEnd.y - mouseStart.y);
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
    panel().zoom(toModel(centre), zoomFactor);
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
  	mouseEnd = currPoint;
    drawBand(g);
  }
  
  private void drawBand(Graphics g) {
    if (isPanning()) {
      drawLine(g);
    }
    else {
      drawRect(g);
    }
  }

  public void activate() { }
  
  private static final int MIN_MOVEMENT = 5;
  
  private boolean isSignificantMouseMove(Point p)
  {
    int delta = Math.abs(mouseStart.x - p.x) + Math.abs(mouseStart.y - p.y);
    if (delta < MIN_MOVEMENT)
        return false;
    return true;
  }
  
  public void drawRect(Graphics g)
  {
  	Point base = new Point(Math.min(mouseStart.x, mouseEnd.x),
  			Math.min(mouseStart.y, mouseEnd.y));
  	int width = Math.abs(mouseEnd.x - mouseStart.x);
  	int height = Math.abs(mouseEnd.y - mouseStart.y);
  	g.drawRect(base.x, base.y, width, height);
  }
  public void drawLine(Graphics g)
  {
    g.drawLine(mouseStart.x, mouseStart.y, mouseEnd.x, mouseEnd.y);
  }
  
}


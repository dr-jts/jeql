package jeql.workbench.ui.geomview.tool;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import jeql.workbench.images.IconLoader;


/**
 * @version 1.7
 */
public class PanTool extends BasicTool {
  private static PanTool singleton = null;

  public static PanTool getInstance() {
    if (singleton == null)
      singleton = new PanTool();
    return singleton;
  }

  private Point2D source;

  private Cursor cursorHand = Toolkit.getDefaultToolkit().createCustomCursor(
      IconLoader.createIcon("Hand.png").getImage(), new java.awt.Point(7, 7), "Pan");

  private PanTool() {
  }

  public Cursor getCursor() {
    return cursorHand;
  }

  public void activate() {
    source = null;
  }

  public void mousePressed(MouseEvent e) {
    source = toModel(e.getPoint());
  }
  
  public void mouseReleased(MouseEvent e) {
    if (source == null)
      return;
    Point2D destination = toModel(e.getPoint());
    double xDisplacement = destination.getX() - source.getX();
    double yDisplacement = destination.getY() - source.getY();
    panel().zoomPan(xDisplacement, yDisplacement);
    /*
    getViewport().setViewOrigin(getViewport().getViewOriginX() - xDisplacement,
        getViewport().getViewOriginY() - yDisplacement);
        */
  }

}

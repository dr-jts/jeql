package jeql.workbench.ui.geomview;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.awt.FontGlyphReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import jeql.jts.geom.util.ConstrainedInteriorPoint;
import jeql.util.GraphicsUtil;

public class LabelRenderer {
  private static final double MIN_LABELLED_VIEW_SIZE = 20;
  
  public static boolean isLabellableSize(Geometry geometry, Viewport viewport) {
    Envelope env = geometry.getEnvelopeInternal();
    double diagonalDist = diagonalSize(env);
    double viewSize = viewport.toView(diagonalDist);
    return viewSize > MIN_LABELLED_VIEW_SIZE;
  }

  private static double diagonalSize(Envelope env) {
    double h = env.getHeight();
    double w = env.getWidth();
    return Math.sqrt(w * w + h * h);
  }
  
  private List<Label> labels = new ArrayList<Label>();
  private Font font = null;
  private Viewport viewport;

  LabelRenderer(Viewport viewport) {
    this.viewport = viewport;
  }
  
  public void add(Geometry geometry, Label label) {
    if (label == null || ! label.hasText()) return;
    
    if (geometry.getNumGeometries() <= 1) {
      addLabel(geometry, label);
      return;
    }
    for (int i = 0; i < geometry.getNumGeometries(); i++) {
      addLabel(geometry.getGeometryN(i), label.copy());
    }
  }

  private void addLabel(Geometry geometry, Label label) {
    if (! isLabellableSize(geometry, viewport)) return;
    label.setPoint( computeLabelPoint(geometry, viewport));
    labels.add(label); 
  }
  
  private Point2D computeLabelPoint(Geometry geometry, Viewport viewport) {
    // TODO: use a better point
    Coordinate labelPt = null;
    if (geometry instanceof Polygon) {
      labelPt = ConstrainedInteriorPoint.getPoint((Polygon) geometry, viewport.getModelEnv());
    }
    if (labelPt == null) {
      labelPt = geometry.getCentroid().getCoordinate();
    }
    return viewport.toView(labelPt);
  }
  
  
  public void render(Graphics2D g) {
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
    
    Point2D pt = lbl.getPoint();
    if (lbl.hasHalo()) {
      g.setColor(lbl.haloColor);
      AffineTransform oldTrans = GraphicsUtil.transform(g, pt, 0);
      // TODO: handle multiline better (perhaps skip halo?)
      GraphicsUtil.drawHalo(g, lbl.label, lbl.haloSize, font);
      g.setTransform(oldTrans);
    }
    
    g.setColor(lbl.color);
    GraphicsUtil.drawStringMultiLine(g, lbl.label, (int) pt.getX(), (int) pt.getY());
    //g.drawString(lbl.label, (int) pt.getX(), (int) pt.getY());
  }

}

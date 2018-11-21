package jeql.workbench.ui.geomview;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Label {
  String label;
  Color color;
  int fontSize;
  private Point2D pt;
  Color haloColor;
  int haloSize;
  
  
  public Label(String label, Color color, int fontSize, Color haloColor, int haloSize) {
    this.label = label;
    this.color = color;
    this.fontSize = fontSize;
    this.haloColor = haloColor;
    this.haloSize = haloSize;
  }

  public Label(Label lbl) {
    this.label = lbl.label;
    this.color = lbl.color;
    this.fontSize = lbl.fontSize;
    this.haloColor = lbl.haloColor;
    this.haloSize = lbl.haloSize;
    this.pt = lbl.pt;
  }

  public void setPoint(Point2D pt) {
    this.pt = pt;
  }

  public Point2D getPoint() {
    return pt;
  }

  public boolean hasText() {
    return label != null && label.length() > 0;
  }
  
  public boolean hasHalo() {
    return haloColor != null;
  }

 
  public Label copy() {
    return new Label(this);
  }
}

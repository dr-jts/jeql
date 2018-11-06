package jeql.workbench.ui.geomview;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Label {
  String label;
  Color color;
  int fontSize;
  private Point2D pt;
  
  
  public Label(String label, Color color, int fontSize) {
    this.label = label;
    this.color = color;
    this.fontSize = fontSize;
  }

  public Label(Label lbl) {
    this.label = lbl.label;
    this.color = lbl.color;
    this.fontSize = lbl.fontSize;
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
  
  public Label copy() {
    return new Label(this);
  }
}

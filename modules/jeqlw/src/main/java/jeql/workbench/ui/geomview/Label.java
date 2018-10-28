package jeql.workbench.ui.geomview;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Label {
  String label;
  Color color;
  double fontSize;
  private Point2D pt;
  
  
  public Label(String label, Color color) {
    this.label = label;
    this.color = color;
  }

  public void setPoint(Point2D pt) {
    this.pt = pt;
  }

  public Point2D getPoint() {
    return pt;
  }
}

package jeql.command.chart;

public class XYItem
{
  private double x;
  private double y;
  private String label;
  
 public XYItem(double x, double y, String label)
 {
   this.x = x;
   this.y = y;
   this.label = label;
 }
 
   public double getX() { return x; }
   public double getY() { return y; }
   public String getLabel() { return label; }
}

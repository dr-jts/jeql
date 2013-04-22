package jeql.workbench.ui.data;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DoubleFormatRenderer extends DefaultTableCellRenderer
{
  //private static final DecimalFormat formatter = new DecimalFormat("#.00");

  public DoubleFormatRenderer()
  {
     super();
     setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
  {
    // Format the cell value as required
    //value = formatter.format((Number) value);
    if (value == null)
      value = "";
    else
      value = Double.toString((Double) value);
    // Pass it on to parent class
    return super.getTableCellRendererComponent(table, value, isSelected,
        hasFocus, row, column);
  }
}

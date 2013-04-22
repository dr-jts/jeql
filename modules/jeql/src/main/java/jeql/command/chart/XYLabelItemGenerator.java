package jeql.command.chart;

import java.text.MessageFormat;
import java.util.List;

import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

public class XYLabelItemGenerator 
    extends AbstractXYItemLabelGenerator
    implements XYItemLabelGenerator
{
  private List xyItems;
  
  public XYLabelItemGenerator(List xyItems)
  {
    this.xyItems = xyItems;
  }
  
  /**
   * Generates a label string for an item in the dataset.
   *
   * @param dataset  the dataset (<code>null</code> not permitted).
   * @param series  the series (zero-based index).
   * @param item  the item (zero-based index).
   *
   * @return The label (possibly <code>null</code>).
   */
  public String generateLabelString(XYDataset dataset, int series, int item) {
      String result = ((XYItem) xyItems.get(item)).getLabel();
      return result;
  }

  public String generateLabel(XYDataset dataset, int series, int item)
  {
    return generateLabelString(dataset, series, item);
  }

}

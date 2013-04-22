package jeql.command.chart;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.SchemaUtil;
import jeql.api.table.Table;
import jeql.util.TypeUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

public class CategoryChart 
extends BaseChart
{
	public CategoryChart(String chartType)
	{
		
	}
	
	public JFreeChart createChart() throws Exception 
	{
	  if (param.getNumSeries() == 0) {
	    JFreeChart chart = createChart(createDataset(param.getFirstDataTable(), param.getSeriesKey(0)));
	    return chart;
	  }
	  // create multiple series dataset
	  
    JFreeChart chart = createChart(createDataset(param));
    return chart;
	  
	}

	private CategoryDataset createDataset(ChartParameters param)
  {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (int i = 0; i < param.getNumSeries(); i++) {
      addSeries(dataset, param.getSeriesKey(i), param.getSeries(i));
    }
    return dataset;
  }

  private void addSeries(DefaultCategoryDataset dataset, String datasetKey,
      Table tbl)
  {
    int keyIndex = findColumnIndex(tbl, DATA_COL_KEY, DATA_COL_X, -1);
    if (keyIndex < 0)
      keyIndex = SchemaUtil.getColumnWithType(tbl.getRows().getSchema(), String.class);
    // if no named key and no string key, just use first column
    if (keyIndex < 0)
      keyIndex = 0;
    
    int valueIndex = findRangeIndex(tbl, DATA_COL_VALUE, DATA_COL_Y, keyIndex);
    
    int rowCount = 0;
    for (RowIterator i = tbl.getRows().iterator(); ; )
    {
      Row row = i.next();
      if (row == null) break;
      String key = "" + rowCount++;
      if (keyIndex >= 0) {
        Object keyObj = row.getValue(keyIndex);
        // guard against bad data
        if (keyObj == null) continue;
        key = keyObj.toString();
        double value = TypeUtil.toDouble(row.getValue(valueIndex));
        dataset.setValue(new Double(value), datasetKey, key);
      }
    }
  }

  private CategoryDataset createDataset(Table tbl, String datasetKey)
  {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
    if (tbl == null)
      return dataset;
    addSeries(dataset, datasetKey, tbl);
    return dataset;
  }

  private CategoryDataset OLDcreateDataset(Table tbl, String datasetKey)
  {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
    if (tbl == null)
      return dataset;
    
    int keyIndex = findColumnIndex(tbl, DATA_COL_KEY, DATA_COL_X, -1);
    if (keyIndex < 0)
      keyIndex = SchemaUtil.getColumnWithType(tbl.getRows().getSchema(), String.class);
    // if no named key and no string key, just use first column
    if (keyIndex < 0)
      keyIndex = 0;
    
    int valueIndex = findRangeIndex(tbl, DATA_COL_VALUE, DATA_COL_Y, keyIndex);
    
    int rowCount = 0;
    for (RowIterator i = tbl.getRows().iterator(); ; )
    {
      Row row = i.next();
      if (row == null) break;
      String key = "" + rowCount++;
      if (keyIndex >= 0) {
        Object keyObj = row.getValue(keyIndex);
        // guard against bad data
        if (keyObj == null) continue;
        key = keyObj.toString();
        double value = TypeUtil.toDouble(row.getValue(valueIndex));
        dataset.setValue(new Double(value), datasetKey, key);
      }
    }
    return dataset;
  }

	public String getChartType()
	{
		String chartType = param.chartType;
		return chartType;
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset  the dataset.
	 * 
	 * @return A chart.
	 */
	private JFreeChart createChart(CategoryDataset dataset) 
	{
		String chartType = getChartType();
		
		JFreeChart chart = null;
		if (chartType == CHART_TYPE_LINE && param.isExtruded) {
			chart = ChartFactory.createLineChart3D(
      		param.title,
          param.axisXTitle,
          param.axisYTitle, 
          dataset, 
          param.plotOrientation, 
          param.showLegend,
          false, 
          false);
		}        
		else if (chartType == CHART_TYPE_LINE && param.isFilled)
			chart = ChartFactory.createAreaChart(
      		param.title,
          param.axisXTitle,
          param.axisYTitle, 
          dataset, 
          param.plotOrientation, 
          param.showLegend,
          false, 
          false);

		else if (chartType == CHART_TYPE_BAR && param.isExtruded)
			chart = ChartFactory.createBarChart3D(
      		param.title,
          param.axisXTitle,
          param.axisYTitle, 
          dataset, 
          param.plotOrientation, 
          param.showLegend,
          false, 
          false);
		else if (chartType == CHART_TYPE_LINE) {
			chart = ChartFactory.createLineChart(
      		param.title,
          param.axisXTitle,
          param.axisYTitle, 
          dataset, 
          param.plotOrientation, 
          param.showLegend,
          false, 
          false);
		}        

		else if (chartType == CHART_TYPE_BAR) {
      // Create a simple Bar chart
			chart = ChartFactory.createBarChart(
      		param.title,
          param.axisXTitle,
          param.axisYTitle, 
          dataset, 
          param.plotOrientation, 
          param.showLegend,
          false, 
          false);
		}
		else if (chartType == CHART_TYPE_BAR_STACKED && param.isExtruded) {
			chart = ChartFactory.createStackedBarChart3D(
      		param.title,
          param.axisXTitle,
          param.axisYTitle, 
          dataset, 
          param.plotOrientation, 
          param.showLegend,
          false, 
          false);
		}
		else if (chartType == CHART_TYPE_BAR_STACKED) {
			chart = ChartFactory.createStackedBarChart(
      		param.title,
          param.axisXTitle,
          param.axisYTitle, 
          dataset, 
          param.plotOrientation, 
          param.showLegend,
          false, 
          false);
		}
		
		CategoryPlot plot = chart.getCategoryPlot();
		
		// set X axis label angles
		if (param.axisXLabelRotation != 0.0) {
			CategoryAxis axis = plot.getDomainAxis();
			axis.setCategoryLabelPositions(getCategoryLabelPosition(param.axisXLabelRotation));
		}
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		if (param.showItemLabels) {
			renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setBaseItemLabelsVisible(true);
			
			if (param.itemLabelRotation != 0.0) {
				double ang = -1 * Math.PI / 2 * param.itemLabelRotation;
				ItemLabelPosition pos = renderer.getBasePositiveItemLabelPosition();
				ItemLabelPosition pos2 = new ItemLabelPosition(pos.getItemLabelAnchor(), 
					TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, 
					ang);
				renderer.setBasePositiveItemLabelPosition(pos2);
			}
		}
		// TODO: expose some of these as parameters (eg barColor: clr, shadow: boolean
    // set up bar painting
    if (param.color != null) 
      renderer.setSeriesPaint(0, param.color);
    if (renderer instanceof BarRenderer) {
      BarRenderer barRend = (BarRenderer) renderer;
      // don't draw shadow
      barRend.setShadowVisible(false);
      barRend.setDrawBarOutline(false);
      // use flat filled bar rather than rounded bar with sheen
      barRend.setBarPainter(new StandardBarPainter());
    }
		chart.getCategoryPlot().setRenderer(renderer);
		
		
    return chart;
	}

	public static CategoryLabelPositions getCategoryLabelPosition(double angle)
	{
		if (angle == 0.0) return CategoryLabelPositions.STANDARD;
		
		if (angle > 0.0 && angle < 1.0) return CategoryLabelPositions.UP_45;
		if (angle >= 1.0) return CategoryLabelPositions.UP_90;
		
		if (angle < 0.0 && angle > -1.0) return CategoryLabelPositions.DOWN_45;
		if (angle <= -1.0) return CategoryLabelPositions.DOWN_90;
		
		return CategoryLabelPositions.STANDARD;
	}
}

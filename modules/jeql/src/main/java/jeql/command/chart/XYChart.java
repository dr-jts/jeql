package jeql.command.chart;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowUtil;
import jeql.api.row.SchemaUtil;
import jeql.api.table.Table;
import jeql.std.function.ValFunction;
import jeql.style.StyleConstants;
import jeql.util.TypeUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

public class XYChart extends BaseChart
{
  private List xyItems;
  
	public XYChart(String chartType)
	{
		
	}
	
	public JFreeChart createChart() throws Exception {
		JFreeChart chart = createChart(createDataset(param.getFirstDataTable()));
		return chart;
	}

	private XYDataset createDataset(Table tbl)
	{
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		if (tbl == null)
			return dataset;
		
    int xIndex = findColumnIndex(tbl, DATA_COL_X, DATA_COL_KEY, 0);
    /**
     * If only one column, use it for Y
     */
    if (tbl.getRows().getSchema().size() == 1)
      xIndex = -1;
    int yIndex = findRangeIndex(tbl, DATA_COL_Y, DATA_COL_VALUE, xIndex);
    int lblIndex = findColumnIndex(tbl, StyleConstants.LABEL);
		
		xyItems = scanXY(tbl, xIndex, yIndex, lblIndex);
    double[][] xy = extractXYArray(xyItems);
		dataset.addSeries(param.getSeriesKey(0), xy);
		
		return dataset;
	}

  private List scanXY(Table tbl, int xIndex, int yIndex, int lblIndex)
  {
    List xyItems = new ArrayList();
		
		int rowCount = 0;
		for (RowIterator i = tbl.getRows().iterator(); ; )
		{
			Row row = i.next();
			if (row == null) break;
			
			// default X value is the row count
			double x = rowCount++;
			if (xIndex >= 0)
			  x = TypeUtil.toDouble(row.getValue(xIndex));
			
			double y = TypeUtil.toDouble(row.getValue(yIndex));
			String label = RowUtil.getString(lblIndex, row, null);
			xyItems.add(new XYItem(x, y, label));
		}

    return xyItems;
  }
	
	private static double[][] extractXYArray(List xyPairs)
	{
		double[][] xy = new double[2][xyPairs.size()];
		int index = 0;
		for (Iterator i = xyPairs.iterator(); i.hasNext(); ) {
		  XYItem item = (XYItem) i.next();
			xy[0][index] = item.getX();
			xy[1][index] = item.getY();
			index++;
		}
		return xy;
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
	private JFreeChart createChart(XYDataset dataset) 
	{
		String chartType = getChartType();
		
		JFreeChart chart = null;
		if (chartType == CHART_TYPE_XY && param.isFilled)
			chart = ChartFactory.createXYAreaChart(
				param.title, // chart title
        param.axisXTitle,
        param.axisYTitle, 
				dataset, // data
        param.plotOrientation, 
				param.showLegend, // include legend
				false, // no tooltips
				false // no URLs  
				);
		else if (chartType == CHART_TYPE_XY)
			chart = ChartFactory.createXYLineChart(
				param.title, // chart title
        param.axisXTitle,
        param.axisYTitle, 
				dataset, // data
        param.plotOrientation, 
				param.showLegend, // include legend
				false, // no tooltips
				false // no URLs  
				);
		else if (chartType == CHART_TYPE_XY_STEP && param.isFilled)
			chart = ChartFactory.createXYStepAreaChart(
				param.title, // chart title
        param.axisXTitle,
        param.axisYTitle, 
				dataset, // data
        param.plotOrientation, 
				param.showLegend, // include legend
				false, // no tooltips
				false // no URLs  
				);
		else if (chartType == CHART_TYPE_XY_STEP)
			chart = ChartFactory.createXYStepChart(
				param.title, // chart title
        param.axisXTitle,
        param.axisYTitle, 
				dataset, // data
        param.plotOrientation, 
				param.showLegend, // include legend
				false, // no tooltips
				false // no URLs  
				);
		else if (chartType == CHART_TYPE_POLAR)
				chart = ChartFactory.createPolarChart(
					param.title, // chart title
					dataset, // data
					param.showLegend, // include legend
					false, // no tooltips
					false // no URLs  
					);
		
		// show item labels for XY charts
		if (chartType != CHART_TYPE_POLAR) {
			XYItemRenderer rend = (XYItemRenderer) chart.getXYPlot().getRenderer();
			rend.setBaseItemLabelGenerator(new XYLabelItemGenerator(xyItems));
			//rend.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator()); 
			rend.setBaseItemLabelsVisible(true);
			// rr.setPlotShapes(true);
		}
		
		return chart;

	}

}

package jeql.command.chart;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.table.Table;
import jeql.util.TypeUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class PieChart extends BaseChart
{
	public PieChart(String chartType)
	{
		
	}
	
	public JFreeChart createChart() throws Exception {
		JFreeChart chart = createChart(createDataset(param.getFirstDataTable()));
		return chart;
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return A sample dataset.
	 */
	private PieDataset createSampleDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("One", new Double(43.2));
		dataset.setValue("Two", new Double(10.0));
		dataset.setValue("Three", new Double(27.5));
		dataset.setValue("Four", new Double(17.5));
		dataset.setValue("Five", new Double(11.0));
		dataset.setValue("Six", new Double(200));
		return dataset;
	}

	private PieDataset createDataset(Table tbl)
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		if (tbl == null)
			return dataset;
		
    int keyIndex = findColumnIndex(tbl, DATA_COL_KEY, DATA_COL_X, 0);
    int valueIndex = findColumnIndex(tbl, DATA_COL_VALUE, DATA_COL_Y, 1);
		
		for (RowIterator i = tbl.getRows().iterator(); ; )
		{
			Row row = i.next();
			if (row == null) break;
			String key = row.getValue(keyIndex).toString();
			double value = TypeUtil.toDouble(row.getValue(valueIndex));
			dataset.setValue(key, new Double(value));
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
	private JFreeChart createChart(PieDataset dataset) 
	{
		JFreeChart chart = null;
		if (param.isExtruded)
			chart = ChartFactory.createPieChart3D(
				param.title, // chart title
				dataset, // data
				param.showLegend, // include legend
				false, // no tooltips
				false // no URLs  
				);
		else 
			chart = ChartFactory.createPieChart(
				param.title, // chart title
				dataset, // data
				param.showLegend, // include legend
				false, // no tooltips
				false // no URLs  
				);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setCircular(true);
		plot.setLabelGap(0.01);
		return chart;

	}

}

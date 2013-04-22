package jeql.command.chart;


import java.io.File;

import jeql.api.row.RowSchema;
import jeql.api.row.SchemaUtil;
import jeql.api.table.Table;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;

public abstract class BaseChart 
{
	
	public static final String CHART_TYPE_BAR = "Bar";
	public static final String CHART_TYPE_BAR_STACKED = "BarStacked";
	public static final String CHART_TYPE_LINE = "Line";
	public static final String CHART_TYPE_PIE = "Pie";
	public static final String CHART_TYPE_XY = "XY";
	public static final String CHART_TYPE_XY_STEP = "XYStep";
	public static final String CHART_TYPE_POLAR = "Polar";

	public static final String DATA_COL_KEY = "key";
	public static final String DATA_COL_VALUE = "value";
	public static final String DATA_COL_X = "x";
	public static final String DATA_COL_Y = "y";
	
	public static final String[] chartTypes = new String[] {
		CHART_TYPE_BAR, 
		CHART_TYPE_BAR_STACKED, 
		CHART_TYPE_LINE,
		CHART_TYPE_PIE, 
		CHART_TYPE_XY, 
		CHART_TYPE_XY_STEP, 
		CHART_TYPE_POLAR
	};
	
	public static String getChartType(String chartTypeStr)
	{
		for (int i = 0; i < BaseChart.chartTypes.length; i++) {
			if (chartTypeStr.equalsIgnoreCase(BaseChart.chartTypes[i]))
				return BaseChart.chartTypes[i];
		}
		return null;
	}

	public static BaseChart createChartObject(String chartType)
	{
		if (chartType == BaseChart.CHART_TYPE_BAR
				||	chartType == BaseChart.CHART_TYPE_BAR_STACKED
				||	chartType == BaseChart.CHART_TYPE_LINE
				)
			return new CategoryChart(chartType);
		if (chartType == BaseChart.CHART_TYPE_PIE)
				return new PieChart(chartType);
		if (chartType == BaseChart.CHART_TYPE_XY
				|| chartType == BaseChart.CHART_TYPE_XY_STEP
				|| chartType == BaseChart.CHART_TYPE_POLAR)
				return new XYChart(chartType);
		return null;
	}

	protected ChartParameters param;
	
	void setParameters(ChartParameters param)
	{
		this.param = param;
	}
	
	public void writeChart(String filename, int x, int y)
		throws Exception
	{	
		JFreeChart chart = createChart();
		if (param.backgroundPaint != null) chart.getPlot().setBackgroundPaint(param.backgroundPaint);
		//TODO: handle legends with series titles 
		chart.removeLegend();
		
//	plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
//	plot.setNoDataMessage("No data available");

		if (param.subtitle != null)
			chart.addSubtitle(new TextTitle(param.subtitle));
		
		String fileExt = filenameExtension(filename);
		if (fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("jpeg"))
			ChartUtilities.saveChartAsJPEG(new File(filename), chart, x, y);		
		else
			ChartUtilities.saveChartAsPNG(new File(filename), chart, x, y);
		
	}
	
	private static String filenameExtension(String filename)
	{
		int dotIndex = filename.lastIndexOf('.');
		if (dotIndex >= 0) {
			return filename.substring(dotIndex + 1);
		}
		return "";
	}
	
	public abstract JFreeChart createChart() throws Exception;

	/*
	public static int findColumnIndex(Table tbl, String colName, boolean chooseNumeric, int offset)
	{
	  RowSchema schema = tbl.getRowList().getSchema();
	  // first try direct col name match
	  int colIndex = schema.getColIndex(colName);
	  if (colIndex >= 0) return colIndex;
	  
	  // next try finding a column of appropriate type (string or numeric)
	  for ()
	  
	}
	*/
	
  protected static int findColumnIndex(Table tbl, String colName)
  {
     RowSchema schema = tbl.getRows().getSchema();
     return schema.getColIndexIgnoreCase(colName);
  }
  
  protected static int findColumnIndex(Table tbl, String colName, String colName2, int defaultIndex)
  {
     RowSchema schema = tbl.getRows().getSchema();
     int i = schema.getColIndexIgnoreCase(colName);
     if (i == -1) 
       i = schema.getColIndexIgnoreCase(colName2);
     if (i == -1) 
       i = defaultIndex;
     return i;
  }
  
  protected int findRangeIndex(Table tbl, String colName, String colName2, int xIndex)
  {
    int index = findColumnIndex(tbl, colName, colName2, -1);
    if (index >= 0) return index;
    // may still return -1, which means no applicable column
    return SchemaUtil.getNumericColumn(tbl.getRows().getSchema(), xIndex);
  }
  

}

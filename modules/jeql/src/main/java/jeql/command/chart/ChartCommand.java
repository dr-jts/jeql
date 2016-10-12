package jeql.command.chart;


import java.util.Arrays;

import jeql.api.annotation.Metadata;
import jeql.api.command.Command;
import jeql.api.error.IllegalValueException;
import jeql.api.table.Table;
import jeql.engine.Scope;

@Metadata (description = "Generate charts for datasets")
public class ChartCommand 
implements Command 
{
	String filename = "chart.png";
	String chartType = BaseChart.CHART_TYPE_BAR;
	private ChartParameters param = new ChartParameters();
	private int width = 500;
	private int height = 500;

	public void setFile(String filename) {
		this.filename = filename;
	}
	
	public void setXAxisTitle(String axisLabelX) {
		param.setAxisXTitle(axisLabelX);
	}

	public void setYAxisTitle(String axisLabelY) {
		param.setAxisYTitle(axisLabelY);
	}

	/**
	 * 0 is horizontal
	 * 0.5 is at 45 degrees
	 * 1 is 90 vertical
	 * -0.5 is at -45 degrees
	 * -1 is -1 90 vertical
	 * 
	 * @param angle
	 */
	public void setXAxisLabelRotation(double rot)
	{
		param.setAxisXLabelRotation(rot);
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	
	public void setExtrude(boolean extrude)
	{
		param.setExtrude(extrude);
	}

	public void setOrientation(String strOrientation) {
		param.setOrientation(strOrientation);
	}

	public void setShowItemLabels(boolean showItemLabels)
	{
		param.setShowItemLabels(showItemLabels);
	}
	public void setItemLabelRotation(double itemLabelAngle)
	{
		param.setItemLabelRotation(itemLabelAngle);
	}
        public void setSubtitle(String subtitle) {
          param.setSubtitle(subtitle);
  }
        @Metadata(description = "Show legend (default = true)")
        public void setLegend(boolean showLegend) {
          param.setLegend(showLegend);
  }

	public void setTitle(String title) {
		param.setTitle(title);
	}

	@Metadata (
		description = "Type of chart",
		values = {
				BaseChart.CHART_TYPE_BAR, 
				BaseChart.CHART_TYPE_BAR_STACKED, 
				BaseChart.CHART_TYPE_LINE,
				BaseChart.CHART_TYPE_PIE, 
				BaseChart.CHART_TYPE_XY, 
				BaseChart.CHART_TYPE_XY_STEP, 
				BaseChart.CHART_TYPE_POLAR
			}
	)
	public void setType(String chartTypeStr) 
	{
		String chartType = BaseChart.getChartType(chartTypeStr);
		if (chartType == null)
			throw new IllegalValueException("'" + chartTypeStr + "' is not a valid chart type - "
			    + Arrays.toString(BaseChart.chartTypes));
		param.setType(chartType);
		
	}

	@Metadata (description = "Dataset to display on chart (schema can have columns x, y)")
  public void setDefault(Table dataTbl) 
  {
    param.setSeries(0, dataTbl);
  }

  @Metadata (description = "Dataset to display on chart (schema can have columns x, y)", isMultiple = true)
  public void setData(Table dataTbl) 
  {
    param.addSeries(dataTbl);
  }

  @Metadata (description = "Dataset name.  Must appear BEFORE data: parameter", isMultiple = true)
  public void setName(String name) {
    param.addSeriesKey(name);
  }

  public void setBackground(String color)
  {
    param.setBackground(color);
  }
  
  public void setColor(String color)
  {
    param.setColor(color);
  }
  
	public void setFill(boolean isFilled)
	{
		param.setFill(isFilled);
	}
	
	public void execute(Scope scope) throws Exception 
	{
		BaseChart baseChart = BaseChart.createChartObject(param.chartType);
		baseChart.setParameters(param);
		baseChart.writeChart(filename, width, height);
	}
	

}

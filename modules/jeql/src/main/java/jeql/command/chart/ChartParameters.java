package jeql.command.chart;

import java.awt.Color;

import jeql.api.table.Table;
import jeql.util.ColorUtil;

import org.jfree.chart.plot.PlotOrientation;

public class ChartParameters 
{
  public static final String ORIENTATION_VERTICAL = "vertical";
  public static final String ORIENTATION_HORIZONTAL = "horizontal";
  
  Color backgroundPaint;
  Color color;
	String axisXTitle = "";
	String axisYTitle = "";
	double axisXLabelRotation = 0;
	String chartType = BaseChart.CHART_TYPE_BAR;
	boolean isExtruded = false;
	String subtitle = null;
	String title = null;
	boolean showItemLabels = false;
	double itemLabelRotation = 0.0;
	boolean showLegend = true;
	PlotOrientation plotOrientation = PlotOrientation.VERTICAL;
	int seriesIndex = 0;
	Table[] seriesTables = new Table[10];
	String[] seriesKey = new String[10];
	boolean isFilled;

	public void setBackground(String backgroundClr)
	{
	  this.backgroundPaint = ColorUtil.RGBAtoColor(backgroundClr);
	}
	
  public void setColor(String color) {
    this.color = ColorUtil.RGBAtoColor(color);
  }

	public void setAxisXTitle(String axisLabelX) {
		this.axisXTitle = axisLabelX;
	}

	public void setAxisYTitle(String axisLabelY) {
		this.axisYTitle = axisLabelY;
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
	public void setAxisXLabelRotation(double rot)
	{
		axisXLabelRotation = rot;
	}

	public void setExtrude(boolean isExtruded)
	{
		this.isExtruded = isExtruded;
	}

	public void setShowItemLabels(boolean showItemLabels)
	{
		this.showItemLabels = showItemLabels;
	}

	public void setItemLabelRotation(double itemLabelRotation)
	{
		double rot = itemLabelRotation;
		if (rot > 1.0) rot = 1.0;
		if (rot < -1.0) rot = -1.0;
		this.itemLabelRotation = rot;
	}
        public void setLegend(boolean showLegend) {
          this.showLegend = showLegend;
  }
        public void setSubtitle(String subtitle) {
          this.subtitle = subtitle;
  }
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setType(String chartType) {
		this.chartType = chartType;
	}
	
	public void setFill(boolean isFilled) {
		this.isFilled = isFilled;
	}
	
	public void setOrientation(String strOrientation)
	{
		if (strOrientation.equalsIgnoreCase(ORIENTATION_VERTICAL))
			plotOrientation = PlotOrientation.VERTICAL;
		else if (strOrientation.equalsIgnoreCase(ORIENTATION_HORIZONTAL))
			plotOrientation = PlotOrientation.HORIZONTAL;
	}
	
  public void addSeries(Table dataTbl)
  {
    seriesTables[seriesIndex++] = dataTbl;
  }

  public void addSeriesKey(String key)
  {
    seriesKey[seriesIndex] = key;
  }
  
  public void setSeries(int dataIndex, Table dataTbl)
  {
    seriesTables[dataIndex] = dataTbl;
  }

	public void setSeriesKey(int dataIndex, String key)
	{
		seriesKey[dataIndex] = key;
	}
	
	public Table getSeries(int index)
	{
		return seriesTables[index];
	}
	
	public String getSeriesKey(int index)
	{
		if (seriesKey[index]  != null)
			return seriesKey[index];
		if (index == 0 && axisYTitle != null)
			return axisYTitle;
		return "Series " + (index + 1);
	}
	
	public int getNumSeries()
	{
	  return seriesIndex;
	}
	
	public Table getFirstDataTable()
	{
		for (int i = 0; i < seriesTables.length; i++ ) {
			Table tbl = seriesTables[i];
			if (tbl != null)
				return tbl;
		}
		return null;
	}
	
	public String getChartType()
	{
		return chartType;
	}

}

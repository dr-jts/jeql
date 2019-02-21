package jeql.command.plot;

import jeql.api.command.Command;
import jeql.api.table.Table;
import jeql.engine.Scope;

import org.locationtech.jts.geom.Geometry;

public class PlotCommand 
implements Command 
{
  Plot plot;
  Plotter plotter;
  String filename = "plot.png";
  
  public PlotCommand() {
    super();
  }

  public void setWidth(int width)
  {
    getPlot().setWidth(width);
  }
  
  public void setHeight(int height)
  {
    getPlot().setHeight(height);
  }
  
  public void setSize(int size)
  {
    setWidth(size);
    setHeight(size);
  }
  
  public void setExtent(Geometry g)
  {
    getPlot().setExtent(g.getEnvelopeInternal());
  }
  
  public void setBackground(String color)
  {
    getPlot().setBackground(color);
  }
  
  public void setBorderSize(int size)
  {
    getPlot().setBorderSize(size);
  }
  
  public void setBorderColor(String color)
  {
    getPlot().setBorderColor(color);
  }
  
  public void setDefault(Table dataTbl) 
  {
	getPlotter().addData(dataTbl);
  }

  public void setData(Table tbl)
  {
    getPlotter().addData(tbl);
  }
  
  /*
  public void setLabels(Table tbl)
  {
    getPlotter().addLabels(tbl);
  }
  */
  
  public void setFile(String filename)
  {
    this.filename = filename;
  }
  
  public void execute(Scope scope) throws Exception 
  {
    getPlotter().plot();
    getPlot().write(filename);
  }

  private Plotter getPlotter()
  {
    init();
    return plotter;
  }
  
  private Plot getPlot()
  {
    init();
    return plot;
  }
  
  private void init()
  {
    if (plot == null) {
      plot = new Plot();
      plotter = new Plotter(plot);
    }

  }
}

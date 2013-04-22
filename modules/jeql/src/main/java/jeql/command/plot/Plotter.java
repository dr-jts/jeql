package jeql.command.plot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jeql.api.table.Table;

public class Plotter 
{
  private Plot plot;
  
  private List dataTables = new ArrayList();
  //private List labelTables = new ArrayList();
  
  public Plotter(Plot plot) {
    this.plot = plot;
  }

  public void addData(Table tbl)
  {
    dataTables.add(tbl);
  }
    
  public void plot()
  throws IOException
  {
      plot.init();
      DataPlotter.plot(plot, dataTables);
      LabelPlotter.plot(plot, dataTables);
  }
  

}

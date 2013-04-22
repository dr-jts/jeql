package jeql.command.io;

import jeql.api.table.Table;
import jeql.io.shapefile.ShapefileWriter;

public class ShapefileWriterCommand 
  extends TableFileWriterCmd
{
  private String prj = null;
  
  public ShapefileWriterCommand() {
  }
  
  public void setPrj(String prj)
  {
    this.prj = prj;
  }
  

  public void write(Table tbl)
    throws Exception
  {
    ShapefileWriter writer = new ShapefileWriter();
    writer.write(tbl, filename, prj);
  }
  
}

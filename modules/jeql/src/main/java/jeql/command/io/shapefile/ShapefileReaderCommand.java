package jeql.command.io.shapefile;

import jeql.api.table.Table;
import jeql.command.io.TableFileReaderCmd;
import jeql.engine.Scope;
import jeql.io.InputSource;
import jeql.io.shapefile.ShapefileRowList;


public class ShapefileReaderCommand 
  extends TableFileReaderCmd
{
  private boolean readDBF = true;
  
  public ShapefileReaderCommand() {
  }

  public void setNoDBF(boolean noDBF)
  {
    readDBF = ! noDBF;
  }
  
  public Table read(Scope scope, InputSource src)
    throws Exception
  {
    ShapefileRowList sis = new  ShapefileRowList(src.getSourceName(), readDBF);
    tbl = new Table(sis);
    return tbl;
  }
}


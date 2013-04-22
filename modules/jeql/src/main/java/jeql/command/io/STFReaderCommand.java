package jeql.command.io;

import java.io.IOException;

import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;

public class STFReaderCommand 
  extends TextFileReaderCmd
{
  public STFReaderCommand() {
  }
  
  public Table read(Scope scope, InputSource src)
    throws IOException
  {
    RowList rs = new STFRowList(src.getSourceName());
    tbl = new Table(rs);
    return tbl;
  }
  
}

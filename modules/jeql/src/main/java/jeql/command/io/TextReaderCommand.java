package jeql.command.io;

import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;


public class TextReaderCommand 
  extends TextFileReaderCmd
{

  public TextReaderCommand() {
  }

  public Table read(Scope scope, InputSource src)
  {
    RowList rs = new TextFileRowList(src);
    tbl = new Table(rs);
    return tbl;
  }
  
}

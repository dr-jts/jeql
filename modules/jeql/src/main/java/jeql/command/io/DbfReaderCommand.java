package jeql.command.io;

import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;
import jeql.io.dbf.DbfRowList;

public class DbfReaderCommand 
  extends TableFileReaderCmd
{
  public DbfReaderCommand() {
  }

  public Table read(Scope scope, InputSource src)
    throws Exception
  {
    DbfRowList sis = new  DbfRowList(src.getSourceName());
    tbl = new Table(sis);
    return tbl;
  }
}


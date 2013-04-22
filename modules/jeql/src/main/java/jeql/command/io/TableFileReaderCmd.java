package jeql.command.io;

import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;

public abstract class TableFileReaderCmd
implements TableReaderCmd
{
  protected String filename;
  protected Table tbl;

  public void setFile(String filename)
  {
    this.filename = filename;
  }
  
  public Table getTable()
  {
    return tbl;
  }
  
  public Table getDefault()
  {
    return getTable();
  }

  public void execute(Scope scope) throws Exception
  {
    tbl = read(scope, new InputSource(filename));
  }
  
  protected abstract Table read(Scope scope, InputSource src) throws Exception;
}

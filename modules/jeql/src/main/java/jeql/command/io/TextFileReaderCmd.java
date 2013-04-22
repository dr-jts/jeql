package jeql.command.io;

import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;

public abstract class TextFileReaderCmd
extends TableFileReaderCmd
{
  private String data = null;
  
  public void setData(String data)
  {
    this.data = data;
  }
  
  public void execute(Scope scope) throws Exception
  {
    if (data != null) {
      tbl = read(scope, new InputSource(InputSource.PROTOCOL_STRING, data));
    }
    else {
      super.execute(scope);
    }
  }
  
  protected abstract Table read(Scope scope, InputSource src) throws Exception;
}

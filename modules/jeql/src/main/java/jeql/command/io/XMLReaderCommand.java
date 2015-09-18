package jeql.command.io;

import java.io.IOException;

import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;

public class XMLReaderCommand 
extends TextFileReaderCmd
{
  private boolean includeAllEndElements = false;

  public XMLReaderCommand() {
    super();
  }

  public void setAllEndElements(boolean includeAllEndElements) {
    this.includeAllEndElements = includeAllEndElements;
  }
  
  protected Table read(Scope scope, InputSource src)
  throws IOException
  {
    RowList rs = new XmlRowList(src, includeAllEndElements );
    tbl = new Table(rs);
    return tbl;
  }

}

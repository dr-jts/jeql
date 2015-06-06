package jeql.command.io;

import java.io.IOException;

import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;

public class XMLReaderCommand 
extends TextFileReaderCmd
{

  public XMLReaderCommand() {
    super();
  }

  protected Table read(Scope scope, InputSource src)
  throws IOException
  {
    RowList rs = new XmlRowList(src);
    tbl = new Table(rs);
    return tbl;
  }

}

package jeql.command.util;

import jeql.api.annotation.Metadata;
import jeql.api.command.Command;
import jeql.api.row.ArrayRowList;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;

@Metadata (
  description = "Materializes a table in memory"
)
public class MemCommand 
implements Command
{
  private int limit = -1;
  private Table inputTbl;
  private Table memTbl;
  
  public MemCommand() {
  }

  public void setDefault(Table inputTbl)
  {
   this.inputTbl = inputTbl;  
  }
  
  @Metadata (
      description = "Table to memorize",
      isRequired = true
    )
  public Table getDefault()
  {
   return memTbl;  
  }
  
  @Metadata (
      description = "Limits the number of rows memorized"
    )
  public void setLimit(int limit)
  {
   this.limit = limit;  
  }
  
  @Metadata (
      description = "SELECT expression to memorize"
    )
  public void setAs(Table inputTbl)
  {
    this.inputTbl = inputTbl;  
  }
  
  public void execute(Scope scope)
  {
    RowList memRowList = null;
    if (limit < 0)
      memRowList = new ArrayRowList(inputTbl.getRows().iterator());
    else {
      memRowList = copyLimited(inputTbl.getRows(), limit);
    }
    memTbl = new Table(memRowList);
  }

  private RowList copyLimited(RowList rowList, int limit)
  {
    int count = 0;
    ArrayRowList memRowList = new ArrayRowList(rowList.getSchema());
    RowIterator rowIt = rowList.iterator();
    while (true) {
      Row row = rowIt.next();
      if (row == null) break;
      memRowList.add(row);
      count++;
      if (count >= limit) break;
    }
    return memRowList;
  }
}

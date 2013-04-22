package jeql.command;

import jeql.api.command.Command;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.Scope;

public abstract class FilterCommand 
implements Command
{
  private Table inputTbl;
  private Table outTbl;
  
  public FilterCommand() 
  {
  }

  public void setDefault(Table inputTbl)
  {
   this.inputTbl = inputTbl;  
  }
  
  public Table getDefault()
  {
   return outTbl;  
  }
  
  public void execute(Scope scope)
  {
    outTbl = new Table(new FilteredRowList());
  }

  protected abstract RowIterator createFilteredRowIterator(RowIterator rowIt);
  
  private class FilteredRowList 
  implements RowList
  {
    public FilteredRowList()
    {
    }
    
    public RowSchema getSchema()
    {
      return inputTbl.getRows().getSchema();
    }
    
    public RowIterator iterator()
    {
      return createFilteredRowIterator(inputTbl.getRows().iterator());
    }
  }
  
  protected class FilteredRowIterator
  implements RowIterator
  {
    protected RowSchema schema;
    private RowIterator rowIt;
    
    public FilteredRowIterator(RowIterator rowIt)
    {
      this.rowIt = rowIt;
      this.schema = rowIt.getSchema();
    }
    
    public RowSchema getSchema()
    {
      return schema;
    }
    
    public Row next()
    {
      Row row = rowIt.next();
      if (row == null)
        return null;
      return transform(row);
    }
    
    protected Row transform(Row row)
    {
      // default behaviour is to simply copy the input
      return new BasicRow(row);
    }

  }
}

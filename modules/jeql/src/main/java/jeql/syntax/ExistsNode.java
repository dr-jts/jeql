package jeql.syntax;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;

public class ExistsNode 
  extends ParseTreeNode
{
  private SelectNode sel;
  
  public ExistsNode(SelectNode sel) 
  {
    this.sel = sel;
  }

  public Class getType(Scope scope)
  {
      return Boolean.class;
  }
  
  public void bind(Scope scope)
  {
    sel.bind(scope);
  }

  public Object eval(Scope scope)
  {
    Table tbl = (Table) sel.eval(scope);
    RowList rows = tbl.getRows();
    boolean rowsExist = ! isEmpty(rows);
    return new Boolean(rowsExist);
  }
  
  private static boolean isEmpty(RowList rows)
  {
    RowIterator it = rows.iterator();
    Row row = it.next();
    if (row == null)
      return true;
    return false;
  }

}

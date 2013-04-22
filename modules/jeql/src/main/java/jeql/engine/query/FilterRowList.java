package jeql.engine.query;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.syntax.ParseTreeNode;

public class FilterRowList 
  implements RowList
{
  private RowList rowStr;
  private ParseTreeNode filterExpr;
  private QueryScope scope;
  
  public FilterRowList(RowList rowStr, ParseTreeNode filterExpr, QueryScope scope) 
  {
    this.rowStr = rowStr;
    this.filterExpr = filterExpr;
    this.scope = scope;
  }

  public RowSchema getSchema() { return rowStr.getSchema(); }

  public RowIterator iterator()
  {
    return new FilterRowIterator(rowStr, filterExpr, scope);
  }
  
  private static class FilterRowIterator implements RowIterator {
    private RowSchema schema;
    private ParseTreeNode filterExpr;
    private QueryScope scope;
    private RowIterator it;
    private int rowNum = 0;

    public FilterRowIterator(RowList rowList,
        ParseTreeNode filterExpr, QueryScope scope) {
      schema = rowList.getSchema();
      this.filterExpr = filterExpr;
      this.scope = scope;
      it = rowList.iterator();
      rowNum = 0;
    }

    public RowSchema getSchema() {
      return schema;
    }

    public Row next() {
      
      // short-circuit if no filter expr
      if (filterExpr == null) {
        rowNum++;
        // set rownum for use in where clause expressions
        scope.setRowNum(rowNum);
        return it.next();
      }
      
      while (true) {
        rowNum++;
        // set rownum for use in where clause expressions
        scope.setRowNum(rowNum);
        Row row = it.next();
        if (row == null)
          return null;
        if (isAccepted(row))
          return row;
      }
    }

    private boolean isAccepted(Row row) {
      if (filterExpr == null)
        return true;
      ((QueryScope) scope).setRow(row);
      boolean isAccepted = ((Boolean) filterExpr.eval(scope)).booleanValue();
      return isAccepted;
    }
  }
}

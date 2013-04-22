package jeql.engine.query;

import java.util.List;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.syntax.ParseTreeNode;

public class SplitRowList 
  implements RowList
{
  private RowList rowStr;
  private ParseTreeNode splitExpr;
  private QueryScope scope;
  
  public SplitRowList(RowList rowStr, ParseTreeNode splitExpr, QueryScope scope) 
  {
    this.rowStr = rowStr;
    this.splitExpr = splitExpr;
    this.scope = scope;
  }

  public RowSchema getSchema() { return rowStr.getSchema(); }

  public RowIterator iterator()
  {
    return new SplitRowIterator(rowStr, splitExpr, scope);
  }
  
  private static class SplitRowIterator implements RowIterator {
    private RowSchema schema;
    private ParseTreeNode splitExpr;
    private QueryScope scope;
    private RowIterator it;
    private Row currRow;
    private List splitValues = null;
    private int splitIndex = 0;

    public SplitRowIterator(RowList rowList,
        ParseTreeNode splitExpr, QueryScope scope) {
      schema = rowList.getSchema();
      this.splitExpr = splitExpr;
      this.scope = scope;
      it = rowList.iterator();
    }

    public RowSchema getSchema() {
      return schema;
    }

    public Row next() 
    {
      if (splitValues != null 
          && splitIndex < splitValues.size()) {
        return nextSplitRow();
      }
      splitValues = null;
      splitIndex = 0;
      // get new row with non-null splitValues
      while (true) {
        currRow = it.next();
        if (currRow == null)
          return null;
        
        ((QueryScope) scope).setRow(currRow);
      
        splitValues = ((List) splitExpr.eval(scope));
        //if (splitValues != null) {
        if (splitValues != null && ! splitValues.isEmpty()) {
          return nextSplitRow();          
        }
      }
    }

    private Row nextSplitRow()
    {
      Row splitRow = new SplitRow(currRow, 
          splitValues.get(splitIndex), 
          splitIndex);
      splitIndex++;
      return splitRow;
    }
    
  }
}

package jeql.engine.query.join;

import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.engine.query.QueryScope;
import jeql.syntax.FromItem;
import jeql.syntax.ParseTreeNode;

public class NestedLoopJoinRowList 
  implements RowList
{
  private RowSchema schema = null;
  private RowList[] rowStr = new RowList[2];
  private ParseTreeNode joinConditionExpr;
  private int joinType = FromItem.JOIN_INNER;
  private QueryScope scope;

  public NestedLoopJoinRowList(RowList rs0, RowList rs1, 
      ParseTreeNode joinConditionExpr,
      int joinType,
      QueryScope scope) 
  {
    this.joinConditionExpr = joinConditionExpr;
    this.joinType = joinType;
    this.scope = scope;
    rowStr = new RowList[] { rs0, rs1 };
    schema = new RowSchema(rs0.getSchema(), rs1.getSchema());
  }

  public RowSchema getSchema() { return schema; }

  public RowIterator iterator()
  {
    NestedLoopJoinRowIterator loopIt = new NestedLoopJoinRowIterator(schema, rowStr, joinType);
    JoinCondFilterRowIterator joinCondIt =  new JoinCondFilterRowIterator(schema, loopIt, joinConditionExpr, scope);
    return joinCondIt;
  }
  
  private static class NestedLoopJoinRowIterator implements RowIterator {
    private RowSchema schema;
    private RowList[] rowStr;
    private RowIterator[] rowIt;
    private Row row0 = null;
    private Row row1 = null;
    private boolean isCurrentLeftRowIncluded = false;
    private boolean isLeftOuterJoin = false;
    private boolean applyFilter = true;

    public NestedLoopJoinRowIterator(RowSchema schema, RowList[] rowStr, int joinType) {
      if (joinType == FromItem.JOIN_LEFT_OUTER)
        isLeftOuterJoin = true;
      this.schema = schema;
      this.rowStr = rowStr;
      rowIt = new RowIterator[rowStr.length];
      rowIt[0] = rowStr[0].iterator();
      rowIt[1] = rowStr[1].iterator();
    }

    public RowSchema getSchema() {
      return schema;
    }

    public Row next() 
    {
      // initialize loops state
      if (joinRow == null) { // just started
        // get first rows in each stream
        row0 = rowIt[0].next();
        if (row0 == null)
          return null;
        row1 = rowIt[1].next();
        if (row1 == null) {
          if (isLeftOuterJoin && ! isCurrentLeftRowIncluded) {
            applyFilter = false;
            isCurrentLeftRowIncluded = true;
            return createRow(row0, null);
          }
          return null;
        }
        
        return createRow(row0, row1);
      }

      // here can assume that both tables are non-empty (otherwise would have returned above)

      // inner loop (second table)
      row1 = rowIt[1].next();
      if (row1 != null) {
        return createRow(row0, row1);
      }

      // outer loop (first (driving) table)
      
      // if current left row is not yet included and this is a LEFT OUTER JOIN, include it
      if (isLeftOuterJoin && ! isCurrentLeftRowIncluded) {
        applyFilter = false;
        isCurrentLeftRowIncluded = true;
        return createRow(row0, null);
      }
      
      row0 = rowIt[0].next();
      isCurrentLeftRowIncluded = false;
      applyFilter = true;
      if (row0 != null) {
        rowIt[1] = rowStr[1].iterator();
        row1 = rowIt[1].next();
        return createRow(row0, row1);
      }

      return null;
    }
    
    public boolean applyFilter()
    {
      if (! applyFilter) {
        applyFilter = true;
        return false;
      }
      return true;
    }
    
    public void rowAccepted()
    {
      isCurrentLeftRowIncluded = true;
    }
    
    private BasicRow joinRow = null;

    private Row createRow(Row row0, Row row1) {
      joinRow = new BasicRow(schema.size());
      joinRow.copyTo(0, row0);
      if (row1 != null)
        joinRow.copyTo(row0.size(), row1);
      else
        joinRow.setColumnsToNull(row0.size(), joinRow.size());
      return joinRow;
    }
  }
  
  private static class JoinCondFilterRowIterator implements RowIterator {
    private RowSchema schema;
    private ParseTreeNode condExpr;
    private QueryScope scope;
    private NestedLoopJoinRowIterator rowIt;
    private int rowNum = 0;

    public JoinCondFilterRowIterator(RowSchema schema, 
        NestedLoopJoinRowIterator rowIt,
        ParseTreeNode condExpr, 
        QueryScope scope) {
      this.schema = schema;
      this.condExpr = condExpr;
      this.scope = scope;
      this.rowIt = rowIt;
      rowNum = 0;
    }

    public RowSchema getSchema() {
      return schema;
    }

    public Row next() {
      
      // short-circuit if no filter expr
      if (condExpr == null) {
        rowNum++;
        // set rownum for use in where clause expressions
        scope.setRowNum(rowNum);
        return rowIt.next();
      }
      
      // skip to next accepted row (if any)
      while (true) {
        rowNum++;
        // set rownum for use in where clause expressions
        scope.setRowNum(rowNum);
        
        Row row = rowIt.next();
        if (row == null)
          return null;
        
        // don't filter rows included for outer join
        if (! rowIt.applyFilter())
          return row;
        
        if (isAccepted(row)) {
          rowIt.rowAccepted();
          return row;
        }
      }
    }

    private boolean isAccepted(Row row) {
      if (condExpr == null)
        return true;
      ((QueryScope) scope).setRow(row);
      boolean isAccepted = ((Boolean) condExpr.eval(scope)).booleanValue();
      return isAccepted;
    }
  }
}

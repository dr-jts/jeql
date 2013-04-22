package jeql.syntax;

import java.util.Iterator;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.util.TypeUtil;

public class InNode 
  extends ParseTreeNode
{
  private static String ERROR_DIFFERENT_TYPEs = "LHS and RHS of IN are of different types";
    
  private ParseTreeNode lhs;
  private SelectNode sel;
  private List exprList;
  private boolean isNegated;
  
  public InNode(ParseTreeNode lhs, SelectNode sel, boolean isNegated) 
  {
    this.lhs = lhs;
    this.sel = sel;
    this.isNegated = isNegated;
    setLoc(lhs);
  }

  public InNode(ParseTreeNode lhs, List exprList, boolean isNegated) 
  {
    this.lhs = lhs;
    this.exprList = exprList;
    this.isNegated = isNegated;
    setLoc(lhs);
  }

  public Class getType(Scope scope)
  {
      return Boolean.class;
  }
  
  public void bind(Scope scope)
  {
    lhs.bind(scope);
    
    if (sel != null) 
      sel.bind(scope);
    else {
      for (Iterator i = exprList.iterator(); i.hasNext(); ) {
        ParseTreeNode node = (ParseTreeNode) i.next();
        node.bind(scope);
      }
    }
  }

  public Object eval(Scope scope)
  {
    Object lhsVal = lhs.eval(scope);
    boolean result;
    if (sel != null) {
      result = evalInSelect(lhsVal, scope);
    }
    else {
      result = evalInExprList(lhsVal, scope);
    }
    if (isNegated) 
      result = ! result;
    return new Boolean(result);
  }
  
  private boolean evalInSelect(Object lhsVal, Scope scope)
  {
    Table tbl = (Table) sel.eval(scope);
    RowList rows = tbl.getRows();
    RowSchema schema = rows.getSchema();
    if (schema.size() != 1)
      throw new ExecutionException(this, "Table on rhs of IN must have single column");
    checkSameTypes(lhsVal.getClass(), schema.getType(0));

    RowIterator i = rows.iterator();
    while (true) {
      Row row = i.next();
      if (row == null) break;
      Object rowVal = row.getValue(0);
      if (TypeUtil.isEqual(lhsVal, rowVal)) {
        return true;
      }      
    }
    return false;
  }
  
  private boolean evalInExprList(Object lhsVal, Scope scope)
  {
    for (Iterator i = exprList.iterator(); i.hasNext(); ) {
      ParseTreeNode node = (ParseTreeNode) i.next();
      Object eVal = node.eval(scope);
      checkSameTypes(lhsVal, eVal);
      if (TypeUtil.isEqual(lhsVal, eVal)) {
        return true;
      }
    }
    return false;
  }
  
  private void checkSameTypes(Object lhsVal, Object rhsVal)
  {
    if (! TypeUtil.isSameType(lhsVal, rhsVal)) {
      throw new ExecutionException(this, ERROR_DIFFERENT_TYPEs);
    }
  }
}

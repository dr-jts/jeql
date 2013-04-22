package jeql.syntax;

import jeql.api.error.ExecutionException;
import jeql.api.table.Table;
import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.engine.UndefinedVariableException;
import jeql.engine.query.QueryScope;
import jeql.engine.query.group.GroupScope;
import jeql.util.StringUtil;

/**
 *
 * @author Martin Davis
 * @version 1.0
 */
public class TableColumnNode
  extends ParseTreeNode
{
  private String table = null;
  private String col = null;
  private boolean isColumn = false;
  private int colIndex = -1;
  /**
   * A correlated column references 
   * the value of a column in one
   * of the tables in a FROM clause
   */
  private boolean isCorrelatedCol = false;
  private Class correlatedVarType = null;
  private Object correlatedVarValue = null;
  private boolean checkUndefinedVariables = true;
  
  /**
   * Models <tt>column</tt> specifier.
   *
   * @param table
   */
  public TableColumnNode(String col) {
    this.col = col;
    table = null;
  }

  /**
   * Models <tt>tbl.column</tt> syntax.
   *
   * @param table
   */
  public TableColumnNode(String table, String col) 
  {
    this.table = table;
    this.col = col;
  }

  public void setCheckUndefinedVariables(boolean checkUndefined)
  {
    this.checkUndefinedVariables = checkUndefined;
  }
  
  /**
   * Gets the specified table name, if present.
   * 
   * @return the table name if present
   * @return null if no table name was specified
   */
  public String getTableName() { return table; }
  
  public String getColName() { return col; }
  
  public boolean isMatch(String tblName, String colName)
  {
    return StringUtil.isEqualOrBothNull(tblName, table)
    && col.equals(colName);
  }
  
  private boolean isColumn()
  {
    if (colIndex >= 0) return true;
    if (correlatedVarType != null) return true;
    return false;
  }
  
  public void bind(Scope scope)
  {
    // reset state in case this is a node in an inner select
    correlatedVarType = null;
    correlatedVarValue = null;
    isColumn = false;
    colIndex = -1;
    
    if (scope instanceof QueryScope) {
      colIndex = ((QueryScope) scope).getColumnIndex(table, col);      
    }
    if (colIndex >= 0) {
      isColumn = true;
      return;
    }
    
    // check for correlated column
    if (scope instanceof QueryScope) {
      QueryScope colScope = findColumnScope((QueryScope) scope, table, col);      
      // if not a column in current scope, check if it is a correlated variable from a parent query scope
      if (colScope != null) {
        findCorrelatedValue((QueryScope) scope, table, col);
        if (isColumn()) {
          isColumn = true;
          return;
        }
      }
    }
    
    // only need this check if variables are required to be defined before use 
    if (! isColumn && checkUndefinedVariables) {
      if (table != null) {
        String msg = "Table " + table + " is not defined (in correlated variable " 
        + table + "." + col + ")";
        
        Table t = null;
        try {
          t = ((QueryScope) scope).resolveTable(table);
        }
        catch (UndefinedVariableException e) {
          throw new ExecutionException(this, msg);
        }
        
        if (t != null) {
          msg = "Column '" + col + "' is not defined in table " + table;
        }
        throw new ExecutionException(this, msg);
      }
      if (! scope.hasVariable(col)) {
        String scopeDesc = "";
        if (scope instanceof GroupScope)
          scopeDesc = " in GROUP scope";
        String msg = "Variable '" + col + "' is not defined" + scopeDesc; 
        throw new ExecutionException(this, msg);
      }
    }
  }

  /*
  public void OLDbind(Scope scope)
  {
    //correlatedValue = null;
    if (scope instanceof QueryScope) {
      colIndex = ((QueryScope) scope).getColumnIndex(table, col);      
    }
    isColumn = colIndex >= 0;
    
    // only need this check if variables are required to be defined before use 
    if (! isColumn) {
      if (! scope.hasVariable(col)) {
        String scopeDesc = "";
        if (scope instanceof GroupScope)
          scopeDesc = " in GROUP scope";
        String msg = "Variable " + col + " is not defined" + scopeDesc; 
        throw new CompilationException(this, msg);
      }
    }
  }
*/
  
  /**
   * 
   * @param table
   * @param col
   * @return the value of the correlated column, if any
   * @return null if this column is not defined in any parent query scope
   */
  private QueryScope findColumnScope(Scope scope, String tblName, String col)
  {
    while (scope instanceof QueryScope) {
      QueryScope qs = (QueryScope) scope;
      int colIndex = qs.getColumnIndex(tblName, col);
      if (colIndex >= 0) {
        return qs;
      }
      scope = scope.getParent();
    }
    return null;
  }
  
  /**
   * Finds this column in a parent QueryScope (if any),
   * and if found records the value and type. 
   * If found this column is a correlated column, 
   * and the value and type are fixed for the
   * evaluation of the query in which this column node occurs
   * 
   * @param table
   * @param col
   * @return the value of the correlated column, if any
   * @return null if this column is not defined in any parent query scope
   */
  private void findCorrelatedValue(QueryScope scope, String tblName, String col)
  {
    Scope parentScope = scope.getParent();
    while (parentScope instanceof QueryScope) {
      QueryScope qs = (QueryScope) parentScope;
      int colIndex = qs.getColumnIndex(tblName, col);
      if (colIndex >= 0) {
        isCorrelatedCol = true;
        correlatedVarType = qs.getColumnType(colIndex);
        correlatedVarValue = qs.getColumnValue(colIndex);
      }
      parentScope = parentScope.getParent();
    }
  }
  
  public Class getType(Scope scope)
  {
    if (isColumn) {
      if (colIndex >= 0)
        return ((QueryScope) scope).getColumnType(colIndex);
      
      return correlatedVarType;
    }
    if (scope instanceof QueryScope) {
      QueryScope queryScope = (QueryScope) scope;
      if (queryScope.hasVariable(col)) {
        return queryScope.getVariableType(col);      
      }
    }
    
    // for vars which have been computed outside SELECT, can get type of value
    Object varVal = scope.getVariable(col);
    if (varVal == null) return null;
    return varVal.getClass();
  }
  
  public Object eval(Scope scope)
  {
    if (isColumn) {
      if (colIndex >= 0)
        return ((QueryScope) scope).getRow().getValue(colIndex);
      else
        return correlatedVarValue;
    }
    return scope.getVariable(col);
  }
}
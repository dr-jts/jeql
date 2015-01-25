package jeql.engine.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeql.api.error.ExecutionException;
import jeql.api.error.MissingColumnException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.CompilationException;
import jeql.engine.EngineContext;
import jeql.engine.Scope;
import jeql.syntax.FromItem;
import jeql.syntax.FromList;
import jeql.syntax.FunctionNode;
import jeql.syntax.ParseTreeNode;

/**
 * The Base Query scope introduces all the variable names
 * which are valid in the SELECT expressions.
 * These are provided by the columns in the FROM tables,
 * and the alias definitions
 * 
 * @author Martin Davis
 *
 */
public class BaseQueryScope 
  implements QueryScope
{
  private static final String AMBIGUOUS_TBL_REF = "Ambiguous table reference";
  
  private Scope parentScope;
  private Row currentRow;
  private int rowNum;
  
  // table & column info
  private Map tableMap = new HashMap();
  private Table[] fromTbl; 
  private int[] fromTblOffset;
  private List fromColTypes;
  private String defaultTableName = null;
  private boolean hasDefaultTable = false;
  
  private Map aliasVarMap = new HashMap();
  private Map aliasVarTypeMap = new HashMap();
  
  /**
   * Indicates whether currently binding an expression tree
   * which is an argument to an aggregate function
   */
  private boolean isBindingAggFunction = false;
  
  private List aggFunList = null;
  
  public BaseQueryScope(Scope scope) {
    this.parentScope = scope;    
  }

  public Scope getParent() { return parentScope; }
  
  public EngineContext getContext() { return parentScope.getContext(); }
  
  public Table resolveTable(String name)
  {
    if (tableMap.containsKey(name)) {
      int index = ((Integer) tableMap.get(name)).intValue();
      return fromTbl[index];
    }
    return parentScope.resolveTable(name);
  }

  public boolean hasVariable(String name)
  {
    // built-in variables
    if (name.equalsIgnoreCase(VAR_SPLITVALUE)) {
      return true;
    }
    if (name.equalsIgnoreCase(VAR_SPLITINDEX)) {
      return true;
    }
    //check if this is a column in this scope
    int colIndex = getColumnIndexAnyTable(name);
    if (colIndex != -1)
      return true;
   
    if (aliasVarMap.containsKey(name))
        return true;
    
    return parentScope.hasVariable(name);
  }

  public Object getVariable(String name)
  {
    // built-in variables
    if (name.equalsIgnoreCase(VAR_SPLITVALUE)) {
      return ((SplitRow) currentRow).getSplitValue();
    }
    if (name.equalsIgnoreCase(VAR_SPLITINDEX)) {
      return ((SplitRow) currentRow).getSplitIndex();
    }
    
    if (aliasVarMap.containsKey(name))
      return aliasVarMap.get(name);
    
    //check if this is a column in this scope
    int colIndex = getColumnIndexAnyTable(name);
    if (colIndex != -1)
      return getColumnValue(colIndex);
    
    return parentScope.getVariable(name);
  }

  public void setVariable(String name, Object value)
  {
    aliasVarMap.put(name, value);
  }

  public void setVariableType(String name, Class varType)
  {
    aliasVarTypeMap.put(name, varType);
  }

  public Class getVariableType(String name)
  {
    if (aliasVarMap.containsKey(name))
      return (Class) aliasVarTypeMap.get(name);
    
    //check if this is a column in this scope
    int colIndex = getColumnIndexAnyTable(name);
    if (colIndex != -1)
      return getColumnType(colIndex);
    
    return parentScope.getVariableType(name);
  }

  public Table getFromTable(int index)
  {
    return fromTbl[0];
  }

  public void setRow(Row row)  { currentRow = row;  }
  
  /**
   * Gets the current row at the execution point of this select context.
   * 
   * @return the current row
   */
  public Row getRow() { return currentRow; }
  
  /**
   * Prepares FROM items, binds ON expressions, determines result column types.
   * 
   * @param fromList
   */
  public void prepareFromItems(FromList fromList)
  {
    if (fromList == null || fromList.size() == 0) return;
    
    fromTbl = new Table[fromList.size()];
    fromTblOffset = new int[fromList.size()];
    int colCount = 0;
    for (int i = 0; i < fromList.size(); i++ ) {
      FromItem fromItem = fromList.getItem(i);
      prepareFromItem(fromItem, i);
      
      fromTblOffset[i] = colCount;
      colCount += fromTbl[i].size();
    }
    
    // bind ON expressions
    for (int i = 0; i < fromList.size(); i++ ) {
      FromItem fromItem = fromList.getItem(i);
      ParseTreeNode onExpr = fromItem.getJoinConditionExpr();
      // on expressions are bound in the query scope, not the parent scope
      if (onExpr != null) 
        onExpr.bind(this);
    }

    fromColTypes = findTypes(fromTbl);
  }
  
  private void prepareFromItem(FromItem fromItem, int index)
  {
    // bind table expression (in context of parent scope, not this one)
    fromItem.getTableExpression().bind(parentScope);
    
    // bind names (if any) to index into actual tables
    Integer tblIndex = new Integer(index);
    
    String tblName = fromItem.getTableName();
    if (tblName != null)
      addTableName(tblName, tblIndex);
    String alias = fromItem.getAlias();
    if (alias != null)
      addAliasName(alias, tblIndex);
    // actual table for index
    fromTbl[index] = fromItem.getTable(parentScope);
    // maybe not the best place for this check?
    if (fromTbl[index] == null)
      throw new ExecutionException(fromItem, "Unknown table: " + tblName);
  }
  
  private static List findTypes(Table[] tbl)
  {
    List colTypes = new ArrayList();
    for (int i = 0; i < tbl.length; i++) {
      RowSchema rs = tbl[i].getRows().getSchema();
      for (int j = 0; j < rs.size(); j++) {
        Class colType = rs.getType(j);
        colTypes.add(colType);
      }
    }
    return colTypes;
  }
  
  private void addTableName(String name, Integer index)
  {
    setDefaultTable(name);
      
    /**
     * a table can appear multiple times in a select,
     * but references to it by name will then be ambiguous
     * This condition is flagged by using a sentinel value, 
     * which is checked later (since at this point we don't know
     * if the table will ever be referred to by name rather than alias)
     */
    if (tableMap.containsKey(name))
      tableMap.put(name, AMBIGUOUS_TBL_REF);
    else 
      tableMap.put(name, index);
  }

  private void setDefaultTable(String name)
  {
    if (defaultTableName == null && ! hasDefaultTable) {
      defaultTableName = name;
      hasDefaultTable = true;
      return; 
    }
    hasDefaultTable = false;
  }

   public boolean hasDefaultTable() { return fromTbl != null && fromTbl.length == 1; }
   
   public Table getDefaultTable() 
   { 
     return fromTbl[0];
   }
   
  private void addAliasName(String name, Integer index)
  {
    // Aliases must be unique
    if (tableMap.containsKey(name))
      throw new CompilationException("Dulicate alias name: " + name);
    tableMap.put(name, index);
  }
  
  public boolean hasTable(String tblName)
  {
    return tableMap.containsKey(tblName);
  }
  
  /**
   * Gets the index of a table.column reference in this select context.
   * The index can be used to retrieve the value of the column from 
   * a full row in the {@link RowIterator} generated by the FROM/JOIN specification.
   * 
   * @param tblName the name of the table (may be null)
   * @param colName the name of the column
   * @return
   */
  public int getColumnIndex(String tblName, String colName)
  {
    int colIndex = -1;
    
    if (tblName != null) {
      Integer tblIndex = (Integer) tableMap.get(tblName);
      if (tblIndex == null) {
        // old logic - now left up to client to check this
//        throw new ExecutionException("Unknown table: " + tblName);
        return -1;
      }
      Table table  = fromTbl[tblIndex.intValue()];
      int colTblIndex = table.getColumnIndex(colName);
      if (colTblIndex < 0)
        //throw new MissingColumnException(tblName, colName, null);
        return -1;
      int colIndexOffset = fromTblOffset[tblIndex.intValue()];
      colIndex = colTblIndex + colIndexOffset;
    }
    else {
      colIndex = getColumnIndexAnyTable(colName);
    }
    
//    if (colIndex < 0)
//      throw new ExecutionException("Unknown column: " + colName);
    return colIndex;
  }
  
  public int getColumnIndexAnyTable(String colName)
  {
    // no from table in query
    if (fromTbl == null)
      return -1;
    
    for (int i = 0; i < fromTbl.length; i++) {
      if (! fromTbl[i].hasColumn(colName))
        continue;
      int colIndex = fromTbl[i].getColumnIndex(colName);
      if (colIndex >= 0)
        return colIndex + fromTblOffset[i];
    }
    return -1;
  }
  
  public Object getColumnValue(int colIndex) 
  { 
    return currentRow.getValue(colIndex);
  }
  
  public Class getColumnType(int colIndex)
  {
    return (Class) fromColTypes.get(colIndex);
  }
  
  private Map valMap = new HashMap();
  
  public boolean hasValue(Object key)
  {
    return valMap.containsKey(key);
  }
  
  public void setValue(Object key, Object value)
  {
    valMap.put(key, value);
  }
  
  public Object getValue(Object key) {
    return valMap.get(key);
  }
  
  public void setRowNum(int rowNum)
  {
    this.rowNum = rowNum;
  }
  public int getRowNum()
  {
    return rowNum;
  }

  //============  Aggregate function binding  ==============
  
  public void setBindingAggregateFunction(boolean isBindingAggFunction)
  {
    this.isBindingAggFunction = isBindingAggFunction;
  }
  public boolean isBindingAggregateFunction()
  {
    return isBindingAggFunction;
  }
  
  /**
   * Records aggregate functions encountered in a select list.
   * Agg functions form virtual columns when evaluating the outer select list
   * 
   * @param func
   */
  public void addAggregateFunction(FunctionNode func)
  {
    if (aggFunList == null)
      aggFunList = new ArrayList();
    aggFunList.add(func);
  }
  
  /**
   * 
   * @return List<FunctionNode>
   */
  public List getAggregateFunctionNodes()
  {
    return aggFunList;
  }

  public boolean hasAggregateFunctions()
  {
    return aggFunList != null;
  }
}

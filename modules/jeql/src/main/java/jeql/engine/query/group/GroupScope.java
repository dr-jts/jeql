package jeql.engine.query.group;

import java.util.Iterator;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.api.row.Row;
import jeql.api.table.Table;
import jeql.engine.EngineContext;
import jeql.engine.Scope;
import jeql.engine.query.QueryScope;
import jeql.syntax.TableColumnNode;

import com.vividsolutions.jts.util.Assert;

public class GroupScope
  implements QueryScope
{
  private QueryScope baseScope;
  private List groupByList;
  private List aggFun;
  private Row currentRow;
  private int groupBySize = 0;
  private int rowSize = 0;
  private Class[] aggRowColType;
  
  public GroupScope(QueryScope baseScope, 
      List groupByList, 
      List aggFun) 
  {
    this.baseScope = baseScope;
    this.groupByList = groupByList;
    this.aggFun = aggFun;
    groupBySize = groupByList.size();
    rowSize = groupBySize + aggFun.size();
    aggRowColType = new Class[rowSize];
    computeAggRowTypes();
  }

  public Scope getParent() { return baseScope.getParent(); }
  
  public EngineContext getContext() { return baseScope.getContext(); }
  
  public Table resolveTable(String name)
  {
    Assert.shouldNeverReachHere("Tables not defined in group scope");
    return null;
  }

  public boolean hasVariable(String name)
  {
   return baseScope.hasVariable(name);
  }

  public Object getVariable(String name)
  {
  return baseScope.getVariable(name);
  }

  public void setVariable(String name, Object value)
  {
    baseScope.setVariable(name, value);
  }

  public void setVariableType(String name, Class varType)
  {
    baseScope.setVariableType(name, varType);
  }

  public Class getVariableType(String name)
  {
    return baseScope.getVariableType(name);
  }


  public void setRow(Row row)  { currentRow = row;  }
  
  /**
   * Gets the current row at the execution point of this select context.
   * 
   * @return the current row
   */
  public Row getRow() { return currentRow; }

  public int getColumnIndex(String tblName, String colName)
  {
    // look up index in this scope for table.column
    for (int i = 0; i < groupByList.size(); i++) {
      TableColumnNode col = (TableColumnNode) groupByList.get(i);
      if (col.isMatch(tblName, colName))
        return i;
    }
    return -1;
  }

  public Object getColumnValue(int colIndex) 
  { 
    return currentRow.getValue(colIndex);
  }
  
  public Class getColumnType(int colIndex)
  {
    return (Class) aggRowColType[colIndex];
  }

  public Class[] getAggRowTypes()
  {
    return aggRowColType;
  }
  
  private void computeAggRowTypes()
  {
    int colCount = 0;
    for (Iterator i = groupByList.iterator(); i.hasNext(); ) {
      TableColumnNode col = (TableColumnNode) i.next();
      int baseColIndex = baseScope.getColumnIndex(col.getTableName(), col.getColName());
      if (baseColIndex < 0)
        throw new ExecutionException(col, "Unknown GROUP BY column: " + col.getColName());
      Class colType = baseScope.getColumnType(baseColIndex);
      aggRowColType[colCount++] = colType;
    }
    
    for (int j = 0; j < aggFun.size(); j++) {
      aggRowColType[colCount++] = ((AggregateFunction) aggFun.get(j)).getType();
    }
  }
  
  public Aggregator[] createAggregatorVector()
  {
    Aggregator[] agg = new Aggregator[aggFun.size()];
    for (int i = 0; i < agg.length; i++) {
      agg[i] = ((AggregateFunction) aggFun.get(i)).createAggregator();
    }
    return agg;
  }
  
  public int getAggregatorFunctionCount()
  {
    return aggFun.size();
  }
  
  /**
   * Gets the indices of the GROUP BY columns 
   * in the base scope. 
   * 
   * @return an array of column indices
   */
  public int[] getGroupKeyIndices()
  {
    int[] attrIndex = new int[groupBySize];
    int j = 0;
    for (Iterator i = groupByList.iterator(); i.hasNext(); ) {
      TableColumnNode col = (TableColumnNode) i.next();
      int baseColIndex = baseScope.getColumnIndex(col.getTableName(), col.getColName());
      attrIndex[j++] = baseColIndex;
    }
    return attrIndex;
  }
  
  public boolean hasValue(Object key) { return baseScope.hasValue(key); }
  public void setValue(Object key, Object value)
  {
    baseScope.setValue(key, value);
  }
  
  public Object getValue(Object key) {
    return baseScope.getValue(key);
  }

  // TODO: is this semantics correct? Or does grouping need its own rownum?
  public void setRowNum(int rowNum)
  {
    baseScope.setRowNum(rowNum);
  }
  public int getRowNum()
  {
    return baseScope.getRowNum();
  }
}

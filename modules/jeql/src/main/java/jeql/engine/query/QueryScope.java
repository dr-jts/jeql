package jeql.engine.query;

import jeql.api.row.Row;
import jeql.engine.Scope;

public interface QueryScope
  extends Scope
{
  public static final String VAR_SPLITVALUE = "splitValue";
  public static final String VAR_SPLITINDEX = "splitIndex";

  void setRow(Row row);
  Row getRow();

  void setRowNum(int rowNum);
  int getRowNum();
  
  int getColumnIndex(String tblName, String colName);
  Object getColumnValue(int colIndex) ;
  Class getColumnType(int colIndex);
  
  void setVariableType(String name, Class varType);
  Class getVariableType(String name);

  // execution context values (e.g. for pseudofunctions)
  boolean hasValue(Object key);
  void setValue(Object key, Object value);
  Object getValue(Object key);
  
}

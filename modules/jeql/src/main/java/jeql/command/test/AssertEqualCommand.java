package jeql.command.test;

import jeql.api.command.Command;
import jeql.api.error.JeqlException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowIteratorComparator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.util.TypeUtil;

/**
 * Tests objects/tables/columns for equality,
 * and throws an exception on failure.
 * If a single table is supplied, tests that it has two columns which 
 * contain equal values in each row.
 * 
 * @author Martin Davis
 *
 */
public class AssertEqualCommand 
implements Command
{
  private TableMatcher matcher = new TableMatcher();
  //private Table tbl;
  private Object val;
  private Object expected;
  
  public AssertEqualCommand() {
  }

  public void setMatchColumnNames(boolean matchColNames)
  {
    matcher.setMatchColumnNames(matchColNames);
  }
  
  public void setValue(Object val)
  {
   this.val = val;  
  }
  
  public void setExpected(Object expected)
  {
   this.expected = expected;  
  }
  
  /*
  public void setTable(Table tbl)
  {
   this.tbl = tbl;  
  }
  */
  
  public void setDefault(Object val)
  {
   this.val = val;  
  }
  
  public void execute(Scope scope)
  {
    if (val instanceof Table && expected == null) {
      checkEqual((Table) val);
      return;
    }
    
    if (val instanceof Table) {
      checkEqual((Table) val, (Table) expected);
    }
    else
      checkEqual(val, expected);
  }

  private void checkEqual(Table t)
  {
    String msg = checkColumnsEqual(t);
    if (msg == null) return;
    reportNotEqualError(msg);
  }
  
  private String checkColumnsEqual(Table t)
  {
    RowList rs = t.getRows();
    RowSchema schema = rs.getSchema();
    if (schema.getType(0) != schema.getType(1)) {
      return "Column types do not match";
    }
    
    RowIterator it = rs.iterator();
    while (true) {
      Row row = it.next();
      if (row == null) break;
      Object v0 = row.getValue(0);
      Object v1 = row.getValue(1);
      if (! TypeUtil.isEqual(v0, v1)) {
        return "Values do not match (" + v0 + ", " + v1 + ")";
      }
    }
    return null;
  }
  
  private void checkEqual(Table t1, Table t2)
  {
    boolean isEqual = matcher.match(t1, t2);
    String msg = matcher.getErrorMsg();
    if (isEqual) return;
    reportNotEqualError(msg);
  }
  
  private void checkEqual(Object o1, Object o2)
  {
    if (o1.getClass() == o2.getClass()) {
      boolean isEqual = TypeUtil.isEqual(o1, o2);
      if (isEqual) return;
    }
    reportNotEqualError("Values do not match (" + o1 + ", " + o2 + ")");
  }

  private void reportNotEqualError(String msg)
  {
    String baseMsg = "AssertEqual: Value does not equal Expected Value. ";
    String errMsg = baseMsg;
    if (msg != null && msg.length() > 0) {
      errMsg = baseMsg + " [Cause: " + msg + "]";
    }
    throw new JeqlException(errMsg);
  }
  
  private static class TableMatcher
  {    
    // default is to not match column names
    private boolean matchColNames = false;
    private String errorMsg = "";
    private NormalizingValueComparator valueComp = new NormalizingValueComparator();
    
    public TableMatcher()
    {
      
    }
    
    public void setMatchColumnNames(boolean matchColNames)
    {
      this.matchColNames = matchColNames;
    }
    
    public String getErrorMsg() { return errorMsg; }
    
    public boolean match(Table t1, Table t2)
    {
      if (t1.size() != t2.size()) {
        errorMsg = "Table schema sizes are not equal";
        return false;
      }
      
      if (matchColNames) {
        for (int i = 0; i < t1.size(); i++) {
          if (! t1.getColumnName(i).equals(t2.getColumnName(i))) {
            errorMsg = "Column names are different "
              + "(t1." + t1.getColumnName(i)
              + " vs "
              + "t2." + t2.getColumnName(i)
              + ")";
            return false;
          }
        }
      }
      if (! t1.getRows().getSchema().equals(t2.getRows().getSchema())) {
        errorMsg = "Schemas are not equal";
        return false;
      }
      RowIteratorComparator rowStrComp = new RowIteratorComparator(valueComp);
      boolean isRowsEqual = rowStrComp.compare(
          t1.getRows().iterator(), 
          t2.getRows().iterator()) == 0;
      if (! isRowsEqual) {
        errorMsg = "Rows are not equal";
      }
      return isRowsEqual;
    }
  }
}

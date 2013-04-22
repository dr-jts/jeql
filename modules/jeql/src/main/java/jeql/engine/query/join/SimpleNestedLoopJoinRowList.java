package jeql.engine.query.join;

import jeql.api.error.ImplementationException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;

/**
 * A simplistic nested loop join processor, 
 * which does not support a filter expression.
 * 
 * @author Martin Davis
 *
 */
public class SimpleNestedLoopJoinRowList 
  implements RowList
{
  private RowSchema schema = null;
  private RowList[] rowStr = new RowList[2];
  private int rsCount = 0;

  public SimpleNestedLoopJoinRowList() {
  }

  public void addRowStream(RowList rs)
  {
    rowStr[rsCount++] = rs;
    buildSchema();
  }
  
  private void buildSchema()
  {
    if (rsCount == 1) {
      schema = new RowSchema(rowStr[0].getSchema());
      return;
    }
    else if (rsCount == 2) {
      schema = new RowSchema(rowStr[0].getSchema(), rowStr[1].getSchema());
      return;
    }
    // should never get here
    throw new ImplementationException("Too many tables in join");
  }
  
  public RowSchema getSchema() { return schema; }

  public RowIterator iterator()
  {
    return new SimpleNestedLoopJoinRowIterator(schema, rowStr);
  }
  
  private static class SimpleNestedLoopJoinRowIterator
    implements RowIterator
  {
    private RowSchema schema;
    private RowList[] rowStr;
    private RowIterator[] rowIt;

    private Row row0;
    private Row row1;
    private BasicRow joinRow = null;

    public SimpleNestedLoopJoinRowIterator(RowSchema schema, RowList[] rowStr)
    {
      this.schema = schema;
      this.rowStr = rowStr;
      rowIt = new RowIterator[rowStr.length];
      rowIt[0] = rowStr[0].iterator();
      rowIt[1] = rowStr[1].iterator();
    }
    
    public RowSchema getSchema() { return schema; }

  public Row next()
  {
    if (joinRow == null) {  // just started
      
      // get first rows in each stream
      row0 = rowIt[0].next();
      if (row0 == null) return null;
      row1 = rowIt[1].next();
      if (row1 == null) return null;
   
      return createRow();
    }
    
    // here we can assume left rows are non-null
    // can also assume that right table is non-empty (otherwise would have returned above)
    row1 = rowIt[1].next();
    if (row1 != null) {
      return createRow();
    }
    
    row0 = rowIt[0].next();
    if (row0 != null) {
      rowIt[1]= rowStr[1].iterator();
      row1 = rowIt[1].next();
      return createRow();
    }
    
    return null;
  }
  
  private void copyToJoin(Row srcRow, int startIndex)
  {
    for (int i = 0; i < srcRow.size(); i++) {
      joinRow.setValue(i + startIndex, srcRow.getValue(i));
    }
  }
  
  private Row createRow()
  {
    if (joinRow == null) {
      joinRow = new BasicRow(schema.size());
    }
    copyToJoin(row0, 0);
    copyToJoin(row1, row0.size());
    return joinRow;
  }
  
  }
}

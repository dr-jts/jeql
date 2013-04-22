package jeql.engine.query;

import jeql.api.row.ArrayRowList;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.syntax.SelectNode;

public class LimitEvaluator 
  implements QueryOp
{
   private RowList rowStr;
   private int limit;
   private int offset = 0;
   private int maxRowCount = 0;
   
   public LimitEvaluator(SelectNode select, RowList rs) 
   {
     rowStr = rs;
     limit = select.getLimitValue();
     offset = select.getOffsetValue();
   }
   
   /**
    * 
    * @param rowStr
    * @param limit the limit value (-1 if no limit)
    * @param offset the offset value (0 if no offset)
    */
  public LimitEvaluator(RowList rowStr, int limit, int offset) 
  {
    this.rowStr = rowStr;
    this.limit = limit;
    this.offset = offset;
  }
  
  public RowList eval()
  {
    /**
     * Eval using a stream
     */
    return new LimitRowList(rowStr, limit, offset);
  }

  public RowList OLDeval()
  {
    if (limit < 0)
      maxRowCount = -1;
    else
      maxRowCount = limit + offset;

    ArrayRowList rowList = new ArrayRowList(rowStr.getSchema());

    RowIterator it = rowStr.iterator();
    
    int rowCount = 0;
    
    // get rows after offset up to limit
    while (true) {
      Row row = it.next();
      if (row == null) return rowList;
      rowCount++;
      if (rowCount <= offset) continue;
      rowList.add(row);
      if (maxRowCount >= 0 && rowCount >= maxRowCount) return rowList;
    }
  }
  
}


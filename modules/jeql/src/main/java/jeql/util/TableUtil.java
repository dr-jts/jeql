package jeql.util;

import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;

public class TableUtil 
{
  public static RowSchema emptySchema()
  {
    return new RowSchema(0);
  }
  
  public static RowList singleRowList(RowSchema schema)
  {
    ArrayRowList singleRowList = new ArrayRowList(schema);
    BasicRow row = new BasicRow(schema.size()); 
    singleRowList.add(row);
    return singleRowList;
  }
}

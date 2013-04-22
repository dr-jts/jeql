package jeql.engine.query;

import jeql.api.error.ExecutionException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;

public class UnionRowList 
  implements RowList
{
  private RowList rs1;
  private RowList rs2;
  
  public UnionRowList(RowList rs1, RowList rs2) 
  {
    this.rs1 = rs1;
    this.rs2 = rs2;
    checkSchemasEqual(rs1, rs2);
  }

  private void checkSchemasEqual(RowList rs1, RowList rs2)
  {
    if (! rs1.getSchema().equalsWithNames(rs2.getSchema()))
      throw new ExecutionException("Schemas of UNIONed tables are not equal (" 
          + rs1.getSchema() + " *** "
          + rs2.getSchema() + " )");
  }
  
  public RowSchema getSchema() { return rs1.getSchema(); }

  public RowIterator iterator()
  {
    return new UnionRowIterator(rs1, rs2);
  }
  
  private static class UnionRowIterator implements RowIterator {
    private RowList rs1;
    private RowList rs2;
    private RowSchema schema;
    private RowIterator it1;
    private RowIterator it2;

    public UnionRowIterator(RowList rs1, RowList rs2) {
      schema = rs1.getSchema();
      it1 = rs1.iterator();
      this.rs2 = rs2;
    }

    public RowSchema getSchema() {
      return schema;
    }

    public Row next() 
    {
      if (it1 != null) {
        Row nextRow = it1.next();
        if (nextRow != null)
          return nextRow;
        it1 = null;
        it2 = rs2.iterator();
      }
      // final state
      if (it2 == null) return null;
      return it2.next();
    }

  }
}

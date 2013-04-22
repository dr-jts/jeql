package jeql.api.row;


public class AliasedRowList 
implements RowList
{
  private RowList baseRows;
  private RowSchema schema;
  
  public AliasedRowList(RowList rl, String[] colNames)
  {
    baseRows = rl;
    schema = new RowSchema(colNames, baseRows.getSchema());
  }
  
  public RowSchema getSchema() {
    return schema;
  }

  public RowIterator iterator() {
    return new AliasedRowIterator(baseRows.iterator());
  }

  private class AliasedRowIterator implements RowIterator
  {
    private RowIterator baseRowIt;
    
    public AliasedRowIterator(RowIterator rowIt)
    {
      baseRowIt = rowIt;
    }
    
    public RowSchema getSchema() {
      return schema;
    }

    public Row next() {
      return baseRowIt.next();
    }
    
  }
}

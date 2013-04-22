package jeql.api.row;


public interface RowList 
{
  RowSchema getSchema();
  RowIterator iterator();
}

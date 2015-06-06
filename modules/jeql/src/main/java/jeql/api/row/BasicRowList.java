package jeql.api.row;

public abstract class BasicRowList implements RowList
{

  protected RowSchema schema;

  public RowSchema getSchema()
  {
    return schema;
  }

}

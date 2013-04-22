package jeql.api.error;

/**
   * Exceptions which occur at execution time.
 * 
 * @author Martin Davis
 *
 */
public class MissingColumnException 
  extends ExecutionException
{

  public MissingColumnException(String colName) {
    this(colName, null);
  }
  
  public MissingColumnException(String colName, String suffix) 
  {
    super(colName + " column is not defined"
        + (suffix != null ? " " + suffix : "") );
  }
  
  public MissingColumnException(String tblName, String colName, String suffix) 
  {
    super(tblName + "." + colName + " column is not defined"
        + (suffix != null ? " " + suffix : "") );
  }
}

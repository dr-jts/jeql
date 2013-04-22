package jeql.api.error;

/**
 * Indicates an illegal value was encountered during input.
 * 
 * @author Martin Davis
 *
 */
public class InvalidInputException 
  extends ExecutionException
{
  private String cause;
  
  public InvalidInputException(String cause) {
    super(msg(cause, -1));
    this.cause = cause;
  }
    
  public InvalidInputException(String cause, int row) {
    super(msg(cause, row));
  }
    
  public InvalidInputException(InvalidInputException e, int row) {
    super(msg(e.cause, row));
  }
    
  private static String msg(String cause, int row)
  {
    String rowClause = "";
    if (row >= 0) rowClause = " (row " + row + ")";
    return "Invalid input : " + cause + rowClause;
  }
}

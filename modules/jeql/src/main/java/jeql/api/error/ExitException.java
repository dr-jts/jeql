package jeql.api.error;


/**
 * Causes termination of execution
 * 
 * @author Martin Davis
 * 
 */
public class ExitException 
extends JeqlException 
{

  public ExitException(String msg) {
    super(msg);
  }
  public ExitException() {
    this("");
  }
}

package jeql.api.error;

/**
 * Indicates an illegal value was encountered during processing.
 * 
 * @author Martin Davis
 *
 */
public class IllegalValueException 
  extends ExecutionException
{

  public IllegalValueException(String msg) {
    super(msg);
  }
  public IllegalValueException(String nature, String value) {
    super("Illegal value for " + nature + ": " + value);
  }
}

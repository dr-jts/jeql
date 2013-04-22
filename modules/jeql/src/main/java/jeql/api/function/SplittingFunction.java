package jeql.api.function;

/**
 * Interface for SPLIT BY functions.
 * <p>
 * Functions implementing this interface
 * must also supply a method with signature
 * <pre>
 *    List execute(...)
 * </pre>
 * This function provides the signature of the
 * user-level function, as well
 * as the logic to implement the function. 
 * It may take any number and type of arguments.
 * 
 * @author Martin Davis
 */
public interface SplittingFunction 
{
  /**
   * Returns the type of the objects which are contained 
   * in the list of split items.
   * 
   * @return the type of the objects resulting from the split
   */
  Class getType();
}

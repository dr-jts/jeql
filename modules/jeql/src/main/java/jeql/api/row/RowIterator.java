package jeql.api.row;


/**
 * In order to simplify the logic of extending and client classes,
 * this implementation uses the concept of a sentinel to signal the end
 * of the stream.
 * It is straightforward to check this value and exit from 
 * calling classes (as opposed to providing the equivalent
 * of a hasNext method, which requires error-prone lookahead logic).
 * The sentinel value is simply <tt>null</tt>.
 * 
 * @author Martin Davis
 *
 */
public interface RowIterator 
{
  /**
   * Gets the {@link RowSchema} for the Rows returned by this stream.
   * 
   * @return a RowSchema
   */
  RowSchema getSchema();
  
  /**
   * Gets the next Row from this stream, if any.
   * The Row returned must be a value object 
   * (i.e. its contents must not change once it has been returned).
   * 
   * @return a Row
   * @return null if no Row remains in the stream
   */
  Row next();

}

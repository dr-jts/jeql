package jeql.api.error;

import jeql.syntax.ParseTreeNode;

/**
   * Exceptions which occur at execution time.
 * 
 * @author Martin Davis
 *
 */
public class ExecutionException 
  extends JeqlException
{

  public ExecutionException(String msg) {
    super(msg);
  }

  public ExecutionException(ParseTreeNode node, String msg) {
    super(node, msg);
  }

  public ExecutionException(int line, String msg) {
    super(line, msg);
  }
  public ExecutionException(Throwable ex) {
    super(ex);
  }
}

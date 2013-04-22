package jeql.api.error;

import jeql.syntax.ParseTreeNode;
import jeql.util.ExceptionUtil;

/**
 * Errors which are due to an incorrect implementation.
 * For example, assertion failures are usually of this type.
 * 
 * @author Martin Davis
 *
 */
public class ImplementationException 
  extends JeqlException
{

  private static final String MSG_TAG = "INTERNAL ERROR - ";
  
  public ImplementationException(String msg) {
    super(MSG_TAG + msg);
  }

  public ImplementationException(ParseTreeNode node, String msg) {
    super(node, MSG_TAG + msg);
  }
  
  public ImplementationException(ParseTreeNode node, Throwable ex) {
    super(node, MSG_TAG + ExceptionUtil.getMessage(ex) + " (" +  ExceptionUtil.throwLocation(ex) + ")");
  }
  
  public ImplementationException(int line, String msg) {
    super(line, MSG_TAG + msg);
  }

  public ImplementationException(Throwable ex) {
    super(ex);
  }

}

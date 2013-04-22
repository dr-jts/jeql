package jeql.api.error;

import jeql.syntax.ParseTreeNode;
import jeql.util.ExceptionUtil;

/**
 * 
 * 
 * Extends RuntimeException, to make code development easier. 
 * 
 * @author Owner
 *
 */
public class JeqlException
  extends RuntimeException
{
  public static String errorFileLoc(String filename, int line)
  {
    return "(" + filename + ":" + line +") ";
  }
  private ParseTreeNode node = null;
  private int line = 0;
  
  public JeqlException(String msg) {
    super(msg);
  }

  public JeqlException(ParseTreeNode node, String msg) {
    super(msg);
    this.node = node;
    if (node != null) line = node.getLine();
  }

  public JeqlException(int line, String msg) {
    super(msg);
    this.line = line;
  }

  public JeqlException(Throwable ex) {
    this(ExceptionUtil.getMessage(ex));
  }

  public String getLocMessage(String filename)
  {
    if (hasLocation()) {
      return errorFileLoc(filename, line)
       + getMessage();
    }
    return getMessage();
  }
  
  public boolean hasLocation()
  {
    return  line != 0;
  }
  
  public void setLocation(ParseTreeNode node)
  {
    setLocation(node.getLine());
  }
  
  public void setLocation(int line)
  {
    // this indicates a flaw in the design of line number handling
    if (this.line != 0) {
      // MD - disable for now until this approach is really required
      //throw new IllegalStateException("*** Line number is already set ***");
    }

    // MD - don't override line # if already set
    if (this.line == 0)
      this.line = line;
  }
}

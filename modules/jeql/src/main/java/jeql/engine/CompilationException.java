package jeql.engine;

import jeql.api.error.JeqlException;
import jeql.syntax.ParseTreeNode;

/**
 * User-caused errors which are detected at compile-time.
 * These would be situations which are legal according to 
 * the parser but which are determined to be semantically illegal
 * by subsequent analysis.
 * 
 * @author Martin Davis
 *
 */
public class CompilationException 
  extends JeqlException
{

  public CompilationException(String msg) {
    super(msg);
  }

  public CompilationException(ParseTreeNode node, String msg) {
    super(node, msg);
  }
  
  public CompilationException(int line, String msg) {
    super(line, msg);
  }


  public CompilationException(Throwable ex) {
    super(ex);
  }

}

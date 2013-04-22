package jeql.engine;

import jeql.api.error.ExecutionException;
import jeql.syntax.ParseTreeNode;

/**
   * Exceptions which occur at execution time.
 * 
 * @author Martin Davis
 *
 */
public class UndefinedVariableException 
  extends ExecutionException
{
  public UndefinedVariableException(String varName) {
    super(msg(varName));
  }
  
  public UndefinedVariableException(ParseTreeNode node, String varName) {
    super(node, msg(varName));
  }

  public UndefinedVariableException(int line, String varName) {
    super(line, msg(varName));
  }

  private static String msg(String varName)
  {
    return "Variable '" + varName + "' is not defined";
  }
}

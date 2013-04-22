package jeql.command.test;

import jeql.api.command.Command;
import jeql.api.error.JeqlException;
import jeql.engine.Scope;

/**
 * Tests a condition,
 * and throws an exception if it is false.
 *
 * @author Martin Davis
 *
 */
public class AssertCommand 
implements Command
{
  private Object cond;
  private boolean isFailExpected;
  
  public AssertCommand() {
  }
  
  public void setDefault(Object cond)
  {
   this.cond = cond;  
  }
  
  public void setFails(boolean isFailExpected)
  {
    isFailExpected = true;
  }
  
  public void execute(Scope scope)
  {
    boolean bool = ((Boolean) cond).booleanValue();
    if (! bool) {
      reportFailure("");
    }
  }

  private void reportFailure(String msg)
  {
    String baseMsg = "Assert: FAILED ";
    String errMsg = baseMsg;
    if (msg != null && msg.length() > 0) {
      errMsg = baseMsg + " [Cause: " + msg + "]";
    }
    throw new JeqlException(errMsg);
  }
  
}

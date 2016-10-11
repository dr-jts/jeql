package jeql.command.test;

import jeql.api.command.Command;
import jeql.api.error.JeqlException;
import jeql.api.table.Table;
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
  private Object actual;
  private Object expected;
  private boolean isExpectedSet = false;
  private boolean isFailExpected;
  
  public AssertCommand() {
  }
  
  public void setDefault(Object actual)
  {
   this.actual = actual;  
  }
  
  public void setFails(boolean isFailExpected)
  {
    isFailExpected = true;
  }
  
  public void setEquals(Object expected) {
    setExpected(expected);
  }
  
  public void setExpected(Object expected) {
    this.expected = expected;
    isExpectedSet = true;
  }
  
  public void execute(Scope scope)
  {
    if (! isExpectedSet) {
      expected = true;
    }
    if (isFailExpected) {
      boolean bool = ((Boolean) actual).booleanValue();
      if (! bool) {
        reportFailure("");
      }
      return;
    }
    String errMsg = AssertUtil.checkEqual(actual, expected);
    if (errMsg != null)
      reportFailure(errMsg);
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

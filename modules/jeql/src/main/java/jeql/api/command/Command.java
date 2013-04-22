package jeql.api.command;

import jeql.engine.Scope;

/**
 * Interface for classes which implement commands.
 * 
 * 
 * @author Martin Davis
 *
 */
public interface Command 
{
  /**
   * Called to execute the command
   *
   */
  void execute(Scope scope) throws Exception;
}

package jeql.monitor;

import jeql.api.error.JeqlException;

/**
 * Exception which indicates the script has been terminated 
 * by an external event (such as user cancellation).
 * 
 * @author Martin Davis
 *
 */
public class TerminationException 
extends JeqlException
{

  public TerminationException() {
    super("*** Script execution terminated ***");
  }
  

}

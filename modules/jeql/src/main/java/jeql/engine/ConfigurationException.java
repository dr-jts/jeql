package jeql.engine;

import jeql.api.error.JeqlException;


/**
 * Exceptions which indicate a problem in the
 * configuration of the Perql engine.
 * 
 * @author Martin Davis
 *
 */
public class ConfigurationException 
extends JeqlException
{

  public ConfigurationException(String msg) {
    super(msg);
  }
  
  public ConfigurationException(Throwable ex) {
    super(ex);
  }

}

package jeql.engine;

import jeql.api.table.Table;

/**
 * The context for evaluating language constructs. 
 * 
 * @author Martin Davis
 * @version 1.0
 */
public interface Scope
{
  Table resolveTable(String name);
  
  EngineContext getContext();
  
  Scope getParent();
  
  boolean hasVariable(String name);

  Object getVariable(String name);
  
  void setVariable(String name, Object val);

  Class getVariableType(String name);
  

}

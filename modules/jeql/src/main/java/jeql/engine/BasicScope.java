package jeql.engine;

import java.util.Map;
import java.util.TreeMap;

import jeql.api.error.MissingColumnException;
import jeql.api.table.Table;


/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Martin Davis
 * @version 1.0
 */
public class BasicScope
  implements Scope
{
  private EngineContext context;
  private Map varMap = new TreeMap();
  
  public BasicScope() {
    context = EngineContext.getInstance();
  }

  public BasicScope(EngineContext context) {
    this.context = context;
  }

  public BasicScope(Scope scope) {
    context = scope.getContext();
  }

  public Scope getParent() { return null; }
  
  public void setVariable(String name, Object value)
  {
    varMap.put(name, value);
  }

  public boolean hasVariable(String name)
  {
    return varMap.containsKey(name);
  }

  public Object getVariable(String name)
  {
    checkDefined(name);
    return varMap.get(name);
  }

  public Class getVariableType(String name)
  {
    Object value = varMap.get(name);
    if (value == null) {
      throw new MissingColumnException(name);
    }
    return value.getClass();
  }

  public Table resolveTable(String name)
  {
    checkDefined(name);
    return (Table) varMap.get(name);
  }
  
  private void checkDefined(String name)
  {
    if (! hasVariable(name))
      throw new UndefinedVariableException(name);
  }

  public EngineContext getContext() { return context; }
  
}

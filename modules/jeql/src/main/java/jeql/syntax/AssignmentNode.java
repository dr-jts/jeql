package jeql.syntax;

import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.monitor.Monitor;

public class AssignmentNode 
  extends ParseTreeNode
{
  private String lhs;
  private ParseTreeNode rhs;
  
  public AssignmentNode(String lhs, 
      ParseTreeNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public String getVariableName() { return lhs; }
  
  public Class getType(Scope scope)
  {
    return rhs.getType(scope);
  }
  
  public void bind(Scope scope)
  {
    rhs.bind(scope);
  }

  public Object eval(Scope scope)
  {
    Object v = rhs.eval(scope);  
    v = monitor(v);
    scope.setVariable(lhs, v);
    return v;
  }
  
  private Object monitor(Object v)
  {
    if (v instanceof Table && rhs instanceof SelectNode) {
      SelectNode select = (SelectNode) rhs;
      v = Monitor.wrap(select.getLine(), 
          lhs,
          lhs + " = " + select.monitorTag(), v);
    }
    return v;
  }
  
  public String monitorTag()
  {
    SelectNode select = (SelectNode) rhs;
    return lhs + " = " + select.monitorTag();
  }
}

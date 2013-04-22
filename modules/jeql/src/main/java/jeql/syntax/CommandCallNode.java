package jeql.syntax;

import java.util.List;

import jeql.api.error.JeqlException;
import jeql.engine.CommandInvoker;
import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.monitor.Monitor;

public class CommandCallNode 
  extends ParseTreeNode
{
  private String className = null;
  private String name;
  private List args; //List<CommandParameterNode>
  private CommandInvoker invoker;
  
  public CommandCallNode(String className,
      String name,
      List args) {
    this.className = className;
    this.name = name;
    this.args = args;
  }

  public CommandCallNode(
      String name,
      List args) {
    this(null, name, args);
  }

  public Class getType(Scope scope)
  {
    throw new UnsupportedOperationException();
  }
  
  public void bind(Scope scope)
  {
    invoker = scope.getContext().getCommand(className);
    if (invoker == null) {
      throw new CompilationException(this, "Unknown command: " + className);
    }
    
    invoker.checkParametersExist(args);
    
    for (int i = 0; i < args.size(); i++) {
      ((ParseTreeNode) args.get(i)).bind(scope);
    }
  }

  public Object eval(Scope scope)
  {
    try {
      invoker.invoke(args, scope);
    }
    catch (JeqlException ex) {
      ex.setLocation(this);
      throw ex;
    }
    return null;
  } 
  
  /*
  public Object evalMock(Scope scope)
  {
    // mock a procedure for now
    CommandArgNode arg = (CommandArgNode) args.get(0);
    Object val = arg.eval(scope);
    Table tbl = (Table) val;
//    RestartableRowStream rs = (RestartableRowStream) tbl.getRowStream();
    
    // stub for now only
    TablePrinter.print(tbl, true);
    return null;
  }
  */
  

}

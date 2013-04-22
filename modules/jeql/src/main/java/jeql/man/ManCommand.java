package jeql.man;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import jeql.api.command.Command;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.EngineContext;
import jeql.engine.FunctionRegistry;
import jeql.engine.Scope;
import jeql.util.ClassUtil;

public class ManCommand
implements Command
{
  private RowSchema functionsSchema = new RowSchema(
      new String[] {
          "module",
          "name",
          "type",
          "description"
      },
      new Class[] {
         String.class, String.class, String.class, String.class 
      });

  private RowSchema functionParamSchema = new RowSchema(
      new String[] {
          "module",
          "name",
          "type",
          "name",
          "description"
      },
      new Class[] {
         String.class, String.class, String.class, String.class, String.class 
      });

  private Table functions;
  private Table functionParam;
  
  public ManCommand()
  {
    
  }
  
  public Table getFunctions()
  {
    return functions;
  }
  
  public Table getFunctionParams()
  {
    return functionParam;
  }
  
  public void execute(Scope scope) throws Exception
  {
    genFunctions();
  }
  
  private void genFunctions()
  {
    ArrayRowList funcRL = new ArrayRowList(functionsSchema);
    ArrayRowList funcParamRL = new ArrayRowList(functionParamSchema);
    
    FunctionRegistry reg = EngineContext.getInstance().getFunctionRegistry();
    Collection funcNames = reg.getFunctionNames();
    
    String currModule = "";
    for (Iterator i = funcNames.iterator(); i.hasNext(); ) {
      
      String funcName = (String) i.next();
      
      Collection methods = reg.getFunctionMethods(funcName);
      
      for (Iterator j = methods.iterator(); j.hasNext(); ) {
        Method meth = (Method) j.next();
        
        genFunction(funcName,
            meth,
            funcRL, funcParamRL);
        
      }
    }
    functions = new Table(funcRL);
    functionParam = new Table(funcParamRL);
  }
  
  private void genFunction(String funcName, Method meth, 
      ArrayRowList funcRL, ArrayRowList funcParamRL)
  {
    String moduleName = FunctionRegistry.moduleName(funcName);
    String name = ClassUtil.classname(funcName);
    Class retType = meth.getReturnType();
    String type = ClassUtil.classname(retType);
    
    funcRL.add(new BasicRow(new Object[] {
        moduleName,
        name,
        type,
        ManUtil.description(meth)
    }));
    
    Class[] param = meth.getParameterTypes();
    Annotation[][] panno = meth.getParameterAnnotations();
    
    for (int i = 0; i < param.length; i++) {
      String pname = ManUtil.name(panno[i]);
      funcParamRL.add(new BasicRow(new Object[] {
          moduleName,
          name,
          ClassUtil.classname(param[i].getName()),
          pname,
          ManUtil.description(meth)
      }));
    }
  }


}

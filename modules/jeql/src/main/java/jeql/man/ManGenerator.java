package jeql.man;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jeql.api.annotation.Metadata;
import jeql.engine.CommandInvoker;
import jeql.engine.CommandRegistry;
import jeql.engine.EngineContext;
import jeql.engine.FunctionRegistry;
import jeql.util.ClassUtil;
import jeql.util.StringUtil;

public class ManGenerator 
{
  public static void generate(EngineContext context, Writer writer)
  {
    ManGenerator mg = new ManGenerator(context);
    mg.generate(writer);
  }
  
  public static String generate(EngineContext context)
  {
    ManGenerator mg = new ManGenerator(context);
    StringWriter sw = new StringWriter();
    mg.generate(sw);
    return sw.toString();
  }
  
  public static String generate()
  {
    return generate(EngineContext.getInstance());
  }
  
  EngineContext context;
  TextManWriter writer;
  
  public ManGenerator(EngineContext context) {
    this.context = context;
  }

  void generate(Writer wr)
  {
    writer = new TextManWriter(wr);
    writer.writeTitle("Functions");
    writeFunctions();
    writer.writeTitle("Commands");
    writeCommands();
    try {
      wr.flush();
    }
    catch (IOException e) {
      // do nothing
    }
  }
  
  void writeFunctions()
  {

    FunctionRegistry reg = context.getFunctionRegistry();
    Collection funcNames = reg.getFunctionNames();
    
    String currModule = "";
    for (Iterator i = funcNames.iterator(); i.hasNext(); ) {
      String name = (String) i.next();
      
      String module = FunctionRegistry.moduleName(name);
      // add blank line between modules
      if (! module.equals(currModule)) {
        currModule = module;
        writer.writeFunctionModule(module);
      }
      
      Collection methods = reg.getFunctionMethods(name);
      
//      int retTypeWidth = findMaxReturnTypeLen(methods);
      for (Iterator j = methods.iterator(); j.hasNext(); ) {
        writeFunction(name, (Method) j.next());
      }
    }
  }
  
  
  private int findMaxReturnTypeLen(Collection methods)
  {
    int max = 0;
    for (Iterator j = methods.iterator(); j.hasNext(); ) {
      Class retType = ((Method) j.next()).getReturnType();
      int retTypeLen = ClassUtil.classname(retType).length();
      if (retTypeLen > max)
        max = retTypeLen;
    }
    return max;
  }
  
  private void writeFunction(String name, Method meth)
  {
    writer.writeFunction(FunctionRegistry.resultType(meth), name, ManUtil.description(meth));
    
    Class[] param = meth.getParameterTypes();
    Annotation[][] panno = meth.getParameterAnnotations();
    
    for (int i = 0; i < param.length; i++) {
      String pname = ManUtil.name(panno[i]);
      writer.writeFunctionParam(ClassUtil.classname(param[i]), pname);
    }
    writer.writeFunctionClose();
  }
  
  void writeCommands()
  {
    CommandRegistry reg = context.geCommandRegistry();
    Collection cmdNames = reg.getCommandNames();
    
    for (Iterator i = cmdNames.iterator(); i.hasNext(); ) {
      String name = (String) i.next();
      CommandInvoker invoker = reg.getCommand(name);
      writer.writeCommand(name, ManUtil.description(invoker.getCommandClass()));
      writeParams(invoker);
    }
  }
  
  void writeParams(CommandInvoker invoker)
  {
    Map params = CommandUtil.getParameters(invoker.getCommandClass());
    
    // output default first
    if (params.containsKey(CommandInvoker.DEFAULT_METHOD_NAME)) {
      //System.out.println(PARAM_INDENT + params.get(CommandInvoker.DEFAULT_METHOD_NAME));
      CommandParamMethod param = (CommandParamMethod) params.get(CommandInvoker.DEFAULT_METHOD_NAME);
      writer.writeCommandParam(param);
    }
    for (Iterator i = params.keySet().iterator(); i.hasNext(); ) {
      String paramName = (String) i.next();
      if (paramName.equals(CommandInvoker.DEFAULT_METHOD_NAME))
        continue;
      CommandParamMethod param = (CommandParamMethod) params.get(paramName);
      writer.writeCommandParam(param);
    }

  }
  
}

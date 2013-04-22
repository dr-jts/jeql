package jeql.man;

import java.lang.reflect.Method;

import jeql.util.ClassUtil;

public class CommandParamMethod {

  private String name;
  private Method method;
  private boolean isInput = false;
  private boolean isOutput = false;
  
  public CommandParamMethod(String name, Method method) 
  {
    this.name = name;
    this.method = method;
  }

  public String getName()
  {
    return name;
  }
  
  public Method getMethod()
  {
    return method;
  }
  
  public void setInput(boolean isInput)
  {
    this.isInput = isInput;
  }
  
  public void setOutput(boolean isOutput)
  {
    this.isOutput = isOutput;
  }
  
  public boolean isInput() { return isInput; }
  
  public boolean isOutput() { return isOutput; }
  
  public String getIOTag()
  {
    return (isInput ? "I" : "") + (isOutput ? "O" : "");
  }
  public String getDisplayName()
  {
    if (name.equalsIgnoreCase("default"))
      return "<" + name + ">";
    return name;
  }
  
  public String getDescription()
  {
    return ManUtil.description(method);
  }
  public String getArgTypeList()
  {
    if (isInput)
      return methodParamList(method);
    return ManGenerator.methodReturnType(method);
  }
  
  public static String methodParamList(Method meth)
  {
    StringBuffer pd = new StringBuffer();
    Class[] param = meth.getParameterTypes();
    for (int i = 0; i < param.length; i++) {
      if (i > 0)
        pd.append(", ");
      pd.append(ClassUtil.classname(param[i].getName()));
    }
    return pd.toString();
  }

  public String toString()
  {
    return getIOTag() + " " + getDisplayName() 
    + ": " +  getArgTypeList()
    + " - " + ManUtil.description(method);
  }
}

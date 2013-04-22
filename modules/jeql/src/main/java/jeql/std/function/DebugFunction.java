package jeql.std.function;

import jeql.api.function.FunctionClass;

public class DebugFunction 
implements FunctionClass
{
  public static Object print(Object obj)
  {
    System.out.print(obj);
    return obj;
  }
  public static Object println(Object obj)
  {
    System.out.println(obj);
    return obj;
  }

}

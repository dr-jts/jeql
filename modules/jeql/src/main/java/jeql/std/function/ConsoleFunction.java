package jeql.std.function;

import jeql.api.function.FunctionClass;

public class ConsoleFunction 
implements FunctionClass
{
  public static String println(Object s)
  {
    System.out.println(s);
    return s.toString();
  }
  public static String print(Object s)
  {
    System.out.print(s);
    return s.toString();
  }

}

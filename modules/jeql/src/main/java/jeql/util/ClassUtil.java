package jeql.util;

public class ClassUtil 
{
  public static Class objectClass(Class c)
  {
    if (c == int.class) return Integer.class;
    if (c == double.class) return Double.class;
    if (c == boolean.class) return Boolean.class;
    return c;
  }

  public static String classname(Class javaClass)
  {
    String jClassName = javaClass.getName();
    return classname(jClassName);
  }

  public static String classname(String jClassName)
  {
    int lastDotPos = jClassName.lastIndexOf(".");
    return jClassName.substring(lastDotPos + 1, jClassName.length());
  }

  
}

package jeql.std.function;

import jeql.api.function.FunctionClass;

public class XmlStrFunction
implements FunctionClass
{
  public static String elt(String name, Object content)
  {
    return elt2(name, null, content);
  }

  private static boolean isElement(Object content)
  {
    if (! (content instanceof String)) return false;
    return ((String) content).startsWith("<");
  }
  public static String elt2(String name, String attrs, Object content)
  {
    StringBuffer buf = new StringBuffer();
    
    buf.append("<");
    buf.append(name);
    if (attrs != null) 
      buf.append(attrs);
    buf.append(">");
    
    if (isElement(content))
      buf.append("\n");
    buf.append(content);
    
    buf.append("</");
    buf.append(name);
    buf.append(">\n");
    
    return buf.toString();
  }

  public static String attr(String name, Object content)
  {
    return " " + name + "=" + "'" + content + "'";
  }
  
  
}

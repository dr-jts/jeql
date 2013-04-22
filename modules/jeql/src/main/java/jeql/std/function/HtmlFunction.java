package jeql.std.function;

import jeql.api.function.FunctionClass;

public class HtmlFunction 
implements FunctionClass
{
  public static String encode(String str)
  {
    StringBuffer buf = new StringBuffer((int) (str.length() * 1.1));
    
    int len = str.length();
    for (int i = 0; i < len; i++) {
        char c = str.charAt(i);
        String entityName = entityName(c);
        if (entityName == null) {
            if (c > 0x7F) {
                buf.append("&#");
                buf.append(Integer.toString(c, 10));
                buf.append(';');
            } else {
              buf.append(c);
            }
        } else {
          buf.append('&');
          buf.append(entityName);
          buf.append(';');
        }
    }
    return buf.toString();
  }

  private static String entityName(char c)
  {
    switch (c) {
    case '"': return "quot";
    case '&': return "amp";
    case '<': return "lt";
    case '>': return "gt";
    }
    return null;
  }
}

package jeql.syntax;

import jeql.engine.Scope;
import jeql.syntax.util.StringLiteralUtil;



/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Martin Davis
 * @version 1.0
 */
public class StringLiteralNode
  extends ParseTreeNode
{
  public static final char ESCAPED_CHAR = '\\';
  public static final char RICH_CHAR = '$';
  public static final char QUICK_CHAR = '#';
  
  private String value = null;
  private RichString richStr = null;

  public StringLiteralNode(String tokenImage) 
  {
    //escaped string
    if (tokenImage.charAt(0) == ESCAPED_CHAR) {
      // strip leading \ and surrounding quotatation chars
      value = tokenImage.substring(2, tokenImage.length() - 1);
    }
    // rich string
    else if (tokenImage.charAt(0) == QUICK_CHAR) {
      // strip leading #
      value = tokenImage.substring(1, tokenImage.length());
    }
    else if (tokenImage.charAt(0) == RICH_CHAR) {
      // strip leading $ and surrounding quotatation chars
      String str = tokenImage.substring(2, tokenImage.length()-1);
      richStr = new RichString(str);
    }
    else {
      // strip leading and trailing quote chars (either " or ')
      String str = tokenImage.substring(1, tokenImage.length()-1);
      value = StringLiteralUtil.decodeEscapedString(str);
    }
  }

  /**
   * Returns the literal value of this string.
   * If this is a rich string, the value is null,
   * since rich string's value can potentially depend on 
   * execution context.
   * 
   * @return the constant value of this string, if any
   */
  public String getConstantValue()
  { 
    return value;
  }
  public Class getType(Scope scope)
  {
    return String.class;
  }
   
  public void bind(Scope scope)
  {
  }

  public Object eval(Scope scope)
  {
    if (richStr != null) {
      return richStr.eval(scope);
    } 
    return value;
  }
  
}
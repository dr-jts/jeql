package jeql.syntax.util;

public class StringLiteralUtil 
{

  public static String decodeRichString(String tokenImage)
  {
    // strip quotes
    String str = tokenImage.substring(2, tokenImage.length()-1);
    return decodeEscapedString(str);
  }
  
  public static String decodeRawString(String tokenImage)
  {
    return tokenImage.substring(2, tokenImage.length() - 1);
  }
  
  /**
   * Decodes a raw string supporting escaped quote.
   * No longer used.
   * 
   * @param tokenImage
   * @return
   */
  private static String OLDdecodeRawString(String tokenImage)
  {
    // strip quotes
    String val = tokenImage.substring(2, tokenImage.length() - 1);
    
    // only escape allowed is escape-quote
    if (val.indexOf("\\\"") < 0) {
      return val;
    }
    
    // convert escape-quotes
    StringBuffer buf = new StringBuffer(val.length());
    int len = val.length();
    for (int i = 0; i < len; i++) {
      char c = val.charAt(i);
      if (c != '\\') {
        buf.append(c);
      }
      else {
        // handle escaped-quote 
        if (i < len - 1 && val.charAt(i + 1) == '"') {
          buf.append('"');
          i++;
        }
        else {
          buf.append(c);
        }
      }
    }
    return buf.toString();
  }
  
  public static String decodeEscapedString(String val)
  {
    // check for no escapes (simple case)
    if (val.indexOf("\\") < 0) {
      return val;
    }
    
    // convert escaped chars
    StringBuffer buf = new StringBuffer(val.length());
    int len = val.length();
    for (int i = 0; i < len; i++) {
      char c = val.charAt(i);
      // escaped char
      if (c == '\\') {
          i++;
          c = unescape(val.charAt(i));
      }
      buf.append(c);
    }
    return buf.toString();
  }
  
  private static char unescape(char c)
  {
    switch (c) {
    case '"': return '"';
    case '\'': return '\'';
    case '\\': return '\\';
    case 'b': return '\b';
    case 't': return '\t';
    case 'n': return '\n';
    case 'r': return '\r';
    case 'f': return '\f';
    }
    // MD - is this right?  Or throw an error?
    return c;
  }


}

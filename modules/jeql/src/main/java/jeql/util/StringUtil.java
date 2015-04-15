package jeql.util;

public class StringUtil 
{
  public static boolean isWhitespace(String s) {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (! Character.isWhitespace(s.charAt(i)))
          return false;
    }
    return true;    
  }
  
  public static boolean isEqualOrBothNull(String s1, String s2)
  {
    if (s1 == null && s2 == null) return true;
    if (s1 == null || s2 == null) return false;
    return s1.equals(s2);
  }
  

  /**
   *  Returns a <code>String</code> of repeated characters.
   *
   *@param  ch     the character to repeat
   *@param  count  the number of times to repeat the character
   *@return        a <code>String</code> of characters
   */
  public static String stringOf(char ch, int count) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < count; i++) {
      buf.append(ch);
    }
    return buf.toString();
  }

  public static String stringOf(String s, int len)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < len; i++) {
      buf.append(s);
    }
    return buf.toString();
  }

  /**
   * Pads the right
   * @param s
   * @param padStr
   * @param len
   * @return
   */
  public static String pad(String s, String padStr, int len)
  {
    if (s.length() >= len)
      return s;
    String raw = s + stringOf(padStr, len);
    return raw.substring(0, len);
  }
  
  public static String padLeft(String s, String padStr, int len)
  {
    if (s.length() >= len)
      return s;
    String raw = stringOf(padStr, len) + s;
    return raw.substring(raw.length() - len);
  }
  
  public static boolean startsWithIgnoreCase(String input, String prefix)
  {
    if (input.length() < prefix.length()) return false;
    String inputStart = input.substring(0, prefix.length());
    return inputStart.equalsIgnoreCase(prefix);
  }
  
  public static boolean endsWith(String input, String suffix)
  {
    if (input.length() < suffix.length()) return false;
    String inputSuf = input.substring(input.length() - suffix.length());
    return inputSuf.equals(suffix);
  }
  public static boolean endsWithIgnoreCase(String input, String suffix)
  {
    if (input.length() < suffix.length()) return false;
    String inputSuf = input.substring(input.length() - suffix.length());
    return inputSuf.equalsIgnoreCase(suffix);
  }
  
  /**
   *  Line-wraps a string s by inserting CR-LF instead of the first space after the nth
   *  columns.
   */
  public static String wrap(String s, int n) {
      StringBuffer b = new StringBuffer();
      boolean wrapPending = false;
      for (int i = 0; i < s.length(); i++) {
          if (i % n == 0 && i > 0) {
              wrapPending = true;
          }
          char c = s.charAt(i);
          if (wrapPending && c == ' ') {
              b.append("\n");
              wrapPending = false;
          } else {
              b.append(c);
          }
      }
      return b.toString();
  }

}

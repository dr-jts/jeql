package jeql.std.function;

import java.util.ArrayList;
import java.util.List;

import jeql.api.function.FunctionClass;
import jeql.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;


public class StringFunction 
implements FunctionClass
{
  public static String toChar(int chInt) { return String.valueOf((char) chInt); }
  public static String concat(String s1, String s2) { return s1.concat(s2); }
  public static int indexOf(String s, String subStr) { return s.indexOf(subStr); }
  public static int indexOfFromPos(String s, String subStr, int pos) { return s.indexOf(subStr, pos); }
  public static int lastIndexOf(String s, String subStr) { return s.lastIndexOf(subStr); }
  public static int lastIndexOfFromPos(String s, String subStr, int pos) { return s.lastIndexOf(subStr, pos); }
  public static String leftStr(String s, int count)
  {
    if (count < 0) count = 0;
    if (count > s.length())
      count = s.length();
    return s.substring(0, count);
  }
  public static int length(String s) 
  { 
    if (s == null) return 0;
    return s.length(); 
  }
  
  /*
  public static boolean matches(String s, String regex) 
  { 
    return s.matches(regex); 
  }
  */
  //=========  Superseded by RegEx
  
  /**
   * Tests whether a regex matches a subsequence of a string
   * 
   * @param s
   * @param regex
   * @return
   */
  /*
  public static boolean matchesRegion(String s, String regex) 
  { 
    // this could probably be done faster using Matcher directly
    return matchGroup(s, regex, 0).length() > 0; 
  }
  
  public static String match(String s, String regex) 
  {
    return matchGroup(s, regex, 0);
  }
  
  public static String matchGroup(String s, String regex, int group) 
  {
    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);
    boolean isMatched = mat.find();
    if (! isMatched)
      return "";
    return mat.group(group);
  }
  */
  //=============
  
  public static boolean contains(String s, String q)
  {
    return s.indexOf(q) >= 0;
  }
  
  public static String keepChars(String input, String charsToKeep) {
    if (input == null) return null;
    StringBuilder result = new StringBuilder();
    for (char c : input.toCharArray()) {
        if (charsToKeep.indexOf(c) != -1) {
            result.append(c);
        }
    }
    return result.toString();
  }


  public static String remove(String s, String strToRemove)
  {
    if (s == null || strToRemove == null) return s;
    return replaceString(s, strToRemove, "");
  }
  
  public static String removeChars(String input, String charsToRemove) {
    StringBuilder result = new StringBuilder();
    for (char c : input.toCharArray()) {
        if (charsToRemove.indexOf(c) == -1) {
            result.append(c);
        }
    }
    return result.toString();
  }

  public static String replaceAll(String s, String strOld, String strNew)
  {
    if (s == null || strOld == null) return s;
    if (strNew == null) strNew = "";
    
    // replace char
    if (strOld.length() == 1 && strNew.length() == 1)
      return s.replace(strOld.charAt(0), strNew.charAt(0));
    
    return replaceString(s, strOld, strNew);
  }
  
  private static String replaceString(String s, String strOld, String strNew)
  {
    StringBuffer buf = new StringBuffer();
    int pos = 0;
    int sLen = s.length();
    int oldLen = strOld.length();
    while (pos < s.length()) {
      if (pos + oldLen <= sLen 
          && s.substring(pos, pos + oldLen).equals(strOld)) {
          buf.append(strNew);
          pos += oldLen;
      }
      else {
        buf.append(s.charAt(pos));
        pos++;
      }
    }
    return buf.toString();
  }
  
  // TODO: fix to NOT use Regex
  public static String replaceFirst(String s, String regExp, String replacement)
  {
    if (s == null || regExp == null) return s;
    if (replacement == null) replacement = "";
    return s.replaceFirst(regExp, replacement);
  }
  
  /**
   * Splits a string on the given regex, and returns the i'th substring.
   * Named <tt>splitAt</tt> to avoid collision with SPLIT reserved word.
   */
  public static String splitAt(String s, String regex, int index)
  {
    return s.split(regex)[index];
  }
  
  public static List<String> splitByString(String str, String splitRegEx) {
    List<String> items = new ArrayList<String>();
    // TODO: this should not be a regEx, but a plain string
    if (str == null)
      return items;
    String[] splits = str.split(splitRegEx);
    for (int i = 0; i < splits.length; i++) {
      items.add(splits[i]);
    }
    return items;
  }
  
  // DEPRECATED
  public static List<String> splitByRegEx(String str, String splitRegEx) {
    List<String> items = new ArrayList<String>();
    String[] splits = str.split(splitRegEx);
    for (int i = 0; i < splits.length; i++) {
      items.add(splits[i]);
    }
    return items;
  }
  
  public static String rightStr(String s, int count)
  {
    if (count < 0) count = 0;
    int start = s.length() - count;
    if (start < 0) start = 0;
    return s.substring(start, s.length());
  }
  
  public static boolean startsWith(String s, String prefix) 
  { return s.startsWith(prefix); }
  
  public static boolean startsWithIgnoreCase(String s, String prefix) 
  { return StringUtil.startsWithIgnoreCase(s, prefix); }
  
  public static boolean endsWith(String s, String suffix) 
  { return StringUtil.endsWith(s, suffix); }

  public static boolean endsWithIgnoreCase(String s, String suffix) 
  { return StringUtil.endsWithIgnoreCase(s, suffix); }
  
  public static String substring(String s, int beginIndex, int endIndex) 
  { return s.substring(beginIndex, endIndex); }

  public static String substring(String s, int beginIndex) 
  {
    if (s == null) return s;
    return s.substring(beginIndex); 
  }

  public static String tail(String s, int beginIndex) 
  { 
    return substring(s, beginIndex); 
  }
  public static String toLowerCase(String s) 
  { 
    if (s == null) return s;
    return s.toLowerCase(); 
  }
  public static String toUpperCase(String s) 
  { 
    if (s == null) return s;
    return s.toUpperCase(); 
  }
  public static String trim(String s) 
  { 
    if (s == null) return s;
    return s.trim(); 
  }
  
  public static String toString(Object o) 
  { 
    return ValFunction.toString(o); 
  }
  
  public static int count(String s, String charset)
  {
    int n = s.length();
    int count = 0;
    for (int i = 0; i < n; i++) {
      if (charset.indexOf(s.charAt(i)) >= 0)
        count++;
    }
    return count;
  }
  
  public static String repeat(String s, int count)
  {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < count; i++) {
      buf.append(s);
    }
    return buf.toString();
  }
  
  public static String lineSeparator() { return System.getProperty("line.separator"); }
  public static String fileSeparator() { return System.getProperty("file.separator"); }
  
  /**
   * Pads a string <tt>s</tt> on the right with a padding string
   * to bring the total length up to at least <tt>width</tt>.
   * 
   * @param s
   * @param padCh
   * @param width
   * @return
   */
  public static String pad(String s, String padStr, int width)
  {
    return StringUtil.pad(s, padStr, width);
  }
  public static String padLeft(String s, String padStr, int width)
  {
    return StringUtil.padLeft(s, padStr, width);
  }
  
  public static boolean isUpperCase(String s)
  {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (! Character.isUpperCase(s.charAt(i)))
          return false;
    }
    return true;
  }
  public static boolean isLowerCase(String s)
  {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (! Character.isLowerCase(s.charAt(i)))
          return false;
    }
    return true;
  }
  public static boolean isAlphanumeric(String s)
  {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (! Character.isLetterOrDigit(s.charAt(i)))
          return false;
    }
    return true;
  }
  public static boolean isNumeric(String s)
  {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (! Character.isDigit(s.charAt(i)))
          return false;
    }
    return true;
  }
  public static boolean isAlpha(String s)
  {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (! Character.isLetter(s.charAt(i)))
          return false;
    }
    return true;
  }
  public static boolean isWhitespace(String s)
  {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (! Character.isWhitespace(s.charAt(i)))
          return false;
    }
    return true;
  }
  public static String escapeHTML(String s)
  {
    return StringEscapeUtils.escapeHtml(s);
  }
  public static String unescapeHTML(String s)
  {
    return StringEscapeUtils.unescapeHtml(s);
  }
  public static String escapeXML(String s)
  {
    return StringEscapeUtils.escapeXml(s);
  }
  public static String unescapeXML(String s)
  {
    return StringEscapeUtils.unescapeXml(s);
  }
}

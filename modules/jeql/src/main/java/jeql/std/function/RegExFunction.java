package jeql.std.function;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeql.api.annotation.Metadata;
import jeql.api.function.FunctionClass;

public class RegExFunction 
implements FunctionClass
{
  /**
   * Counts the number of occurences of a regex in a string.
   * 
   * @param s
   * @param regex
   * @return
   */
  @Metadata (description = "Counts the number of occurences of a pattern" )
  public static int count(String s, String regex)
  {
    if (s == null) return 0;
    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);
    int count = 0;
    while (mat.find()) {
      count++;
    }
    return count;
  }
  
  /**
   * Tests whether a regex matches a string
   * 
   * @param s
   * @param regex
   * @return
   */
  @Metadata (description = "Tests whether the string matches the regex" )
  public static boolean matches(String s, String regex) 
  { 
    if (s == null) return false;
    return s.matches(regex); 
  }
  /**
   * Tests whether a regex matches a subsequence of a string
   * 
   * @param s
   * @param regex
   * @return
   */
  @Metadata (description = "Tests whether a substring matches the regex" )
  public static boolean find(String s, String regex) 
  { 
    if (s == null) return false;
    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);
    return mat.find();
  }
    
  /**
   * Extracts the first group (1) in the match,
   * or the entire matched pattern (group 0)
   * if no groups are present
   * in the pattern.
   * 
   * @param s
   * @param regex
   * @return
   */
  @Metadata (description = "Extracts a substring which matches the regex or the first regex group, if present" )
  public static String extract(String s, String regex) 
  {
    if (s == null) return null;

    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);
    boolean isMatched = mat.find();
    if (! isMatched)
      return "";
    // if no groups, just return entire string
    if (mat.groupCount() <= 0)
      return mat.group(0);
    return mat.group(1);
  }
  
  /**
   * Extracts the specified matched group.
   * 
   * @param s
   * @param regex
   * @param group index of group (0 is entire pattern)
   * @return the string corresponding to the i'th group in the match
   * @return blank string if no match was found
   */
  @Metadata (description = "Extracts a substring which matches the specified regex group" )
  public static String extract(String s, String regex, int groupNum) 
  {
    if (s == null) return null;
    
    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);
    boolean isMatched = mat.find();
    if (! isMatched)
      return "";
    return mat.group(groupNum);
  }

  /**
   * Splits a string on the given regex, and returns the index'th substring.
   * If the number of splits is less than the index, <tt>null</tt> is returned
   * 
   * @return the i'th split
   * @return null if the index is out of range
   */
  @Metadata (description = "Splits the string on the regex and returns the specified substring" )
  public static String splitAt(String s, String regex, int index)
  {
    if (s == null) return null;
    String[] split = s.split(regex);
    if (index < split.length)
      return split[index];
    return null;
  }

  /**
   * 
   * @param s the string to match in and replace on
   * @param regex the regex to match
   * @param repStr the replacement pattern (may contain references to capturing groups)
   * @return
   */
  @Metadata (description = "Replaces the first substrings matching the regex with the given replacement string" )
  public static String replaceFirst(String s, String regex, String repStr)
  {
    if (s == null) return null;
    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);
    return mat.replaceFirst(repStr);
  }
  
  /**
   * 
   * @param s the string to match in and replace on
   * @param regex the regex to match
   * @param repStr the replacement pattern (may contain references to capturing groups)
   * @return
   */
  @Metadata (description = "Replaces all substrings matching the regex with the given replacement string" )
  public static String replaceAll(String s, String regex, String repStr)
  {
    if (s == null) return null;
    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);
    return mat.replaceAll(repStr);
  }
  
  /**
   * Splits a string by separators determined by matching a regex.
   * 
   * @param str
   * @param regex
   * @return
   */
  public static List<String> splitBySep(String s, String regex) {
    List<String> items = new ArrayList<String>();
    if (s == null) return items;

    String[] splits = s.split(regex);
    for (int i = 0; i < splits.length; i++) {
      items.add(splits[i]);
    }
    return items;
  }
  
  /**
   * Splits a string into the substrings that match
   * a regex.
   * 
   * @param s
   * @param regex
   * @return
   */
  public static List<String> splitByMatch(String s, String regex) {
    List<String> items = new ArrayList<String>();
    if (s == null) return items;

    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(s);

    while (mat.find()) {
      items.add(mat.group());
    }
    return items;
  }
  

}
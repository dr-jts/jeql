package jeql.syntax.util;

public class IdentifierUtil {

  public static String keyIdentifier(String key) {
    if (!isKey(key))
      return key;
    return key.substring(0, key.length() - 1);
  }

  private static boolean isKey(String key) {
    if (key == null)
      return false;
    return key.charAt(key.length() - 1) == ':';
  }
}

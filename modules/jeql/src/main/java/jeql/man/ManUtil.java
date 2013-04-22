package jeql.man;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jeql.api.annotation.ManDoc;

public class ManUtil
{
  public static String description(Class<?> clz)
  {
    ManDoc doc = clz.getAnnotation(ManDoc.class);
    if (doc != null)
      return doc.description();
    return "";
  }
  
  public static String name(Annotation[] anno)
  {
    for (int i = 0; i < anno.length; i++) {
      if (anno[i] instanceof ManDoc) {
        ManDoc doc = (ManDoc) anno[i];
        if (doc != null)
          return doc.name();
      }
    }
    return "";
  }
  
  public static String name(Method m)
  {
    ManDoc doc = m.getAnnotation(ManDoc.class);
    if (doc != null)
      return doc.name();
    return "";
  }
  
  public static String description(Method m)
  {
    ManDoc doc = m.getAnnotation(ManDoc.class);
    if (doc != null)
      return doc.description();
    return "";
  }

}

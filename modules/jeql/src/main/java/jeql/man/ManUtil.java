package jeql.man;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jeql.api.annotation.Metadata;

public class ManUtil
{
  public static String description(Class<?> clz)
  {
    Metadata doc = clz.getAnnotation(Metadata.class);
    if (doc != null)
      return doc.description();
    return "";
  }
  
  public static String name(Annotation[] anno)
  {
    for (int i = 0; i < anno.length; i++) {
      if (anno[i] instanceof Metadata) {
        Metadata doc = (Metadata) anno[i];
        if (doc != null)
          return doc.name();
      }
    }
    return "";
  }
  
  public static String name(Method m)
  {
    Metadata doc = m.getAnnotation(Metadata.class);
    return (doc == null) ? "" : doc.name();
  }
  
  public static String description(Method m)
  {
    Metadata doc = m.getAnnotation(Metadata.class);
    return (doc == null) ? "" : doc.description();
  }

  public static boolean isMultiple(Method m)
  {
    Metadata doc = m.getAnnotation(Metadata.class);
    return (doc == null) ? false :doc.isMultiple();
  }
  public static boolean isRequired(Method m)
  {
    Metadata doc = m.getAnnotation(Metadata.class);
    return (doc == null) ? false :doc.isRequired();
  }

}

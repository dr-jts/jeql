package jeql.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodUtil
{
  public static void invokeVoidSafe(Object obj, String methodName, Class paramClass, Object param)
  {
    try {
      Method method = obj.getClass().getMethod(methodName, paramClass);
      method.invoke(obj, param);
    } catch (SecurityException e) {
      // ignored
    } catch (NoSuchMethodException e) {
      // ignored
    }
    catch (IllegalArgumentException e) {
      // ignored
    }
    catch (IllegalAccessException e) {
      // ignored
    }
    catch (InvocationTargetException e) {
      // ignored
    }

  }
}

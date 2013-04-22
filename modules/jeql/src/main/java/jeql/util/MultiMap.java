package jeql.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A Map which can contain multiple values for a given key
 * 
 * @author Martin Davis
 *
 */
public class MultiMap
  implements Map
{

  Map map = null;

  public MultiMap(Map baseMap) {
    map = baseMap;
  }

  public void clear() {
    map.clear();
  }

  public Collection values() {
    return map.values();
  }

  public Set keySet() {
    return map.keySet();
  }

  public int size() {
    return map.size();
  }

  public Object get(Object key) {
    return map.get(key);
  }

  public Object remove(Object key) {
    return map.remove(key);
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  public Set entrySet() {
    return map.entrySet();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public Object put(Object key, Object obj) {
    Object value = map.get(key);
    Object prev;
    if (value == null) {
       prev = map.put(key, obj);
    }
    else if (value instanceof Collection)
    {
      prev = value;
      ((Collection) value).add(obj);
    }
    else {
      List l = new ArrayList();
      l.add(value);
      l.add(obj);
      prev = map.put(key, l);
    }
    return prev;
  }

  public void putAll(Map map) {
    for (Iterator i = map.keySet().iterator(); i.hasNext();) {
      Object key = i.next();
      put(key, map.get(key));
    }
  }
 
  
}

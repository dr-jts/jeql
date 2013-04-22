package jeql.engine.query;

import jeql.util.TypeUtil;

/**
 * An ordered vector of values.
 * Tuples are fully comparable.
 * 
 * @author Martin Davis
 *
 */
public class Tuple
  implements Comparable
{
  private Object[] values;

  public Tuple(int size) {
    values = new Object[size];    
  }
  
  public void setValue(int i, Object o) { values[i] = o; }
   
  public Object getValue(int i) { return values[i]; }
   
  public int size() { return values.length; }

  public int compareTo(Object o)
  {
    Tuple tup = (Tuple) o;
    
    if (size() != tup.size())
      throw new IllegalArgumentException("Tuples are different sizes");
    
    for (int i = 0; i < size(); i++) {
      Object v1 = getValue(i);
      Object v2 = tup.getValue(i);
      int comp = TypeUtil.compareValue(v1, v2);
      if (comp != 0)
        return comp;
    }
    return 0;
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append('<');
    for (int i = 0; i < size(); i++) {
      if (i > 0)
        buf.append(", ");
      buf.append(getValue(i));
    }
    buf.append('>');
    return buf.toString();
  }
}

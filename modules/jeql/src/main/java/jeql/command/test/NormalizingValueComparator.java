package jeql.command.test;

import java.util.Comparator;

import jeql.util.TypeUtil;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Compares {@link Geometry} objects using their normalized value.
 * All other values are compared using the default comparision.
 * 
 * @author Martin Davis
 *
 */
public class NormalizingValueComparator
  implements Comparator
{

  public NormalizingValueComparator() {
    super();
  }

  public int compare(Object v1, Object v2)
  {
    if (v1 instanceof Geometry) {
      Geometry g1 = (Geometry) ((Geometry) v1).clone();
      Geometry g2 = (Geometry) ((Geometry) v2).clone();
      g1.normalize();
      g2.normalize();
      return g1.compareTo(g2);
    }
    return TypeUtil.compareValue(v1, v2);
  }
}

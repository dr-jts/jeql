package jeql.syntax;

import jeql.engine.Scope;

import org.locationtech.jts.geom.Geometry;



/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Martin Davis
 * @version 1.0
 */
public class GeometryConstantNode
  extends ParseTreeNode
{
  private Geometry value = null;
  
  public GeometryConstantNode(Geometry g) 
  {
    this.value = g;
  }

  public Class getType(Scope scope)
  {
    return Geometry.class;
  }
   
  public void bind(Scope scope)
  {
  }

  public Object eval(Scope scope)
  {
    return value;
  }
  
}
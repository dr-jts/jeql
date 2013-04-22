package jeql.engine;

import jeql.api.error.JeqlException;
import jeql.syntax.ParseTreeNode;
import jeql.util.TypeUtil;

/**
 * Error caused by attempting to convert one type to another 
 * which is not value-compatible.
 * 
 * @author Martin Davis
 *
 */
public class TypeConversionException 
  extends JeqlException
{

  public TypeConversionException(String msg) {
    super(msg);
  }

  public TypeConversionException(ParseTreeNode node, String msg) {
    super(node, msg);
  }
  
  public TypeConversionException(int line, String msg) {
    super(line, msg);
  }

  public TypeConversionException(Object srcVal, Class destClass) {
    this("Can't convert value " + srcVal + " to type " 
        + TypeUtil.nameForType(destClass));
  }

}

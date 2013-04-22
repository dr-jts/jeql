package jeql.syntax.operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeql.engine.Scope;
import jeql.syntax.ParseTreeNode;

public class RegExOperation 
  extends Operation
{
  private String cacheKey = null;
  private Pattern cacheValue = null;
  
  public RegExOperation(ParseTreeNode e1, ParseTreeNode e2, String opStr, int opCode) {
    super(e1, e2, opStr, opCode);
  }

  public Class getType(Scope scope)
  {
    return Boolean.class;
  }

  
  public Object compute(Object o1, Object o2)
  {
    Object v1 = coerce(o1, String.class);
    require(o2, String.class);
    
    String s1 = (String) v1;
    String s2 = (String) o2;
    
    // only compile if necessary
    Pattern pat = getPattern(s2);
    
    Matcher matcher = pat.matcher(s1);
    
    boolean result = false;
    switch (opCode) {
    case RE_MATCH:
      result = matcher.matches();
      break;
    case RE_FIND:
      result = matcher.find();
    }
    return new Boolean(result);
  }

  private Pattern getPattern(String patStr)
  {
    // use reference equality for simplicity and speed
    if (patStr != cacheKey) {
    // cache miss - compile this and save in cache
     Pattern pattern = Pattern.compile(patStr);
     cacheKey = patStr;
     cacheValue = pattern;
    }
    return cacheValue;
  }
}

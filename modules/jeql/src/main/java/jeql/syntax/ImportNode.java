package jeql.syntax;

import java.util.ArrayList;
import java.util.List;

import jeql.engine.CompilationException;
import jeql.engine.Scope;

/**
 * Models import statements of the form:
 * <pre>
 *  import comp{.comp}.(comp | *}
 * <pre>
 * The constraint that * can only occur as the last component must
 * be checked.
 * 
 * @author Martin Davis
 *
 */
public class ImportNode 
  extends ParseTreeNode
{
  private static final String WILDCARD = "*";
  
  private List path = new ArrayList();
  
  public ImportNode() {
  }

  public void add(String member)
  {
    path.add(member);
  }
  
  public void checkSyntax()
  {
    if (path.size() <= 0)
      throw new CompilationException(this, "Import path must be specified");
    
    for (int i = 0; i < path.size(); i++) {
      String member = (String) path.get(i);
      if (member.equals(WILDCARD) && i < path.size() - 1)
        throw new CompilationException(this, "Invalid import path");
      
      // for now, until issue of determining classes in package is resolved
      if (member.equals(WILDCARD))
        throw new CompilationException(this, "Import paths must not contain wildcards");

    }
  }
  
  public String getClassname()
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < path.size(); i++) {
      String member = (String) path.get(i);
      if (i > 0)
        buf.append(".");
      buf.append(member);
    }
    return buf.toString();
  }
  
  public Class getType(Scope scope)
  {
    throw new UnsupportedOperationException();
  }
  
  public void bind(Scope scope)
  {
    throw new UnsupportedOperationException();
  }

  public Object eval(Scope scope)
  {
    throw new UnsupportedOperationException();
  }

}

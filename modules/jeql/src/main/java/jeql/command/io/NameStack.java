package jeql.command.io;

import java.util.Iterator;
import java.util.Stack;

/**
 * A stack of names, which can be read out as a single string 
 * with separator characters.
 * 
 * @author Martin Davis
 *
 */
public class NameStack 
{
  private String sepStr = " ";
  private Stack names = new Stack();
  private String currentNames = "";
  
  public NameStack() {
  }

  public NameStack(String sepStr) {
    setSeparator(sepStr);
  }

  public void setSeparator(String sepStr)
  {
    this.sepStr = sepStr;
  }
  
  public void push(String name)
  {
    names.push(name);
    currentNames = listNames();
  }
  
  public void pop()
  {
    names.pop();
    currentNames = listNames();
 }
  
  public String getNameList()
  {
    return currentNames; 
  }
  
  private String listNames()
  {
    StringBuffer buf = new StringBuffer();
    boolean isFirst = true;
    for (Iterator i = names.iterator(); i.hasNext(); ) {
      if (! isFirst) {
        buf.append(sepStr);
      }
      String name = (String) i.next();
      buf.append(name);
      isFirst = false;
    }
    return buf.toString();
  }
  
  public String toString() { return getNameList(); }
}

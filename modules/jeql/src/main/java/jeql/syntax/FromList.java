package jeql.syntax;

import java.util.ArrayList;
import java.util.List;

public class FromList 
{

  private List items = new ArrayList();
   
  public FromList() {
  }

  public void add(FromItem fromItem) { items.add(fromItem); }
  
  public List getItems() { return items; }
  
  public int size() { return items.size(); }
  
  public FromItem getItem(int i) { return (FromItem) items.get(i); }
}

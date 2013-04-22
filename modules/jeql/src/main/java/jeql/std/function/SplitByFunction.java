package jeql.std.function;

import java.util.ArrayList;
import java.util.List;

import jeql.api.function.FunctionClass;
import jeql.api.function.SplittingFunction;

public class SplitByFunction
implements FunctionClass
{
  public static List<Integer> index(int n)
  {
    List<Integer> lst = new ArrayList<Integer>();
    for (int i = 0; i < n; i++) {
      lst.add(i);
    }
    return lst;
  }
  
  public static List<Integer> range(int index1, int index2) {
    List<Integer> members = new ArrayList<Integer>();

    if (index1 <= index2) {
      for (int i = index1; i <= index2; i++) {
        members.add(new Integer(i));
      }
    }
    else {
      for (int i = index2; i <= index1; i++) {
        members.add(new Integer(i));
      }        
    }
    return members;
  }
}


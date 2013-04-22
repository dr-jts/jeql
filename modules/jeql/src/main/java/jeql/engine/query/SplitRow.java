package jeql.engine.query;

import jeql.api.row.Row;

public class SplitRow 
implements Row
{
  private Row row;
  private Object splitValue;
  private Integer splitIndex;
  
  public SplitRow(Row row, Object splitValue, int splitIndex) {
    this.row = row;
    this.splitValue = splitValue;
    this.splitIndex = new Integer(splitIndex + 1);
  }

  public Integer getSplitIndex() { return splitIndex; }
  
  public Object getSplitValue() { return splitValue; } 
  
  public Object getValue(int i)
  {
    return row.getValue(i);
  }
  
  public int size() {
    return row.size();
  }

}

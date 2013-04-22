package jeql.jts.geodetic;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;

class MultiCoordinateList
{
  private List coordLists = new ArrayList();
  private boolean allowRepeated = true;
  private int currListIndex = 0;
  
  public MultiCoordinateList()
  {
    
  }
  
  public MultiCoordinateList(boolean allowRepeated)
  {
    this.allowRepeated = allowRepeated;
  }
  
  public void add(Coordinate c)
  {
    CoordinateList coordList = current();
    coordList.add(c, allowRepeated);
  }
  
  public void finish()
  {
    currListIndex++;
  }
  
  private CoordinateList current()
  {
    if (currListIndex > coordLists.size() - 1) {
      CoordinateList cl = new CoordinateList();
      coordLists.add(cl);
    }
    return (CoordinateList) coordLists.get(currListIndex);
  }
  
  public Coordinate[][] toCoordinateArrays()
  {
    Coordinate[][] arrays = new Coordinate[coordLists.size()][];
    for (int i = 0; i < coordLists.size(); i++) {
      arrays[i] = ((CoordinateList) coordLists.get(i)).toCoordinateArray();
    }
    return arrays;
  }
  

}
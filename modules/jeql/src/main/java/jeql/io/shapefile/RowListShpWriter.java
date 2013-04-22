package jeql.io.shapefile;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;

import com.vividsolutions.jts.geom.Geometry;

public class RowListShpWriter 
{
  public RowListShpWriter()
  {
    
  }
  
  public void write(RowList rowList, String shpfile)
      throws Exception {
    int geomIndex = ShapefileWriter.findFirstGeometryIndex(rowList.getSchema());

    //URL url = new URL("file", "localhost", shpfilePathNoExt + ".shp");
    ShpStreamWriter shpWriter = new ShpStreamWriter(shpfile);
    RowIterator it = rowList.iterator();
    while (true) {
      Row row = it.next();
      if (row == null) break;
      Geometry geom = (Geometry) row.getValue(geomIndex);
      shpWriter.write(geom);
    }
    shpWriter.close();

    // write index
    // *
    //String shxPath = shpfilePathNoExt + ".shx";
    //shpWriter.writeIndex(gc, coordDim, shxPath);
    // */
  }

}

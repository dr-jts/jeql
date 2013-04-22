package jeql.io.shapefile;

import jeql.api.error.ExecutionException;
import jeql.api.row.ArrayRowList;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.util.IOUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A writer which writes a {@link Table} to a shapefile (which consists of a
 * .shp and a .dbf file). Currently the .shx file is not written
 */
public class ShapefileWriter 
{
  /**
   * Returns: <br>
   * 2 for 2d (default) <br>
   * 4 for 3d - one of the oordinates has a non-NaN z value <br>
   * (3 is for x,y,m but thats not supported yet) <br>
   * 
   * @param g
   *          geometry to test - looks at 1st coordinate
   **/
  public static int determineCoordinateDimension(Geometry g) {
    Coordinate[] cs = g.getCoordinates();

    for (int t = 0; t < cs.length; t++) {
      if (!(Double.isNaN(cs[t].z))) {
        return Shapefile.XYZM;
      }
    }
    return Shapefile.XY;
  }

  public static int findFirstGeometryIndex(RowSchema schema) {
    for (int i = 0; i < schema.size(); i++) {
      if (schema.getType(i) == Geometry.class)
        return i;
    }
    return -1;
  }


  /**
   * Creates a new writer
   */
  public ShapefileWriter() {
  }

  /**
   * Writes a table to a shapefile (2d, 3d or 4d).
   * 
   * @param tbl
   *          table to write
   * @param shpfilePath
   *          file path to write to (*.shp)
   * @param prj
   *          projection string to write, or <tt>null</tt>
   */
  public void write(Table tbl, String shpfilePath, String prj) throws Exception {

    int dotLoc = shpfilePath.lastIndexOf(".");

    if (dotLoc == -1) {
      throw new IllegalArgumentException("Filename must end in '.shp'");
    }

    String pathNoExt = shpfilePath.substring(0, dotLoc); 
    String dbfname = pathNoExt + ".dbf";
    String shpname = pathNoExt + ".shp";
    String prjName = pathNoExt + ".prj";

    writeShpDbfStream(tbl, shpname, dbfname);
    if (prj != null) {
      writePrj(prjName, prj);
    }
  }

  private void writeShpDbf(Table tbl, String shpname, String dbfname)
  throws Exception
  {
    // memorize the rowlist to allow determining size
    ArrayRowList memRowList = new ArrayRowList(tbl.getRows());

    TableDbfWriter dbfWriter = new TableDbfWriter();
    dbfWriter.write(memRowList, dbfname);

    RowListShpWriter shpWriter = new RowListShpWriter();
//    TableShpWriter shpWriter = new TableShpWriter();
    shpWriter.write(memRowList, shpname);
  }
  
  private void writePrj(String filename, String prj) {
    IOUtil.writeToFileNoThrow(filename, prj);
  }
  
  private void writeShpDbfStream(Table tbl, String shpname, String dbfname)
  throws Exception
  {
    // memorize the rowlist to allow determining size
    ArrayRowList rowList = new ArrayRowList(tbl.getRows());

    int geomIndex = ShapefileWriter.findFirstGeometryIndex(rowList.getSchema());
    if (geomIndex < 0) {
      throw new ExecutionException("No geometry column in table");
    }
    
    ShpStreamWriter shpWriter = new ShpStreamWriter(shpname);
    
    RowIterator it = rowList.iterator();
    while (true) {
      Row row = it.next();
      if (row == null) break;
      Geometry geom = (Geometry) row.getValue(geomIndex);
      shpWriter.write(geom);
      
      // Write dbf row here 
    }
    shpWriter.close();

    // for now only
    TableDbfWriter dbfWriter = new TableDbfWriter();
    dbfWriter.write(rowList, dbfname);
  }
  

}


package jeql.io.shapefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jeql.api.error.ExecutionException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.util.TypeUtil;

import org.geotools.dbffile.DbfFile;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * ShapefileRowInputStream reads Shapefiles.
 * DBF files are optional - if one is not present
 * only the geometry is read. 
 */
public class ShapefileRowIterator 
implements RowIterator 
{
  private Shapefile shapeStream;
  private DbfFile dbf;
  private int count = 0;
  private int numDbfFields;
  private GeometryFactory geomFactory = new GeometryFactory();
  private RowSchema schema;

  public ShapefileRowIterator() {
  }

  public RowSchema getSchema() { return schema; }
  
  public void open(String shpFilename)
  throws Exception
  {
    open(shpFilename, true);
  }
  
  public void open(String shpFilename, boolean readDBF)
    throws Exception
  {
    // System.out.println("separator:" + File.separatorChar);
    int sepLoc = shpFilename.lastIndexOf(File.separatorChar);

    String path = shpFilename.substring(0, sepLoc + 1); // ie. "/data1/hills.shp" -> "/data1/"
    String filename = shpFilename.substring(sepLoc + 1); // ie. "/data1/hills.shp" -> "hills.shp"

    int dotLoc = filename.lastIndexOf(".");

    if (dotLoc == -1) {
      throw new IllegalArgumentException("Filename must end in '.shp'");
    }

    String filenameWithoutExtension = filename.substring(0, dotLoc); // ie. "hills.shp" -> "hills."
    String dbfFileName = path + filenameWithoutExtension + ".dbf";
    if (! readDBF) {
      dbfFileName = null;
    }
    open(shpFilename, dbfFileName);
  }
  
  /**
   * Opens a shapefile and optional DBF file.
   * 
   * @param shpFilename
   * @param dbfFilename name of dbf file, or null if not to be read
   * @throws Exception
   */
  private void open(String shpFilename, String dbfFilename)
    throws Exception
  {
    shapeStream = getShapefile(shpFilename);
    shapeStream.readStream(geomFactory);

    // try to open DBF file if given, but continue even if it is not present
    dbf = null;
    if (dbfFilename != null) {
      try {
        dbf = getDbfFile(dbfFilename);
      } catch (FileNotFoundException ex) {
        // null indicates no dbf present
        dbf = null;
      }
    }
    
    createSchema();
  }

  private void createSchema()
  {
    numDbfFields = 0;
    if (dbf != null)
      numDbfFields = dbf.getNumFields();
    
    schema = new RowSchema(numDbfFields + 1);
    // fill in schema
    schema.setColumnDef(0, "GEOMETRY", Geometry.class);
    
    // dbf fields are not added if no dbf is present
    if (dbf == null)
      return;
    
    for (int j = 0; j < numDbfFields; j++) {
      String typename = dbf.getFieldType(j);
      schema.setColumnDef(j + 1, dbf.getFieldName(j), TypeUtil.typeForName(typename));
    }
  }
  
  public Row next()
  {
    try {
      return nextRaw();
    }
    catch (IOException ex) {
      throw new ExecutionException(ex);
    }
    
  }
  
  public Row nextRaw() throws IOException 
  {
    Geometry geom = shapeStream.next();
    if (geom == null)
      return null;
    
    BasicRow row = new BasicRow(schema.size());
    row.setValue(0, geom);

    if (dbf == null)
      return row;
    
    StringBuffer s = dbf.getNextDbfRec();
    count++;

    for (int y = 0; y < numDbfFields; y++) {
      try {
        row.setValue(y + 1, dbf.ParseRecordColumn(s, y));
      } catch (Exception ex) {
        /**
         * don't propagate exceptions from datatype problems
         * what *should* happen is that ParseRecordColumn does not throw any exceptions
         * but simply returns a null value of the right type
         *
         */
      }
    }

    return row;
  }

  public RowSchema getFeatureSchema() {
    return schema;
  }

  public void close() throws IOException 
  {
    shapeStream.close();
    if (dbf != null)
        dbf.close(); 
  }

  protected Shapefile getShapefile(String shpfileName)
      throws Exception {
    java.io.InputStream in = new FileInputStream(shpfileName);
    Shapefile shpfile = new Shapefile(in);
    return shpfile;
  }

  protected DbfFile getDbfFile(String dbfFileName)
      throws Exception {
    DbfFile dbf = new DbfFile(dbfFileName);
    return dbf;
  }

}

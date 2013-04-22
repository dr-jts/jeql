package jeql.command.io.kml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import jeql.api.error.JeqlException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.command.io.TableFileReaderCmd;
import jeql.command.io.TextFileReaderCmd;
import jeql.engine.Scope;
import jeql.io.InputSource;

import com.vividsolutions.jts.geom.Geometry;

public class KMLReaderCommand 
extends TextFileReaderCmd
{
  
  public KMLReaderCommand() 
  {
  }

  protected Table read(Scope scope, InputSource src)
  throws IOException
  {
    RowList rs = new KMLRowList(new KMLFileSource(src.getSourceName()));
    tbl = new Table(rs);
    return tbl;
  }

}

class KMLRowList implements RowList {
  private KMLFileSource src;

  private RowSchema schema;

  public KMLRowList(KMLFileSource src) {
    this.src = src;
    
    schema = new RowSchema(5);
    schema.setColumnDef(0, KMLCol.GEOMETRY, Geometry.class);
    schema.setColumnDef(1, KMLCol.KML_NAME, String.class);
    schema.setColumnDef(2, KMLCol.KML_DESCRIPTION, String.class);
    schema.setColumnDef(3, KMLCol.KML_STYLEURL, String.class);
    schema.setColumnDef(4, KMLCol.KML_FOLDER_NAME, String.class);
  }

  public RowSchema getSchema() {
    return schema;
  }

  public RowIterator iterator() {
    return new KMLRowIterator(schema, src);
  }

  private static class KMLRowIterator implements RowIterator {
    private KMLFileSource src;
    private RowSchema schema;
    private KMLObjectReader kmlObjReader = null;
    private boolean isClosed = false;

    public KMLRowIterator(RowSchema schema, KMLFileSource src) {
      this.schema = schema;
      this.src = src;
      init();
    }

    public RowSchema getSchema() {
      return schema;
    }

    private void init() {
      if (isClosed)
        return;
      if (kmlObjReader != null)
        return;
      try {
        kmlObjReader = new KMLObjectReader();
        Reader rdr = src.createReader();
        kmlObjReader.open(rdr);
      } 
      catch (Exception ex) {
        throw new JeqlException(ex);
      }
    }

    public Row next() 
    {
      init();
      Placemark pm = null;
      try {
        pm = kmlObjReader.next();
      }
      catch (Exception ex) {
        throw new JeqlException(ex);        
      }
      finally {
        // if at end, can close input
        if (pm == null) {
          close();
          return null;
        }
      }
      return createRow(pm);
    }

    private void close() {
      if (kmlObjReader != null) {
        kmlObjReader.close();
      }
      kmlObjReader = null;
      isClosed = true;
    }

    private Row createRow(Placemark pm) {
      BasicRow row = new BasicRow(schema.size());
      row.setValue(0, pm.geom);
      row.setValue(1, pm.name);
      row.setValue(2, pm.description);
      row.setValue(3, pm.styleUrl);
      row.setValue(4, pm.folder);
      return row;
    }
  }
}


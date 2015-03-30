package jeql.command.db.sde;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.engine.ExecutionException;
import jeql.std.geom.GeomFunction;

import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeCoordinateReference;
import com.esri.sde.sdk.client.SeError;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeFilter;
import com.esri.sde.sdk.client.SeLayer;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.esri.sde.sdk.client.SeShapeFilter;
import com.esri.sde.sdk.client.SeSqlConstruct;
import com.esri.sde.sdk.client.SeTable;
import com.esri.sde.sdk.geom.SeGeometry;
import com.esri.sde.sdk.geom.SeGeometrySource;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public class SdeRowList implements RowList {
  private static final int FETCH_SIZE = 1000;
  private String connectString;
  private String user;
  private String password;
  private SdeRowMapper rowMapper;
  private String sql;

  private String[] columns = null;
  private String datasetName = null;
  private String condition = null;

  private SeConnection conn = null;
  private SeQuery query = null;
  private RowSchema schema;
  private boolean isRead = false;
  private Geometry filterGeom;
  private String spatialCol;
  private String filterMethodName;

  public SdeRowList(String connectString, String user, String password,
      String sql, String spatialCol, String filterMethodName, Geometry filterGeom, SdeRowMapper rowMapper) {
    this.connectString = connectString;
    this.user = user;
    this.password = password;
    this.sql = sql;
    this.spatialCol = spatialCol;
    this.filterMethodName = filterMethodName;
    this.filterGeom = filterGeom;
    this.rowMapper = rowMapper;
    open();
  }

  private void open() {
    SdeUtil.checkSdePresent();

    SdeConnectionUrlParser urlParser = new SdeConnectionUrlParser(connectString);

    SdeSqlParser parser = new SdeSqlParser();
    parser.parse(sql);
    columns = parser.getColumns();
    datasetName = parser.getDataset();
    condition = parser.getCondition();

    try {
      conn = new SeConnection(urlParser.getServer(), urlParser.getInstance(),
          urlParser.getDatabase(), user, password);
      //dumpConnInfo(conn);
      
      if (columns[0].equals("*")) {
        columns = SdeUtil.fetchColumnNames(new SeTable(conn, datasetName));
      }

      SeSqlConstruct construct;
      if (condition != null) {
        construct = new SeSqlConstruct(datasetName, condition);
      } else {
        construct = new SeSqlConstruct(datasetName);
      }
      query = new SeQuery(conn, columns, construct);
      if (spatialCol != null && filterGeom != null) {
        addSpatialFilter(conn, datasetName, spatialCol, filterMethodName, filterGeom, query);
      }
      query.prepareQuery();

      // set spatial constraints here if required

      query.execute();
      schema = rowMapper.getSchema(query);
    } catch (SeException ex) {
      close();
      throw new ExecutionException(SdeUtil.seErrDesc(ex));
    }
  }

  private static void dumpConnInfo(SeConnection conn) throws SeException {
    SeConnection.SeStreamSpec spec = conn.getStreamSpec();
    System.out.println("MinBufSize: " + spec.getMinBufSize());
  }
  
  private static void addSpatialFilter(SeConnection conn, String tableName, String spatialCol, String filterMethodName, Geometry geom, SeQuery query) throws SeException {
    SeLayer seLayer = new SeLayer(conn, tableName, spatialCol);
    SeCoordinateReference cr = seLayer.getCoordRef();
    //String spatCol = seLayer.getSpatialColumn();
    //Geometry geom = parseGeom(wkt);
    
    SeShape shape = new SeShape(cr);
    shape.generateRectangle(SdeUtil.toExtent(geom));
    //int method = SeFilter.METHOD_AI_OR_ET;
    int method = SdeUtil.filterMethod(filterMethodName);
    //int method = SeFilter.METHOD_ENVP;
    SeFilter filter = new SeShapeFilter(tableName, spatialCol, shape, method);
    query.setSpatialConstraints(SeQuery.SE_OPTIMIZE, false, new SeFilter[] { filter } );
  }

  private static Geometry parseGeom(String wkt) {
    Geometry geom;
    try {
      geom = GeomFunction.fromWKT(wkt);
    }
    catch (ParseException ex) {
      throw new ExecutionException(ex);
    }
    return geom;
  }
  
  public RowSchema getSchema() {
    return schema;
  }

  public RowIterator iterator() {
    if (isRead)
      throw new ExecutionException("Attempt to re-read streamed SDE layer ("
          + datasetName + ")");
    isRead = true;
    return new SdeRowIterator(this);
  }

  private void close() {
    try {
      if (query != null)
        query.close();
    } catch (Exception ex) {
      // eat this exception - nothing we can do about it now
    }
    query = null;
    try {
      if (conn != null)
        conn.close();
      conn = null;
    } catch (Exception ex) {
      // eat this exception - nothing we can do about it now
    }
  }

  // =============================================

  private static class SdeRowIterator implements RowIterator {
    private SdeRowList dbRL;

    private RowSchema schema = null;

    public SdeRowIterator(SdeRowList dbRL) {
      this.dbRL = dbRL;
      schema = dbRL.getSchema();
    }

    public RowSchema getSchema() {
      return schema;
    }

    public Row next() {
      if (dbRL == null)
        return null;

      Row row = null;
      try {
        SeRow sdeRow = dbRL.query.fetch();
        if (sdeRow != null) {
          row = dbRL.rowMapper.createRow(dbRL.schema, sdeRow);
        }
        // else drop through and close the rowlist
      } catch (Exception ex) {
        throw new ExecutionException(ex.getMessage());
      } finally {
        if (row == null) {
          dbRL.close();
          dbRL = null;
        }
      }
      return row;
    }

  }
}

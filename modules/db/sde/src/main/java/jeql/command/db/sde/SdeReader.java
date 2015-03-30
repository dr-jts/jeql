package jeql.command.db.sde;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.table.Table;
import jeql.command.db.DbCommandBase;
import jeql.engine.*;

public class SdeReader extends DbCommandBase {
  
  
  
  protected String sql;
  protected Table result;
  private Geometry filterGeom = null;
  private String spatialCol;
  private String filterMethodName = "ENVP";

  public SdeReader() {

  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public void setSpatialCol(String spatialCol) {
    this.spatialCol  = spatialCol;
  }

  public void setFilter(Geometry geom) {
    this.filterGeom  = geom;
  }

  public void setFilterMethod(String filterMethodName) {
    this.filterMethodName = filterMethodName;
  }
  
  public Table getDefault() {
    return result;
  }

  public void execute(Scope scope) throws Exception {

    executeQuery(new SdeRowMapper());
  }

  protected void executeQuery(SdeRowMapper rowMapper) {
    if (filterGeom != null && spatialCol == null) {
      throw new ExecutionException("Spatial column not specified");
    }
    result = new Table(new SdeRowList(url, user, password, sql, spatialCol, filterMethodName, filterGeom, rowMapper));
  }
}
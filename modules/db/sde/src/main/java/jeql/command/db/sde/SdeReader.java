package jeql.command.db.sde;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.table.Table;
import jeql.command.db.DbCommandBase;
import jeql.engine.*;

/**
 * Parameters:
 * <h3>url:</h3>
 * 
 * Format:  <code>  server : port [ : database ] </code> 
 * 
 * <h3>sql:</h3>
 * Examples:
 * <pre>
 *  select * from LAYER
 *  select COL1, COL2 from LAYER
 *  select COL1, COL2 from LAYER where COL1 = 1
 * </pre>
 * 
 * <h3>spatialCol:</h3>
 * Name of spatial column in layer
 * 
 * <h3>filterMethod:</h3>
 * Optional SDE Filter Method name. Default is ENVP.  Values include:
 * ENVP, ENVP_BY_GRID, II, AI_NO_ET, AI_OR_ET, ET_OR_AI, PC, SC, etc...
 * 
 * 
 *  <h3>filter:</h3>
 *  A Geometry to filter with (currently only the envelope is used)
 *  
 *  Examples:
 *  <pre>
 *  SdeReader t  url: "x.y.z:5555" user: "xxx"  password: "yyy"
 *    sql: "select * from SCHEMA.LAYER ";
 *    
 *    
 *  SdeReader t  
 *    url: "x.y.z:5555" user: "xxx"  password: "yyy"
 *    sql: "select * from SCHEMA.LAYER "
 *    spatialCol: "GEOMETRY"
 *    filterMethod: "ENVP_BY_GRID"
 *    filter: POLYGON ((2024991 489738, 2024991 490282, 2025535 490282, 2025535 489738, 2024991 489738))
 *    ;
 *  </pre>
 *  
 * 
 * @author mbdavis
 *
 */
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
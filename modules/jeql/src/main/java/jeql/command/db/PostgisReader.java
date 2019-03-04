package jeql.command.db;

import jeql.command.db.driver.PostgisRowMapper;
import jeql.engine.Scope;

/**
 * 
 * <ul>
 * <li><code>driver:</code> is optional
 * </ul>
 * 
 * Example:
 * 
 * <pre>
 * PostgisReader t driver: "org.postgresql.Driver"
 *	url: "jdbc:postgresql://localhost/dbname"
 *	user: "user"
 *	password: ""
 *	sql: "select ST_AsBinary(geom) geom from some_table " ;
 * </pre>
 * 
 * @author mbdavis
 *
 */
public class PostgisReader 
  extends DbReader 
{
	public static final String URL_PREFIX = "jdbc:postgresql://";

	public PostgisReader()
	{
		super();
		jdbcDriver = "org.postgresql.Driver";
	}
  
  public void execute(Scope scope) throws Exception 
  {
    // TODO: should check for correct url prefix here
    executeQuery(new PostgisRowMapper());
  }

}

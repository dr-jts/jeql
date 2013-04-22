package jeql.command.db;

import jeql.command.db.driver.PostgisRowMapper;
import jeql.engine.Scope;

/**
 * 
 * <ul>
 * <li>jdbcClass is optional
 * </ul>
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
	
  /*
	public void execute(Scope scope) throws Exception 
	{
    // should check for correct prefix here
    
		JdbcTemplate template = new JdbcTemplate(jdbcClass, url, user, password);
		RowMapper rowMapper = new PostgisRowMapper();
		RowList rl = template.query(sql, rowMapper);
		result = new Table(rl);
	}
  */
  
  public void execute(Scope scope) throws Exception 
  {
    // TODO: should check for correct url prefix here
    executeQuery(new PostgisRowMapper());
  }

}

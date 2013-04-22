package jeql.command.db;

import jeql.api.function.FunctionClass;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.command.db.driver.JdbcTemplate;
import jeql.command.db.driver.PostgisRowMapper;
import jeql.command.db.driver.RowMapper;

public class PostgisFunction implements FunctionClass 
{

	/**
	 * Executes a SQL query against a PostGIS database, 
	 * and returns the results as a table.
	 * 
	 * The query results are read eagerly (i.e. the
	 * table which is returned is in-memory)
	 * 
	 * @param connectString is in the form "host:port/database"
	 * @param user the username to connect with
	 * @param password the password to use
	 * @param sql the SQL string to execute
	 * @return
	 * @throws Exception
	 */
	public static Table query(String connectString, String user, String password,
			String sql) throws Exception {
		PostGISQuery query = new PostGISQuery();
		query.setConnect(connectString);
		query.setUser(user);
		query.setPassword(password);
		return query.execute(sql);
	}
}

class PostGISQuery 
{
	public static final String JDBC_CLASS = "org.postgresql.Driver";


	private String connectString;

	private String user;

	private String password;

	public PostGISQuery() {

	}

	public void setConnect(String connectString) {
		this.connectString = connectString;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Table execute(String sql) throws Exception {
		String url = PostgisReader.URL_PREFIX + connectString;
		JdbcTemplate template = new JdbcTemplate(JDBC_CLASS, url, user, password);
		RowMapper rowMapper = new PostgisRowMapper();
		RowList rl = template.query(sql, rowMapper);
		return new Table(rl);
	}

	/*
	 private Connection createConnection(String connectString) throws Exception {

	 String url = URL_PREFIX + connectString;
	 registerJDBCDriver(JDBC_CLASS, URL_PREFIX);
	 Connection conn = DriverManager.getConnection(url, user, password);
	 return conn;
	 }

	 private static void registerJDBCDriver(String driverClassName, String driverURL)
	 throws InstantiationException, SQLException, ClassNotFoundException, IllegalAccessException
	 {
	 Class driverClass = Class.forName(driverClassName);
	 Driver currDriver = DriverManager.getDriver(driverURL);
	 if (currDriver != null && currDriver.getClass() == driverClass)
	 return;
	 Driver driver = (Driver) driverClass.newInstance();
	 DriverManager.registerDriver(driver);
	 }
	 */
}

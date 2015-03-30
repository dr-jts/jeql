package jeql.command.db.sde;

public class SdeConnectionUrlParser 
{
	private String server = null;
	private int instance = 0;;
	private String database = null;

	public SdeConnectionUrlParser(String url)
	{
		parseConnectString(url);
	}
	
	/**
	 * Parses a connect string and sets the appropriate instance variables.
	 *  
	 * The connect string has the format
	 * <pre>
	 *   host:port:database
	 * <pre>
	 * The host and port must be specified.  The database is optional. 
	 * 
	 * @param connString
	 */
	private void parseConnectString(String connString)
	{
		String[] parts = connString.split(":");
		
		if (parts.length < 1) return;
		server = parts[0];
		
		if (parts.length < 2) return;
		instance = Integer.parseInt(parts[1]);
		
		if (parts.length < 3) return;
		database = parts[2];
	}

	public String getServer() { return server; }
	public int getInstance() { return instance; }
	public String getDatabase() { return database; }
}

package jeql.command.db.sde;

import jeql.api.table.Table;
import jeql.command.db.DbCommandBase;
import jeql.engine.*;

public class SdeReader 
extends DbCommandBase
{
	protected String sql;
	protected Table result;
	
	public SdeReader()
	{
		
	}
	
	public void setSql(String sql)
	{
		this.sql = sql;
	}
	
	public Table getDefault()
	{
		return result;
	}
	
	public void execute(Scope scope) throws Exception 
	{
		
		executeQuery(new SdeRowMapper());
	}
	
	protected void executeQuery(SdeRowMapper rowMapper)
	{
		result = new Table(new SdeRowList(url, user, password, sql, rowMapper));
	}
}
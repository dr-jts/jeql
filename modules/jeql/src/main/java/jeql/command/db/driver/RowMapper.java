package jeql.command.db.driver;

import java.sql.ResultSet;
import java.sql.SQLException;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;

public interface RowMapper 
{
	RowSchema getSchema(ResultSet rs) 	throws SQLException;
	Row createRow(RowSchema schema, ResultSet rs) 	throws Exception;
}

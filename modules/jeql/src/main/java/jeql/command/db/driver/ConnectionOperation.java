package jeql.command.db.driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ConnectionOperation 
{
	ResultSet execute(Connection conn) throws SQLException;
	void close()  throws SQLException;
}

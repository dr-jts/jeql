package jeql.api.error;

import jeql.syntax.ParseTreeNode;
import java.sql.*;

/**
 * Exceptions which occur at execution time.
 * 
 * @author Martin Davis
 *
 */
public class DbException 
  extends JeqlException
{

  public DbException(String msg) {
    super(msg);
  }

  public DbException(String msg, SQLException sqlEx) {
    super(msg + " (Underlying SQL exception: " + sqlEx.getMessage() + ")");
  }

  public DbException(ParseTreeNode node, String msg) {
    super(node, msg);
  }

  public DbException(int line, String msg) {
    super(line, msg);
  }
  public DbException(Throwable ex) {
    super(ex);
  }
}

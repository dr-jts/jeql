package jeql.command.io;

import jeql.api.command.Command;
import jeql.api.table.Table;

/**
 * An interface for commands which read tables.
 * 
 * @author Martin Davis
 *
 */
public interface TableReaderCmd
  extends Command
{
  Table getTable();
}

package jeql.command.io;

import jeql.api.command.Command;
import jeql.api.table.Table;

/**
 * An interface for commands which write tables
 * 
 * @author Martin Davis
 *
 */
public interface TableWriterCmd 
  extends Command
{
  void setDefault(Table tbl);
}

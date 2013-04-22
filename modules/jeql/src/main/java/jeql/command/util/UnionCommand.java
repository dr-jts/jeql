package jeql.command.util;

import java.util.ArrayList;
import java.util.List;

import jeql.api.annotation.ManDoc;
import jeql.api.command.Command;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.engine.query.UnionRowList;

@ManDoc (
    description = "Unions a set of tables (with identical schemas)"
  )
public class UnionCommand 
implements Command
{
  private List<Table> inputTbls = new ArrayList<Table>();
  private Table unionTbl;
  
  public UnionCommand() {
  }

  @ManDoc (
      description = "Union result"
    )
  public Table getDefault()
  {
   return unionTbl;  
  }
  
  @ManDoc (
      description = "Table to union",
      isRequired = true,
      isMultiple = true
    )
  public void setTbl(Table table)
  {
    inputTbls.add(table);
  }
  
  public void execute(Scope scope)
  {
    if (inputTbls.size() <= 0) return;
    unionTbl = inputTbls.get(0);
    if (inputTbls.size() <= 1) return;
   
    RowList unionRowList = unionTbl.getRows();
    for (int i = 1; i < inputTbls.size(); i++) {
      unionRowList = new UnionRowList(unionRowList, inputTbls.get(i).getRows());
    }
    unionTbl = new Table(unionRowList);
  }

}

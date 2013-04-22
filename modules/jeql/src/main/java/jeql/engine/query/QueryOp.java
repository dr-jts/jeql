package jeql.engine.query;

import jeql.api.row.RowList;

public interface QueryOp 
{
  RowList eval();
}

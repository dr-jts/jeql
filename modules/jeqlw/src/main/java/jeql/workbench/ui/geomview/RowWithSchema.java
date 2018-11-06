package jeql.workbench.ui.geomview;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;

public class RowWithSchema {
  private Row row;
  private RowSchema schema;

  public RowWithSchema(Row row, RowSchema schema) {
    this.row = row;
    this.schema = schema;
  }
  
  public Row row() { return row; }
  public RowSchema schema() { return schema; }
}

package jeql.engine.query;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.syntax.SelectNode;

class LimitRowList implements RowList
{
  private RowList rowStr;

  private int limit;

  private int offset = 0;

  public LimitRowList(RowList rowStr, int limit, int offset)
  {
    this.rowStr = rowStr;
    this.limit = limit;
    this.offset = offset;
  }

  public RowSchema getSchema()
  {
    return rowStr.getSchema();
  }

  public RowIterator iterator()
  {
    return new LimitRowIterator(rowStr, limit, offset);
  }

  private static class LimitRowIterator implements RowIterator
  {
    private RowSchema schema;

    private int limit;

    private int offset = 0;

    private RowIterator it;

    private int rowNum = 0;

    private boolean isStart = true;

    private boolean isDone = false;

    public LimitRowIterator(RowList rowList, int limit, int offset)
    {
      schema = rowList.getSchema();
      this.limit = limit;
      this.offset = offset;
      it = rowList.iterator();
      rowNum = 0;
    }

    public RowSchema getSchema()
    {
      return schema;
    }

    public Row next()
    {
      if (isStart) {
        skipOffset();
        isStart = false;
      }
      if (isDone) {
        return null;
      }
      rowNum++;
      if (limit != SelectNode.NOT_SPECIFIED && rowNum > limit) {
        isDone = true;
        return null;
      }
      return nextRow();
    }

    private void skipOffset()
    {
      for (int i = 0; i < offset; i++) {
        Row row = nextRow();
        if (row == null) {
          return;
        }
      }
    }

    private Row nextRow()
    {
      Row row = it.next();
      if (row == null) {
        isDone = true;
      }
      return row;
    }
  }
}
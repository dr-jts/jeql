package jeql.std.function;

import jeql.api.function.FunctionClass;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;

public class GenerateFunction implements FunctionClass 
{
  private static String COLNAME_1 = "i";

  private static String COLNAME_2 = "j";

  private static String COLNAME_3 = "k";

  /**
   * Generates a sequence [start, start+1, ... end]
   * 
   * @param start
   *          the value to start at
   * @param end
   *          the value to end at
   * @return
   */
  public static Table sequence(int start, int end) {
    return sequence(start, end, 1);
  }

  public static Table sequence(int start, int end, int step) {
    RowSchema schema = new RowSchema(1);
    schema.setColumnDef(0, COLNAME_1, Integer.class);
    ArrayRowList rs = new ArrayRowList(schema);
    for (int i = start; i <= end; i += step) {
      BasicRow row = new BasicRow(schema.size());
      row.setValue(0, new Integer(i));
      rs.add(row);
    }
    Table t = new Table(rs);
    return t;
  }

  public static Table grid(int start0, int end0, int start1, int end1) {
    return grid(start0, end0, 1, start1, end1, 1);
  }

  public static Table grid(int start0, int end0, int step0, int start1,
      int end1, int step1) {
    RowSchema schema = new RowSchema(2);
    schema.setColumnDef(0, COLNAME_1, Integer.class);
    schema.setColumnDef(1, COLNAME_2, Integer.class);
    ArrayRowList rs = new ArrayRowList(schema);
    for (int i = start0; i <= end0; i += step0) {
      for (int j = start1; j <= end1; j += step1) {
        BasicRow row = new BasicRow(schema.size());
        row.setValue(0, new Integer(i));
        row.setValue(1, new Integer(j));
        rs.add(row);
      }
    }
    Table t = new Table(rs);
    return t;
  }

  public static Table cube(int start0, int end0, int start1, int end1,
      int start2, int end2) {
    return cube(start0, end0, 1, start1, end1, 1, start2, end2, 1);
  }

  public static Table cube(int start0, int end0, int step0, int start1,
      int end1, int step1, int start2, int end2, int step2) {
    RowSchema schema = new RowSchema(3);
    schema.setColumnDef(0, COLNAME_1, Integer.class);
    schema.setColumnDef(1, COLNAME_2, Integer.class);
    schema.setColumnDef(2, COLNAME_3, Integer.class);
    ArrayRowList rs = new ArrayRowList(schema);
    for (int i = start0; i <= end0; i += step0) {
      for (int j = start1; j <= end1; j += step1) {
        for (int k = start2; k <= end2; k += step2) {
          BasicRow row = new BasicRow(schema.size());
          row.setValue(0, new Integer(i));
          row.setValue(1, new Integer(j));
          row.setValue(2, new Integer(k));
          rs.add(row);
        }
      }
    }
    Table t = new Table(rs);
    return t;
  }

  public static Table seqStream(int start, int end) {
    return seqStream(start, end, 1);
  }

  public static Table seqStream(int start, int end, int step) {
    SequenceStream seqStr = new SequenceStream(start, end, step);
    Table t = new Table(seqStr);
    return t;
  }

  public static Table cubeStream(
      int start0, int end0, 
      int start1, int end1,
      int start2, int end2) {
    return cubeStream(start0, end0, 1,
        start1, end1, 1,
        start2, end2, 1);
  }

  public static Table cubeStream(
      int start0, int end0, int step0,
      int start1, int end1, int step1,
      int start2, int end2, int step2) {
    RowList str = new TupleStream(
        new int[] { start0, start1, start2 },
        new int[] { end0, end1, end2 }, 
        new int[] { step0, step1, step2 });
    Table t = new Table(str);
    return t;
  }

  public static Table tupleSort(int start, int end, int size) {
    RowList str = new SumSortedTupleStream(start, end, size);
    Table t = new Table(str);
    return t;
  }

  private static class SequenceStream implements RowList {
    private RowSchema schema;

    private int start;

    private int end;

    private int step;

    public SequenceStream(int start, int end, int step) {
      this.start = start;
      this.end = end;
      this.step = step;

      schema = new RowSchema(1);
      schema.setColumnDef(0, COLNAME_1, Integer.class);
    }

    public RowSchema getSchema() {
      return schema;
    }

    public RowIterator iterator() {
      return new SequenceIterator();
    }

    private class SequenceIterator implements RowIterator {
      private int count = start;

      public RowSchema getSchema() {
        return schema;
      }

      public Row next() {
        if (count > end)
          return null;

        BasicRow row = new BasicRow(1);
        row.setValue(0, new Integer(count));
        count += step;
        return row;
      }
    }

  }

  private static class TupleStream implements RowList {
    private final int n;

    private RowSchema schema;

    private int[] start;

    private int[] end;

    private int[] step;

    public TupleStream(int[] start, int[] end, int[] step) {
      this.start = start;
      this.end = end;
      this.step = step;

      n = start.length;
      schema = new RowSchema(n);
      if (n >= 1)
        schema.setColumnDef(0, COLNAME_1, Integer.class);
      if (n >= 2)
        schema.setColumnDef(1, COLNAME_2, Integer.class);
      if (n >= 3)
        schema.setColumnDef(2, COLNAME_3, Integer.class);
    }

    public RowSchema getSchema() {
      return schema;
    }

    public RowIterator iterator() {
      return new TupleStreamIterator();
    }

    private class TupleStreamIterator implements RowIterator {
      private int[] count = null;

      public TupleStreamIterator() {
      }

      public RowSchema getSchema() {
        return schema;
      }

      public Row next() {
        if (count == null) {
          count = new int[n];
          for (int i = 0; i < n; i++)
            count[i] = start[i];
          return createRow();
        }

        // increment counters
        for (int i = n - 1; i >= 0; i--) {
          count[i] += step[i];
          // inc this column if possible
          if (count[i] <= end[i]) {
            return createRow();
          }

          // terminating condition
          if (i == 0)
            return null;

          /**
           * Otherwise, this counter has rolled over, so reset it Then carry on
           * incrementing higher counts
           */
          count[i] = start[i];
        }
        return createRow();
      }

      private Row createRow() {
        BasicRow row = new BasicRow(n);

        for (int i = 0; i < n; i++) {
          row.setValue(i, new Integer(count[i]));
        }
        // System.out.println(row);
        return row;
      }
    }
  }

  private static class SumSortedTupleStream implements RowList {
    private final int n;

    private RowSchema schema;

    private int start;
    private int end;

    public SumSortedTupleStream(int start, int end, int size) {
      this.start = start;
      this.end = end;
      n = size;

      schema = new RowSchema(n);
      if (n >= 1)
        schema.setColumnDef(0, COLNAME_1, Integer.class);
      if (n >= 2)
        schema.setColumnDef(1, COLNAME_2, Integer.class);
      if (n >= 3)
        schema.setColumnDef(2, COLNAME_3, Integer.class);
    }

    public RowSchema getSchema() {
      return schema;
    }

    public RowIterator iterator() {
      return new SumSortedTupleStreamIterator();
    }

    private class SumSortedTupleStreamIterator implements RowIterator {
      private int[] count = null;
      private int total = 0; 
      
      public SumSortedTupleStreamIterator() {
      }

      public RowSchema getSchema() {
        return schema;
      }

      public Row next() {
        if (count == null) {
          return initRow();
        }

        if (count[0] > end) return null;
        
        for (int i = n-1; i >= 0; i--) {
          
        }
        
        return createRow();
      }

      private Row initRow()
      {
        total = 3 * start;
        
        count = new int[n];
        for (int i = 0; i < n; i++) {
          count[i] = start;
        }
        return createRow();
      }
      private Row createRow() {
        BasicRow row = new BasicRow(n);

        for (int i = 0; i < n; i++) {
          row.setValue(i, new Integer(count[i]));
        }
        // System.out.println(row);
        return row;
      }
      
    }
  }

}

package jeql.io.shapefile;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jeql.api.row.ArrayRowList;
import jeql.api.row.Row;
import jeql.api.row.RowSchema;

import org.geotools.dbffile.DbfFieldDef;
import org.geotools.dbffile.DbfFile;
import org.geotools.dbffile.DbfFileException;
import org.geotools.dbffile.DbfFileWriter;

import org.locationtech.jts.geom.Geometry;

class TableDbfWriter {
  public TableDbfWriter() {
  }

  /**
   * Write a dbf file with the information from a RowList.
   * 
   * @param RowList
   *          column data from collection
   * @param fname
   *          name of the dbf file to write to
   */
  void write(ArrayRowList memRowList, String fname)
      throws IOException, DbfFileException {
    RowSchema schema = memRowList.getSchema();
    List rows = memRowList.getRows();
    int num = rows.size();

    DbfFieldDef[] fields = createFields(memRowList);

    // write header
    DbfFileWriter dbf = new DbfFileWriter(fname);
    dbf.writeHeader(fields, num);

    // write rows
    for (int t = 0; t < num; t++) {
      // System.out.println("dbf: record "+t);
      Row row = (Row) rows.get(t);
      Vector DBFrow = new Vector();

      // make data for each column in this row
      for (int u = 0; u < schema.size(); u++) {
        Class columnType = schema.getType(u);
        Object a = row.getValue(u);

        if (columnType == Integer.class) {

          if (a == null) {
            DBFrow.add(new Integer(0));
          } else {
            DBFrow.add((Integer) a);
          }
        } else if (columnType == Double.class) {

          if (a == null) {
            DBFrow.add(new Double(0.0));
          } else {
            DBFrow.add((Double) a);
          }
        }
        // not handled for now
        else if (columnType == Date.class) {
          if (a == null) {
            DBFrow.add("");
          } else {
            DBFrow.add(DbfFile.DATE_PARSER.format((Date) a));
          }
        }

        else if (columnType == String.class) {
          if (a == null) {
            DBFrow.add(new String(""));
          } else {
            // MD 16 jan 03 - added some defensive programming
            if (a instanceof String) {
              DBFrow.add(a);
            } else {
              DBFrow.add(a.toString());
            }
          }
        }
      }
      dbf.writeRecord(DBFrow);
    }
    dbf.close();
  }

  private DbfFieldDef[] createFields(ArrayRowList rowList)
      throws IOException {
    RowSchema schema = rowList.getSchema();

    // -1 because one of the columns is geometry
    DbfFieldDef[] fields = new DbfFieldDef[nonGeomColumnCount(schema)];

    // dbf column type and size
    int f = 0;

    for (int i = 0; i < schema.size(); i++) {
      Class columnType = schema.getType(i);
      String columnName = schema.getName(i);

      if (columnType == Integer.class) {
        fields[f] = new DbfFieldDef(columnName, 'N', 16, 0);
        f++;
      } else if (columnType == Double.class) {
        fields[f] = new DbfFieldDef(columnName, 'N', 33, 16);
        f++;
      } else if (columnType == String.class) {
        int maxlength = findMaxStringLength(rowList, i);
        if (maxlength > 255) {
          throw new IOException(
              "DBF files do not support strings longer than 255 characters");
        }

        fields[f] = new DbfFieldDef(columnName, 'C', maxlength, 0);
        f++;
      } else if (columnType == Date.class) {
        fields[f] = new DbfFieldDef(columnName, 'D', 8, 0);
        f++;
      }

      else if (columnType == Geometry.class) {
        // do nothing - the .shp file handles this
      } else {
        throw new IOException("Unsupported attribute type");
      }
    }
    return fields;
  }

  /**
   * look at all the data in the column of the featurecollection, and find the
   * largest string!
   * 
   * @param fc
   *          features to look at
   * @param attributeNumber
   *          which of the column to test.
   * @return the maximum length of the strings in the column
   * @return 0 if there are no rows
   */
  private int findMaxStringLength(ArrayRowList rowList, int index) {
    int maxLen = 0;
    for (Iterator i = rowList.getRows().iterator(); i.hasNext();) {
      Row row = (Row) i.next();
      String s = (String) row.getValue(index);
      if (s != null) {
        int len = s.length();
        if (len > maxLen) {
          maxLen = len;
        }
      }
    }
    return maxLen;
  }

  private static int nonGeomColumnCount(RowSchema schema) {
    int count = 0;
    for (int t = 0; t < schema.size(); t++) {
      Class colType = schema.getType(t);
      if (colType != Geometry.class)
        count++;
    }
    return count;
  }
}
package jeql.command.db.sde;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.engine.ConfigurationException;
import jeql.engine.ExecutionException;
import jeql.std.geom.GeomFunction;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.sde.SdeReader;

public class SdeRowMapper {
  private SdeReader sdeRdr = new SdeReader(GeomFunction.geomFactory);

  public SdeRowMapper() {

  }

  public RowSchema getSchema(SeQuery query) throws SeException {
    int ncols = query.getNumColumns();

    String[] names = new String[ncols];
    Class[] types = new Class[ncols];

    for (int i = 0; i < ncols; i++) {
      SeColumnDefinition colDef = query.describeColumn(i);
      names[i] = colDef.getName();
      types[i] = mapColumnType(colDef);
      if (types[i] == null) {
        throw new ExecutionException("SQL type is not currently handled");
      }
    }
    RowSchema schema = new RowSchema(names, types);
    return schema;
  }

  public Row createRow(RowSchema schema, SeRow seRow) throws Exception {
    BasicRow row = new BasicRow(schema.size());
    for (int i = 0; i < schema.size(); i++) {
      row.setValue(i, mapColumnValue(seRow, i, schema.getType(i)));
    }
    return row;
  }

  /**
   * 
   * @param rm
   * @param colIndex
   * @return null if the column type is not handled
   * @throws SQLException
   */
  protected Class mapColumnType(SeColumnDefinition colDef)
  // throws SeException
  {
    int type = colDef.getType();
    switch (type) {
    case SeColumnDefinition.TYPE_INT16:
    case SeColumnDefinition.TYPE_INT32:
      return Integer.class;
    case SeColumnDefinition.TYPE_INT64:
    case SeColumnDefinition.TYPE_FLOAT32:
    case SeColumnDefinition.TYPE_FLOAT64:
      return Double.class;
    case SeColumnDefinition.TYPE_SHAPE:
      return Geometry.class;
    case SeColumnDefinition.TYPE_DATE:
      return Date.class;
    default:
      return String.class;
    }
  }

  protected Object mapColumnValue(SeRow row, int columnIndex, Class destType)
      throws Exception {
    if (destType == String.class) {
      return row.getString(columnIndex);
    }
    if (destType == Integer.class) {
      // not very efficient? (cache types for columns?)
      if (row.getColumnDef(columnIndex).getType() == SeColumnDefinition.TYPE_INT16)
        return toInteger((Short) row.getShort(columnIndex));

      return row.getInteger(columnIndex);
    }
    if (destType == Double.class) {
      return row.getDouble(columnIndex);
    }
    if (destType == Date.class) {
      Calendar cal = row.getTime(columnIndex);
      if (cal == null) return cal;
      return cal.getTime();
    }
    if (destType == Geometry.class) {
      return toGeometry(row.getShape(columnIndex));
    }
    throw new ConfigurationException("Unhandled SDE value mapping for class "
        + destType.getName());
  }

  private static final Integer toInteger(Short val) {
    return new Integer(val.intValue());
  }

  private Geometry toGeometry(SeShape shape) throws SeException {
    try {
      return sdeRdr.read(shape);
    }
    // ignore geometry conversion errors
    catch (IllegalArgumentException e) {
      return null;
    }
    // return SeShapeConverter.convert(shape);
  }
}

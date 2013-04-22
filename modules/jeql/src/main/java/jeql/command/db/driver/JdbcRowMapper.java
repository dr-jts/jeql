package jeql.command.db.driver;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import jeql.api.error.ExecutionException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.engine.ConfigurationException;

public class JdbcRowMapper
implements RowMapper
{
	public RowSchema getSchema(ResultSet rs)
	throws SQLException
	{
		ResultSetMetaData rm = rs.getMetaData();
		int ncols = rm.getColumnCount();
		
    String[] names = new String[ncols];
    Class[] types = new Class[ncols];
    
		for (int i = 0; i < ncols; i++) {
      names[i] = rm.getColumnName(i+1);
      types[i] = mapColumnType(rm, i+1);
      if (types[i] == null) {
      	throw new ExecutionException("SQL type " + rm.getColumnTypeName(i)
      			+ " is not currently handled");
      }
    }
    RowSchema schema = new RowSchema(names, types);
    return schema;
	}
	
	public Row createRow(RowSchema schema, ResultSet rs)
	throws Exception
	{
		BasicRow row = new BasicRow(schema.size());
		for (int i = 0; i < schema.size(); i++) {
			row.setValue(i, mapColumnValue(rs, i+1, schema.getType(i)));
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
	protected Class mapColumnType(ResultSetMetaData rsm, int columnIndex)
		throws SQLException
	{
		return mapJDBCColumnType(rsm.getColumnType(columnIndex));
	}
	
	public static Class mapJDBCColumnType(int jdbcTypeCode)
	{
		switch (jdbcTypeCode) {
		case Types.TINYINT: 
		case Types.INTEGER: 
		case Types.SMALLINT: 
			return Integer.class;
		case Types.BIGINT: 
		case Types.NUMERIC: 
		case Types.DECIMAL: 
		case Types.FLOAT: 
		case Types.DOUBLE: 
		case Types.REAL: 
			return Double.class;
		case Types.BIT:
		case Types.BOOLEAN:
			return Boolean.class;
		case Types.CHAR: 
		case Types.VARCHAR: 
    case Types.CLOB: 
    //case Types.LONGNVARCHAR: 
    //case Types.NVARCHAR: 
		default:
			return String.class;
		}
//		return null;  // signifies this type is not handled
	}
	
	protected Object mapColumnValue(ResultSet rs, int columnIndex, Class destType)
	throws Exception
	{
		if (destType == String.class) {
			return rs.getString(columnIndex);
		}
		if (destType == Integer.class) {
			return new Integer(rs.getInt(columnIndex));
		}
		if (destType == Double.class) {
			return new Double(rs.getDouble(columnIndex));
		}
		if (destType == Boolean.class) {
			return new Boolean(rs.getBoolean(columnIndex));
		}
		throw new ConfigurationException("Unhandled SQL value mapping for class " + destType.getName());
	}
}

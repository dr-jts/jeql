package jeql.command.db.driver;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.WKTReader;

public class PostgisRowMapper
extends JdbcRowMapper
{
  private static final String DB_TYPE_BYTEA = "bytea";
  private static final String DB_TYPE_GEOMETRY = "geometry";

	private final WKBReader wkbReader = new WKBReader();
  private final WKTReader wktReader = new WKTReader();
  
	public PostgisRowMapper()
	{
		
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
    String dbTypeName = rsm.getColumnTypeName(columnIndex);
    if (dbTypeName.equalsIgnoreCase(DB_TYPE_GEOMETRY)
    		|| dbTypeName.equalsIgnoreCase(DB_TYPE_BYTEA))
        return Geometry.class;
    if (dbTypeName.equalsIgnoreCase("text"))
        return String.class;

		return super.mapColumnType(rsm, columnIndex);
	}
	
	protected Object mapColumnValue(ResultSet rs, int columnIndex, Class destType)
	throws Exception
	{
		if (destType == Geometry.class) {
			// MD- should do some caching here for efficiency
			ResultSetMetaData rsm = rs.getMetaData();
		    String dbTypeName = rsm.getColumnTypeName(columnIndex);
		    if (dbTypeName.equalsIgnoreCase(DB_TYPE_GEOMETRY)) {
		    	return convertWKBHex(rs.getObject(columnIndex));
		    }
		    /**
		     * This is much faster than reading WKBHex.
		     * But need to use ST_AsBinary in PG query to force binary in result column
		     */
		    if (dbTypeName.equalsIgnoreCase(DB_TYPE_BYTEA)) {
		    	return convertWKB(rs.getObject(columnIndex));
		    }
		}
		return super.mapColumnValue(rs, columnIndex, destType);
	}

  public Object convertWKT(Object obj) throws IOException, SQLException,
			ParseException {
		if (obj == null) return null;
		String s = obj.toString();
		Geometry geom = wktReader.read(s);
		return geom;
	}

	public Object convertWKB(Object obj) throws IOException, SQLException,
			ParseException {
		if (obj == null) return null;
		byte[] bytes = (byte[]) obj;
		Geometry geom = wkbReader.read(bytes);
		return geom;
	}

	public Object convertWKBHex(Object obj) throws IOException, SQLException,
			ParseException {
		if (obj == null) return null;
		String hex = obj.toString();
		Geometry geom = wkbReader.read(WKBReader.hexToBytes(hex));
		return geom;
	}
}

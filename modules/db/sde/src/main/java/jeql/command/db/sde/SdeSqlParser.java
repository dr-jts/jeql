package jeql.command.db.sde;

/**
 * Parses a synthetic SQL-like language for specifying SDE queries.
 * <p>
 * Query syntax is:
 * 
 * <pre>
 *   SELECT col-list  FROM dataset [ WHERE condition ]
 *   
 *   col-list := * | col, ...
 * </pre>
 * 
 * Keywords are case insensitive. 'condition' is passed as the where parameter
 * of an SDE SeSqlConstruct.
 * 
 * @author mbdavis
 *
 */
public class SdeSqlParser {
  private String[] columns = new String[0];
  private String dataset = null;
  private String condition = null;

  public SdeSqlParser() {

  }

  public void parse(String sql) throws IllegalArgumentException {
    String sqlLower = sql.toLowerCase();
    int selPos = sqlLower.indexOf("select ");
    if (selPos < 0) {
      throw new IllegalArgumentException("Missing SELECT keyword in SDE query");
    }
    int fromPos = sqlLower.indexOf(" from ");
    if (fromPos < 0) {
      throw new IllegalArgumentException("Missing FROM keyword in SDE query");
    }

    int wherePos = sqlLower.indexOf(" where ");

    String colList = sql.substring(selPos + 7, fromPos).trim();
    columns = parseColumns(colList);
    if (columns.length <= 0) {
      throw new IllegalArgumentException("Empty column list in SDE query");
    }

    int fromEndPos = wherePos > 0 ? wherePos : sql.length();
    dataset = sql.substring(fromPos + 6, fromEndPos).trim();

    if (wherePos > 0) {
      condition = sql.substring(wherePos + 7).trim();
    }

    // System.out.println(colList);
    // System.out.println(dataset);
    // System.out.println(condition);
  }

  private String[] parseColumns(String colList) {
    String[] cols = colList.split(",");
    for (int i = 0; i < cols.length; i++) {
      cols[i] = cols[i].trim();
      if (cols[i].length() <= 0) {
        throw new IllegalArgumentException(
            "Blank column name found in SDE query");
      }
    }
    return cols;
  }

  /**
   * Gets the list of columns provided in the query string. If the query column
   * list was "*", this is returned as the single column name.
   * 
   * @return the list of columns requested
   */
  public String[] getColumns() {
    return columns;
  }

  public String getDataset() {
    return dataset;
  }

  /**
   * The condition specified in the WHERE clause. The WHERE keyword is not
   * returned.
   * 
   * @return the query condition
   */
  public String getCondition() {
    return condition;
  }
}

package jeql.engine.query.join;


import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.engine.Scope;
import jeql.engine.index.EqualityRowIndex;
import jeql.engine.query.QueryScope;
import jeql.syntax.BinaryExpressionNode;
import jeql.syntax.FromItem;
import jeql.syntax.ParseTreeNode;
import jeql.syntax.TableColumnNode;
import jeql.syntax.operation.Operation;

public class IndexedJoinRowList 
  implements RowList
{
  public static int indexableOperandSide(ParseTreeNode condExpr, 
      FromItem from, RowSchema fromSchema)
  {
    // check for acceptable pattern
    if (! (condExpr instanceof BinaryExpressionNode)) return -1;
    BinaryExpressionNode binExpr = (BinaryExpressionNode) condExpr;
    if (binExpr.getOpCode() != Operation.EQ) return -1;
    
    int fromKeyIndex = -1;
    String fromTblName = from.getAliasOrTableName();
    if (isColumnInTable(binExpr.getSide(0), fromTblName, fromSchema)) {
      fromKeyIndex = 0;
    }
    else if (isColumnInTable(binExpr.getSide(1), fromTblName, fromSchema)) {
      fromKeyIndex = 1;      
    }
    return fromKeyIndex;
  }
  
  private static boolean isColumnInTable(ParseTreeNode expr, String tblName, RowSchema schema)
  {
    if (! (expr instanceof TableColumnNode)) return false;
    TableColumnNode tblCol = (TableColumnNode) expr;
    String tcTableName = tblCol.getTableName();
    boolean isMatchingTblName =  tcTableName == null 
                    || tblName.equals(tcTableName);
    
    String tcColName = tblCol.getColName();
    boolean isColumnPresent = schema.hasCol(tcColName);
    return isMatchingTblName && isColumnPresent;
  }
  
  public static String keyColName(ParseTreeNode condExpr, int indexSide)
  {
    BinaryExpressionNode binExpr = (BinaryExpressionNode) condExpr;
    TableColumnNode tblCol = (TableColumnNode) binExpr.getSide(indexSide);
    return tblCol.getColName();
  }

  public static ParseTreeNode driveTblExpr(ParseTreeNode condExpr, int indexSide)
  {
    BinaryExpressionNode binExpr = (BinaryExpressionNode) condExpr;
    return binExpr.getSide(1 - indexSide);
  }

  private RowSchema schema = null;
  private RowList driveRS;
  private int joinType = FromItem.JOIN_INNER;
  private QueryScope scope;
  private ParseTreeNode driveTblExpr;
  private String keyCol; 
  private EqualityRowIndex index;
  private JoinIteratorProvider joinProv;
  
  public IndexedJoinRowList(RowList driveRS, RowList joinRS, 
      int indexColSide,
      ParseTreeNode equalityExpr,
      int joinType,
      QueryScope scope) 
  {
    this.joinType = joinType;
    this.scope = scope;
    this.driveRS = driveRS;
    schema = new RowSchema(driveRS.getSchema(), joinRS.getSchema());
    keyCol = keyColName(equalityExpr, indexColSide);
    index = new EqualityRowIndex(joinRS, keyCol);
    driveTblExpr = driveTblExpr(equalityExpr, indexColSide);
    joinProv 
      = new IndexedJoinIteratorProvider(joinRS, equalityExpr, indexColSide);
  }

  public RowSchema getSchema() { return schema; }

  public RowIterator iterator()
  {
    IndexedJoinRowIterator joinIt = new IndexedJoinRowIterator(schema, driveRS, 
        //driveTblExpr, index, 
        joinProv,
        joinType, scope);
    return joinIt;
  }
  
  private static class IndexedJoinRowIterator implements RowIterator {
    private RowSchema schema;
    private int schema1Size = -1;
    private int schema2Size = -1;
    //private ParseTreeNode driveExpr;
    //private EqualityRowIndex index;
    private JoinIteratorProvider joinProv;
    private QueryScope scope;
    
    private RowIterator driveRowIt;
    private RowIterator joinRowIt;
    private boolean isLeftOuterJoin = false;
    //private boolean applyFilter = true;

    public IndexedJoinRowIterator(RowSchema schema, 
        RowList driveRS, 
        JoinIteratorProvider joinProv,
        //ParseTreeNode driveExpr, 
        //EqualityRowIndex index, 
        int joinType, 
        QueryScope scope) {
      this.schema = schema;
      //this.driveExpr = driveExpr;
      //this.index = index;
      this.joinProv = joinProv;
      this.scope = scope;
      
      schema1Size = driveRS.getSchema().size();
      schema2Size = schema.size() - schema1Size;
      
      if (joinType == FromItem.JOIN_LEFT_OUTER)
        isLeftOuterJoin = true;
      driveRowIt = driveRS.iterator();
    }

    public RowSchema getSchema() {
      return schema;
    }

    private Row driveRow = null;
    private boolean isNewLeftRow = false;
    
    public Row next() 
    {
      while (true) {
        if (driveRow == null && driveRowIt != null) { 
          // get next drive row
          driveRow = driveRowIt.next();
          isNewLeftRow = true;
          joinRowIt = null;
        }
        // at this point if driveRow is null drive iteration is finished
        if (driveRow == null) {
          driveRowIt = null;
          return null;
        }
        
        Row joinRow = nextJoinRow(driveRow);
        if (joinRow == null) {
          // compute LEFT OUTER JOIN with null RHS
          Row currDriveRow = driveRow;
          driveRow = null;
          if (isNewLeftRow && isLeftOuterJoin) {
            //applyFilter = false;
            joinRowIt = null;
            return BasicRow.createRow(currDriveRow,  schema1Size, null, schema2Size);
          }
          // else loop to get new driveRow
          continue;
        }
        else {
          // have non-null index row - can return a row
          isNewLeftRow = false;
          return BasicRow.createRow(driveRow, schema1Size, 
              joinRow, schema2Size);
        }
      }
    }
    
    private Row nextJoinRow(Row driveRow)
    {
      if (joinRowIt == null) {
        ((QueryScope) scope).setRow(driveRow);
        joinRowIt = joinProv.iterator(scope);
        /*
        Object val = driveExpr.eval(scope);
        joinRowIt = index.iterator(val);
        */
        // key not found in index
        if (joinRowIt == null)
          return null;
      }
      Row joinRow = joinRowIt.next();
      // at end of join table
      if (joinRow == null)
        return null;
      return joinRow;
    }
    
  }
  
  interface JoinIteratorProvider
  {
    RowSchema getSchema();
    RowIterator iterator(Scope scope);
  }
  
  static class IndexedJoinIteratorProvider
  implements JoinIteratorProvider
  {
    private RowSchema schema;
    private ParseTreeNode driveTblExpr;
    private EqualityRowIndex index;

    public IndexedJoinIteratorProvider(RowList joinRS, 
        ParseTreeNode equalityExpr,
        int indexColSide
    )
    {
      schema = joinRS.getSchema();
      String keyCol = keyColName(equalityExpr, indexColSide);
      index = new EqualityRowIndex(joinRS, keyCol);
      driveTblExpr = driveTblExpr(equalityExpr, indexColSide);
    }
    
    public RowSchema getSchema()
    {
      return schema;
    }
    
    public RowIterator iterator(Scope scope)
    {
      Object val = driveTblExpr.eval(scope);
      RowIterator joinRowIt = index.iterator(val);
      return joinRowIt;
    }
  }
}


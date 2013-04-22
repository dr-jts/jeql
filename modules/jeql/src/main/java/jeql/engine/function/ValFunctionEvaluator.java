package jeql.engine.function;

import java.util.List;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.syntax.ParseTreeNode;

/**
 * Implements the VAL pseudo-function semantics.
 * 
 * @author Martin Davis
 *
 */
public class ValFunctionEvaluator
  implements FunctionEvaluator
{
  private ParseTreeNode valueExpr;
  public static final String FN_VAL = "val";

  public ValFunctionEvaluator() 
  {
  }

  public void bind(Scope scope, List args)
  {   
    int argCount = args.size();
    if (argCount != 1)
      throw new CompilationException("VAL() function must have 1 argument");
    valueExpr = (ParseTreeNode) args.get(0);
  }
  
   public Object eval(Scope scope)
   {
     Object value = valueExpr.eval(scope);
     if (value instanceof Table) {
       Table tbl = (Table) value;
       // extract first value from table
       RowList rowList = tbl.getRows();
       RowIterator rowit = rowList.iterator();
       Row row = rowit.next();
       if (row == null) return null;
       return row.getValue(0);
     }
     // otherwise simply return the value
     return value;
   }
   
   public Class getType(Scope scope)
   {
     Class valueType = valueExpr.getType(scope);
     if (valueType == Table.class) {
       // IS THIS OK????
       Object value = valueExpr.eval(scope);
       
       Table tbl = (Table) value;
       // extract first value from table
       RowList rowList = tbl.getRows();
       RowSchema schema = rowList.getSchema();
       if (schema.size() > 0) {
         return schema.getType(0);
       }
       return Object.class;
     }
     return valueType;
   }
}
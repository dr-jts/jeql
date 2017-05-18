package jeql.engine.query.group;

import java.util.List;

import jeql.api.function.Aggregator;
import jeql.api.row.Row;
import jeql.api.row.RowList;
import jeql.engine.query.QueryScope;
import jeql.engine.query.SelectedItemsRowList;
import jeql.engine.query.Tuple;
import jeql.syntax.ParseTreeNode;
import jeql.syntax.SelectItemList;
import jeql.syntax.StatementListNode;
import jeql.syntax.TableRefNode;

/**
 * Evaluates a SELECT with a GROUP BY and aggregate functions.
 * 
 * A grouped select is evaluated using the following strategy:
 * <ul>
 * <li>Fully evaluate the innerSelList,
 * using the rows supplied by the FROM/WHERE source
 * <li>Compute a new rowList which consists
 * of a column for each of the GROUP BY columns,
 * and a column for each aggregate function.
 * There will be one row for each unique key tuple
 * <li>Evaluate a rowlist of the outerSelList 
 * applied to the grouped rowList.  This is the result rowlist.
 * </ul> 
 *  
 * @author Martin Davis
 *
 */
public class GroupByEvaluator 
{
  QueryScope baseScope;
  List aggFunArgList;
  StatementListNode aliasList;
  GroupScope groupScope;
  
  private SelectItemList outerSelList;
  private int[] groupKeyAttrIndex;
  private int groupKeyLen = 0;
  
  
  public GroupByEvaluator(
      QueryScope baseScope, 
      List aggFunArgList,
      StatementListNode aliasList,
      GroupScope groupScope, 
      SelectItemList outerSelList) 
  {
    this.baseScope = baseScope;
    this.aggFunArgList = aggFunArgList;
    this.aliasList = aliasList;
    this.groupScope = groupScope;
    this.outerSelList = outerSelList;
    
    groupKeyAttrIndex = groupScope.getGroupKeyIndices();
    groupKeyLen = groupKeyAttrIndex.length; 
  }

  public RowList eval(RowList baseRS)
  {
    GroupRowsBuilder groupRowListBuilder;
    
    if (groupKeyLen > 0) {
      groupRowListBuilder = new MappedGroupRowsBuilder();
    }
    else {
      groupRowListBuilder = new SingleGroupRowsBuilder();
    }
    groupRowListBuilder.init(this);

    // Compute rowlist for the aggregated groups
    RowList groupedRowList = groupRowListBuilder.eval(baseRS);
    // Construct rowlist for the outer select expressions
    RowList rowStrSel = new SelectedItemsRowList(groupedRowList, outerSelList, null, groupScope);
    return rowStrSel;
  }
 
  void evalAggregators(Aggregator[] agg, QueryScope scope)
  {
    for (int i = 0; i < aggFunArgList.size(); i++) {
      ParseTreeNode[] args = (ParseTreeNode[]) aggFunArgList.get(i);
      evalAgg(i, agg, args, scope);
    }
  }

	private void evalAgg(int i, Aggregator[] agg, ParseTreeNode[] args, QueryScope scope) {
		ParseTreeNode expr = args[0];
		Object[] argVal = null;
		// handle aggregator expressions of form f(*) (e.g. count - but this is
		// more general)
		if (args.length == 1 && TableRefNode.isStar(expr)) {
			argVal = new Object[] { scope.getRow() };
		} else {
			argVal = eval(args, scope);
		}
		agg[i].addValue(argVal);
	}

  private Object[] eval(ParseTreeNode[] args, QueryScope scope) {
	  Object[] vals = new Object[args.length];
	  for (int i = 0; i < args.length; i++) {
		  vals[i] = args[i].eval(scope);
	  }
	  return vals;
	}

Tuple extractGroupKey(Row row)
  {
    if (groupKeyLen == 0)
      return null;
    
    Tuple tuple = new Tuple(groupKeyAttrIndex.length);
    for (int i = 0; i < groupKeyAttrIndex.length; i++) {
      tuple.setValue(i, row.getValue(groupKeyAttrIndex[i]));
    }
    return tuple;
  }

}




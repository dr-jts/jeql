package jeql.engine.query.group;

import java.util.ArrayList;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.engine.function.AggregateFunctionEvaluator;
import jeql.engine.function.FunctionEvaluator;
import jeql.engine.query.BaseQueryScope;
import jeql.engine.query.QueryScope;
import jeql.syntax.FunctionNode;
import jeql.syntax.ParseTreeNode;
import jeql.syntax.SelectNode;

public class GroupBinder 
{
  private BaseQueryScope baseScope;
  private List groupByList;
  private int groupByItemOffset = 0;
  private List aggFunArgsList = new ArrayList();
  private List aggFunctions = new ArrayList();
  
  public GroupBinder(SelectNode select, BaseQueryScope baseScope) 
  {
    this.baseScope = baseScope;
    
    groupByList = select.getGroupByList();
    if (groupByList != null)
      groupByItemOffset = groupByList.size();
    init();
  }
  
  public List getAggFunArgs()
  {
    return aggFunArgsList;
  }
  
  public GroupScope getScope(QueryScope scope)
  {
    // TODO: make functions determined from expression (e.g. polymorphic on arg type)
    return new GroupScope(scope, groupByList, aggFunctions);
  }

  /**
   * Extracts agg functions and function args from 
   * agg function node list.
   *
   */
  private void init()
  {
    List<FunctionNode> aggFunNodes = baseScope.getAggregateFunctionNodes();
    if (aggFunNodes == null)
      return;
    
    int funcCount = 0;

    //for (Iterator i = aggFunNodes.iterator(); i.hasNext();) {
    //  FunctionNode funcNode = (FunctionNode) i.next();
    for (FunctionNode funcNode : aggFunNodes) {
      FunctionEvaluator funcEval = funcNode.getFunctionEvaluator();
      if (! (funcEval instanceof AggregateFunctionEvaluator)) {
        continue;
      }
      AggregateFunctionEvaluator aggFunEval = (AggregateFunctionEvaluator) funcEval;
      aggFunEval.setColumnIndex(groupByItemOffset + funcCount);
      AggregateFunction aggFun = aggFunEval.getFunction();
      
      aggFunctions.add(aggFun);
      
      // add argument expression to args list
      aggFunArgsList.add(funcNode.getArgs().toArray(new ParseTreeNode[0]));
      
      funcCount++;  
    }

  }
  

  
}

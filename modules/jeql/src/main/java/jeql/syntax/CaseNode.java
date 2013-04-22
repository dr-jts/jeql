package jeql.syntax;

import java.util.List;

import jeql.engine.Scope;
import jeql.util.TypeUtil;

import com.vividsolutions.jts.util.Assert;

public class CaseNode 
  extends ParseTreeNode
{
  // A non-null valueExpr indicates a simple case expression
  private ParseTreeNode valueExpr;
  
  private ParseTreeNode[] whenExpr;
  private ParseTreeNode[] thenExpr;
  private ParseTreeNode elseExpr;
  private int nCases = 0;
  
  public CaseNode(ParseTreeNode valueExpr, List caseList, ParseTreeNode elseExpr) 
  {
    this.valueExpr = valueExpr;
    this.elseExpr = elseExpr;
    Assert.isTrue(caseList.size() > 0);
    Assert.isTrue(caseList.size() % 2 == 0);
    nCases = caseList.size() / 2;
    
    whenExpr = new ParseTreeNode[nCases];
    thenExpr = new ParseTreeNode[nCases];
    for (int i = 0; i < nCases; i++) {
      int caseIndex = 2 * i;
      whenExpr[i] = (ParseTreeNode) caseList.get(caseIndex);
      thenExpr[i] = (ParseTreeNode) caseList.get(caseIndex + 1);
    }
  }

  public Class getType(Scope scope)
  {
    return thenExpr[0].getType(scope);
  }
  
  public void bind(Scope scope)
  {
    if (valueExpr != null)
      valueExpr.bind(scope);
    for (int i = 0; i < whenExpr.length; i++) {
      whenExpr[i].bind(scope);
      thenExpr[i].bind(scope);
    }
    if (elseExpr != null)
      elseExpr.bind(scope);
  }

  public Object eval(Scope scope)
  {
    if (valueExpr != null)
      return evalSimple(scope);
    return evalSearched(scope);
  }
  
  public Object evalSimple(Scope scope)
  {
    Object value = valueExpr.eval(scope);
    if (value == null) 
      return null;
    
    for (int i = 0; i < whenExpr.length; i++) {
      Object whenVal =  whenExpr[i].eval(scope);
      if (whenVal == null) continue;
      if (TypeUtil.compareValue(value, whenVal) == 0) {
        return thenExpr[i].eval(scope);
      }
    }
    if (elseExpr != null) {
      return elseExpr.eval(scope);
    }
    return null;
  }
 
  public Object evalSearched(Scope scope)
  {
    for (int i = 0; i < whenExpr.length; i++) {
      Boolean whenVal = (Boolean) whenExpr[i].eval(scope);
      if (whenVal == null) continue;
      if (whenVal.booleanValue())
        return thenExpr[i].eval(scope);
    }
    if (elseExpr != null) {
      return elseExpr.eval(scope);
    }
    
    return null;
  }
}

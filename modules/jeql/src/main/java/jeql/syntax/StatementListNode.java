package jeql.syntax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeql.api.error.ImplementationException;
import jeql.api.error.JeqlException;
import jeql.engine.Scope;
import jeql.util.ExceptionUtil;

public class StatementListNode 
  extends ParseTreeNode
{
  private List stmtList = new ArrayList();
  
  public StatementListNode() 
  {
  }

  public void add(ParseTreeNode stmt)
  {
    stmtList.add(stmt);
  }
  
  public List getStatements() { return stmtList; }
  
  public Class getType(Scope scope)
  {
    throw new UnsupportedOperationException();
  }
  
  public void bind(Scope scope)
  {
    throw new UnsupportedOperationException();
  }

  public Object eval(Scope scope)
  {
    Object result = null;
    
    for (Iterator i = stmtList.iterator(); i.hasNext(); ) {
      ParseTreeNode node = (ParseTreeNode) i.next();
      
      // bind statements as they are executed
      node.bind(scope);
      
      Object stmtResult = evalStmt(scope, node);
      
      // only some kinds of statements return a reportable result
      Object lastTbl = null;
      if (node instanceof SelectNode || node instanceof AssignmentNode) {
        result = stmtResult;
        lastTbl = result;
      }
      scope.setVariable(Scope.LAST_TABLE, lastTbl);
    }
    return result;
  }

  private Object evalStmt(Scope scope, ParseTreeNode node)
  {
    try {
      Object stmtResult = node.eval(scope);
      return stmtResult;
    }
    catch (JeqlException e) {
      throw e;
    }
    catch (Throwable e) {
      throw new ImplementationException(node, e);
    }
  }
  
  

}

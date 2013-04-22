package jeql.syntax;

import java.util.List;

import jeql.engine.Scope;

public class ProgramNode
  extends ParseTreeNode
{
  private List imports;
  private StatementListNode stmts;
  
  public ProgramNode(List imports, StatementListNode stmts) {
    this.imports = imports;
    this.stmts = stmts;
  }

  public List getImports() { return imports; }
  
  public StatementListNode getStatements() { return stmts; }
  
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
    throw new UnsupportedOperationException();
  }

}

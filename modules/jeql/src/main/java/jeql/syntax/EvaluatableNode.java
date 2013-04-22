package jeql.syntax;

import jeql.engine.Scope;

// MD - not currently used
public abstract class EvaluatableNode 
extends ParseTreeNode
{
  public abstract void bind(Scope scope);
  public abstract Object eval(Scope scope);
  public abstract Class getType(Scope scope);

}

package jeql.engine.function;

import java.util.List;

import jeql.engine.Scope;

public interface FunctionEvaluator 
{
  void bind(Scope scope, List args);
  Class getType(Scope scope);
  Object eval(Scope scope);
}

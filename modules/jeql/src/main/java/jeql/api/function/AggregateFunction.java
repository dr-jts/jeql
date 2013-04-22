package jeql.api.function;

import java.util.List;

import jeql.engine.Scope;

/**
 * Interface for functions which aggregate values.
 * 
 * @author Martin Davis
 *
 */
public interface AggregateFunction 
{
  void bind(Scope scope, List args);
  
  String getName();
  
  /**
   * Gets the return type from this function
   * 
   * @return the Class of the return type
   */
  Class getType();
  
  /**
   * Creates a new instance of the Aggregator for this function
   * @return
   */
  Aggregator createAggregator();
}

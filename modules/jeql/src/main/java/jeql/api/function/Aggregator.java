package jeql.api.function;

public interface Aggregator 
{
  void addValue(Object obj);
  Object getResult();
}

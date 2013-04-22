package jeql.api.row;


/**
 * A Row deliberately does NOT provide a {@link RowSchema}, 
 * since this allows Rows to be reused in 
 * {@link RowList}s having schemas with different column names.
 * An example of where this capability is used 
 * is table aliasing with column names.
 * 
 * @author Martin Davis
 *
 */
public interface Row 
{
  Object getValue(int i);
  int size();
}

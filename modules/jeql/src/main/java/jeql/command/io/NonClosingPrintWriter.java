package jeql.command.io;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * A PrintWriter which no-ops the close() operation.
 * 
 * Used to wrap PrintWriters which should not be closed - 
 * in particular the standard System output streams.
 * (If they are closed they stop working for 
 * that instance of the JVM!)
 * 
 * 
 * @author Martin Davis
 *
 */
public class NonClosingPrintWriter
extends PrintWriter
{

  public NonClosingPrintWriter(OutputStream out) {
    super(out);
  }

  public void close()
  {
    // do nothing!
  }
}
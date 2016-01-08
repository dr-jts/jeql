package jeql.std.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import jeql.api.function.FunctionClass;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.EngineContext;
import jeql.engine.FunctionRegistry;
import jeql.man.ManGenerator;
import jeql.man.ManUtil;
import jeql.workbench.ui.assist.CodeSnippet;

public class SystemFunction 
implements FunctionClass
{
  /**
   * Execs a system cmd and returns the first line of output as a string.
   * 
   * 
   * Note: to run Windows shell scripts it is necessary 
   * to invoke the cmd.exe processor explicitly.  This is done
   * by prefixing the command string with "cmd /c ".
   * 
   * @param cmd
   * @return the output line from the command
   * @return an empty string if there was no output
   * @throws IOException - if an I/O error occurs
   * @throws InterruptedException
   */
  public static String exec(String cmd)
    throws IOException, InterruptedException
  {
    Runtime rt = Runtime.getRuntime();
    Process proc = rt.exec(cmd);
    proc.waitFor();
    
    // get the first line of output
    InputStream inStream = proc.getInputStream();
    LineNumberReader lineRdr = new LineNumberReader(new InputStreamReader(inStream));
    String line = lineRdr.readLine();
    
    // check for no cmd output - if none return empty string
    if (line == null)
      return "";
    
    return line;
  }

  public static String property(String key)
  {
    return System.getProperty(key);
  }

  public static Table properties() 
  {
    RowSchema schema = new RowSchema(2);
    schema.setColumnDef(0, "key", String.class);
    schema.setColumnDef(1, "value", String.class);

    Map map = System.getProperties();
    ArrayRowList rs = new ArrayRowList(schema);
    for (Iterator i = map.keySet().iterator(); i.hasNext();) {
      String key = (String) i.next();
      BasicRow row = new BasicRow(schema.size());
      row.setValue(0, key);
      row.setValue(1, (String) map.get(key));
      rs.add(row);
    }
    Table t = new Table(rs);
    return t;
  }
  
  public static Table functions() 
  {
    RowSchema schema = new RowSchema(5);
    schema.setColumnDef(0, "class", String.class);
    schema.setColumnDef(1, "name", String.class);
    schema.setColumnDef(2, "resultType", String.class);
    schema.setColumnDef(3, "args", String.class);
    schema.setColumnDef(4, "description", String.class);
    ArrayRowList rs = new ArrayRowList(schema);

    FunctionRegistry reg = EngineContext.getInstance().getFunctionRegistry();
    for (String fname : reg.getFunctionNames() ) {
      String module = FunctionRegistry.moduleName(fname);
        
      for (Method meth :  reg.getFunctionMethods(fname) ) {
        BasicRow row = new BasicRow(schema.size());
        
        row.setValue(0, module);
        row.setValue(1, FunctionRegistry.functionName(fname) );
        row.setValue(2, FunctionRegistry.resultType(meth) );
        row.setValue(3, ManGenerator.functionParamList(meth) );
        row.setValue(4, ManUtil.description(meth) );
        
        rs.add(row);
      }
    }
    return  new Table(rs);
  }

}

package jeql.engine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import jeql.JeqlStrings;
import jeql.monitor.Monitor;
import jeql.syntax.ImportNode;
import jeql.syntax.ParseTreeNode;
import jeql.syntax.ProgramNode;
import jeql.syntax.StatementListNode;
import jeql.syntax.parser.ParseException;
import jeql.syntax.parser.Parser;


/**
 * 
 * @author Martin Davis
 * @version 1.0
 */
public class JeqlEngine
{
  private Scope scope;
  private ParseTreeNode scriptNode;
  private Object scriptResult = null;

  public JeqlEngine(EngineContext context) {
    scope = new BasicScope(context);
  }

  public Object getResult()
  {
    return scriptResult;
  }
  
  public void prepareSelectFromString(String sql) throws ParseException {
    Parser parser = new Parser(new StringReader(sql));
    scriptNode = parser.SelectStatement();
  }

  public void prepareScriptFile(String filename) throws ParseException,
      FileNotFoundException, IOException {
    Reader rdr;
    if (filename.equalsIgnoreCase(JeqlStrings.STDIN)) {
      rdr = new InputStreamReader(System.in);
    } else {
      rdr = new FileReader(filename);
    }
    prepareProgram(rdr);
  }

  public void prepareScript(String scriptValue)
      throws ParseException, FileNotFoundException, IOException {
    Reader rdr;
    rdr = new StringReader(scriptValue);
    prepareProgram(rdr);
  }

  private void prepareProgram(Reader rdr) throws ParseException,
      FileNotFoundException, IOException {
    Parser parser = new Parser(rdr);
    scriptNode = parser.Program();
    rdr.close();
  }

  public Object evalScript() {
    ProgramNode progNode = (ProgramNode) scriptNode;
    registerImports(progNode.getImports());
    
    StatementListNode stmts = progNode.getStatements();
    scriptResult = stmts.eval(scope);
    EngineContext.flush();
    return scriptResult;
  }

  private void registerImports(List imports)
  {
    for (Iterator i = imports.iterator(); i.hasNext(); ) {
      ImportNode importNode = (ImportNode) i.next();
      registerImport(importNode);
    }
  }
  
  private void registerImport(ImportNode importNode) 
  {
    importNode.checkSyntax();
    String classname = importNode.getClassname();
    Class clazz = null;
    try {
      clazz = this.getClass().getClassLoader().loadClass(classname);
    } catch (ClassNotFoundException ex) {
      throw new CompilationException(importNode, "Import " + classname + " not found");
    }
    // testing
    //Package foo = Package.getPackage("test.jeql.function");
    scope.getContext().register(clazz);
  }
}
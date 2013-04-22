package test.jeql.parser;

import java.io.StringReader;

import jeql.syntax.SelectNode;
import jeql.syntax.parser.*;



import junit.framework.TestCase;
import junit.textui.TestRunner;


public class TestSelectParser
  extends TestCase
{

  public static void main(String args[]) {
      TestRunner.run(TestSelectParser.class);
    }
    
  public TestSelectParser(String name) { super(name); }
    
  public void testSelectValid()
  {
    parse("select * from e.r where foo");
    parse("select a as b from e");
    parse("select a.[x y] from e");
    parse("select a, b from e");
    parse("select a.b, c.d from e a");
    parse("select a.*, b.* from e.r.y");
    parse("select a.*, b.* from e.r join e a on foo == blarg");
    parse("select a.*, b.* from (select * from a) join e a on foo == blarg");
  }
  
  void parse(String str)
  {
//    System.out.println("=========================================");
//    System.out.println(str);
    boolean ok = false;
    try {
      Parser parser = new Parser(new StringReader(str));
      SelectNode l = parser.SelectStatement();
      int size = 4;
      ok = true;
    }
    catch(ParseException ex) {
      ex.printStackTrace();
    }
    assertTrue(ok);
  }

}

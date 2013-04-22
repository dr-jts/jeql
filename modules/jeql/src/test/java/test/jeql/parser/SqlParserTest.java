package test.jeql.parser;

import java.io.*;
import java.util.*;

import jeql.*;
import jeql.syntax.*;
import jeql.syntax.parser.*;



/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Martin Davis
 * @version 1.0
 */

public class SqlParserTest {

  public static void main(String args[])
  {
    SqlParserTest test = new SqlParserTest();
    test.run();
    test.runErrors();
  }

  public SqlParserTest() {
  }

  void run()
  {
    parse("select * from e");
    parse("select a as b from e");
    parse("select a.[x y] from e");
    parse("select a, b from e");
    parse("select a.b, c.d from e.r");
    parse("select a.*, b.* from e.r");
    parse("select a.*, b.* from e.r limit 5");
    parse("select distinct a.*, b.* from e.r");
    parse("select * from (values ( Geom.fromWKT(\"POINT(1 1)\"),1,2,3 ), (Geom.fromWKT(\"POINT(2 2)\"), 4, 5, 6) ) ");
    parse("select * from (values ( 1,2,3 ), (4,5,6) ) t(a,b,c)");
    parse("select * from (values ( 1,2,3 ), (4,5,6) ) t(a,b,c) join t2");
    parse("select b ? 1 : 2 from t");
    parse("select b ? 1 : c ? 2 : 3 from t");
    parse("select x, y from Func.name(x, y)");
  }

  void runErrors()
  {
    parseError("SELECTx a, b");
  }

  void parse(String str)
  {
    System.out.println("=========================================");
    System.out.println(str);
    try {
      Parser parser = new Parser(new StringReader(str));
    SelectNode l = parser.SelectStatement();
    int size = 4;
    }
    catch(ParseException ex) {
      ex.printStackTrace();
    }
  }
  void parseError(String str)
  {
    System.out.println("!!!!=======================================");
    System.out.println(str);
    try {
      Parser parser = new Parser(new StringReader(str));
    parser.SelectStatement();
    }
    catch(ParseException ex) {
      System.out.println(ex.getMessage());
      return;
    }
    System.err.println("No error reported!");
  }
}
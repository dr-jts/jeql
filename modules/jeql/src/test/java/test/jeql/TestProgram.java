package test.jeql;

import jeql.api.JeqlOptions;
import jeql.api.JeqlRunner;
import jeql.engine.*;
import jeql.util.*;
import com.vividsolutions.jts.util.*;

/**
 * Reads a JQL program from a file and runs it
 * 
 * @author Martin Davis
 * @version 1.0
 */
public class TestProgram {

  public static void main(String args[])
  {
    TestProgram test = new TestProgram();
    try {
      test.run();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  JeqlEngine engine;
  
  public TestProgram() {
  }

  void run()
      throws Exception
  {
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testAggFunction.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testConsole.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testDistinct.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testSplitBy.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testJoin.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testLeftOuterJoin.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testJoinMultiple.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testLeftOuterJoin.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testValue.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\geom\\testGeomAggFunction.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testCase.jql");
//    execUnit("testDate.jql");
    //execUnit("geom/testGrid.jql");
//    execUnit("plot/testPlot.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testExcept.jql");
//  execUnit("testExpression.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testGroupBy.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testGroupBy2.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testOrderBy.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testMath.prq");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testPrev.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testPrevFunction.jql");
//  execUnit("testRegEx.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testRowNum.jql");
//  execUnit("testSplitBy.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testSubquery.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testSetRelOp.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testSimpleJoin.prq");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testScript.jql");
//  execUnit("testString.jql");
//  execUnit("testStringFunction.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testScope.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testSelectStatement.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testSTFFile.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testTypes.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testCSVFile.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\testSelectWith.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\unitTest\\io\\testKMLWriter.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\unitTest\\io\\testXMLWriter.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\io\\testKMLWriterStyles.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\io\\testShapefile.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\io\\testCSVFile.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\unitTest\\io\\testShapefile2.jql");

//    execUnit("testJoinIndexed.jql");

//    execUnit("testCorrelatedSubquery.jql");
//    execUnit("testStrMatch.jql");
//  execUnit("testExpression.jql");
//  execUnit("testUnion.jql");
//  execUnit("testRegEx.jql");
    execUnit("io\\testXMLReader.jql");
//  execUnit("geom\\testGeomValues.jql");
//  execUnit("geom\\testGeomFunc.jql");
//  execUnit("geom\\testGeomAggFunction2.jql");
//  execUnit("geom\\testPolygonizer.jql");
    
//  execUnit("io\\testCSVFile.jql");
//  execUnit("io\\testShapefile.jql");
//  execUnit("io\\testHtmlWriter.jql");
//  execUnit("io\\testTextFile.jql");

  
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\testNewSplitFun.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\perfTest\\testIndexedJoin.jql");

    //-----------------------------------------------------------
 
//  exec("C:/data/martin/proj/jeql/testProg/files/fileMap.jql");
  //exec("C:\\data\\martin\\proj\\jeql\\testProg\\testJoinColName.jql");
//    exec("createExchangePoly.jql");
//    exec("genPythag.jql");
//    exec("genCube.jql");
//    exec("genSumSortedCube.jql");
  
    //exec("C:\\data\\martin\\proj\\geodata\\airtrack\\testGeodeticSplit180.jql");
//    exec("C:\\data\\martin\\proj\\geodata\\airtrack\\plotRoute.jql");
//    exec("C:\\data\\martin\\proj\\geodata\\airroute\\airStats.jql");

//    exec("C:\\data\\martin\\proj\\geodata\\proj4\\extractTest.jql");
    //exec("C:\\data\\martin\\proj\\jeql\\testProg\\testFilter.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\testREPerf.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\files\\moveMovies.jql");

//exec("C:\\data\\martin\\proj\\jeql\\testProg\\plot\\testPlot.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\plot\\testVoronoi.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\plot\\testPlot2.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\plot\\testWorldPlot.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\plot\\testFont.jql");

//  exec("C:\\data\\martin\\proj\\jeql\\dist\\sample\\example\\mandelbrot.jql");

    
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\io\\testWriteFile.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\io\\writeTFW.jql");
    
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\db\\testH2.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\db\\testH2insert.jql");
//exec("C:\\data\\martin\\proj\\jeql\\testProg\\db\\testH2Meta.jql");
  
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\kml\\testKmlReader.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\kml\\kmlToShape_BCAlbers.jql");
    
//    exec("C:\\data\\martin\\proj\\geodata\\world\\namePieKML.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\testColumnVar.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\testExec.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\testgroupBy.jql");
//  exec("C:\\data\\martin\\proj\\jumpsql\\testProg\\compareSoilPoly2.jql");
//exec("C:\\data\\martin\\proj\\jumpsql\\testProg\\testTextFile.jql");
//exec("C:\\data\\martin\\proj\\jumpsql\\testProg\\testLineArrows.jql");
//exec("C:\\data\\martin\\proj\\jumpsql\\testProg\\unit\\testCase.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\testAgg.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\testMusicIndex.jql");
//exec("C:\\data\\martin\\proj\\jumpsql\\testProg\\testKML.jql");
//    exec("C:\\data\\martin\\proj\\jumpsql\\testProg\\testCWB_KML.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\data\\yao\\roads_KML.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\data\\css\\cssToJava.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\testShapefile.jql");
    //exec("C:\\data\\martin\\proj\\jeql\\testProg\\shapefile\\testShapefile.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\shapefile\\shapefileUnion.jql");
    
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\BC_DAR_toKML.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\data\\gregw\\pgdataToShp.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\data\\gregw\\runCovers.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\data\\stf\\testSTF.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\shpToKML.jql", "C:\\data\\martin\\data\\BC_Basemap.shp");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\tacoma\\rms_to_kml.jql");
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\proj\\testAlbers.jql");
  
 
 
  
//    exec("C:\\data\\martin\\proj\\vimts\\script\\convertRawToFull.jql");
//exec("C:\\data\\martin\\proj\\vimts\\scripts\\convertSTFToKML.jql");

//    exec("C:\\data\\martin\\proj\\bcGaz\\scripts\\extractMtns.jql");
//  exec("C:\\data\\martin\\proj\\bcGaz\\scripts\\convertSTFToKML.jql");
//  exec("C:\\data\\martin\\proj\\bcGaz\\scripts\\convertMtnSTFToKML.jql");
    
//  exec("C:\\data\\martin\\proj\\geodata\\timmys\\timmysToKML.jql");

    
//    exec("C:\\data\\martin\\proj\\jeql\\testProg\\emailList\\extractEmail.jql");
    
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\nat\\removeDupPts.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\jtsNegBufferIssue\\extractData.jql");

//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\testCode.jql");
//  exec("C:\\data\\martin\\proj\\jeql\\testProg\\testKeith1.jql");
//exec("C:\\data\\martin\\proj\\geodata\\greg\\extractFarms.jql");
//  exec("C:\\data\\martin\\proj\\geodata\\empr\\assay\\extractData.jql");
//  exec("C:\\data\\martin\\proj\\geodata\\empr\\geology\\geolPolyToKML.jql");
//    exec("C:\\data\\martin\\proj\\geodata\\empr\\geology\\geolPolyToKML.jql");
//exec("C:\\data\\martin\\proj\\geodata\\soundmap\\joinGpsSonar.jql");
      
//exec("C:\\data\\martin\\proj\\geodata\\ebc\\readRoads.jql");
    
    //----------   C:\data\martin\proj\geodata\darrin
//    exec("cityToCountry.jql");  
//    exec("cityInNoCountry.jql");
    
    //----------   C:\data\martin\proj\geodata\basinrange\basinrain
//    exec("splitRain.jql");  
//    exec("basinRain.jql");  

    
//  exec("accHutToKML.jql");
    
//exec("ukunion.jql");

  }

  /**
   * Assumes run from repo "jeql/script/output" directory
   * 
   * @param scriptname
   */
  void execUnit(String scriptname)
  {
    exec("..\\unitTest\\" + scriptname);
  }
  void exec(String filename)
  {
    exec(filename, new String[0]);
  }

  /*
  void exec(String filename, String arg1)
  {
    exec(new String[] { filename, arg1 });
  }

  void exec(String filename, String arg1, String arg2)
  {
    exec(new String[] { filename, arg1, arg2 });
  }
*/
  
  void exec(String scriptFile, String[] args)
  {
    JeqlRunner runner = new JeqlRunner();
    
    JeqlOptions options = new JeqlOptions();
    options.setVerbose(true);
    options.setDebug(true);
    //options.setMonitor(true);
    
    boolean ok = false;
    try {
      runner.init(options);
      ok = runner.execScriptFile(scriptFile, args);
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }


}
package jeql.engine;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import jeql.command.chart.ChartCommand;
import jeql.command.db.DbExec;
import jeql.command.db.DbMetadata;
import jeql.command.db.DbReader;
import jeql.command.db.DbWriter;
import jeql.command.db.PostgisReader;
import jeql.command.io.CSVReaderCommand;
import jeql.command.io.CSVWriterCommand;
import jeql.command.io.DbfReaderCommand;
import jeql.command.io.GMLWriterCommand;
import jeql.command.io.HtmlWriterCommand;
import jeql.command.io.PrintCommand;
import jeql.command.io.STFReaderCommand;
import jeql.command.io.STFWriterCommand;
import jeql.command.io.ShapefileReaderCommand;
import jeql.command.io.ShapefileWriterCommand;
import jeql.command.io.TextReaderCommand;
import jeql.command.io.TextWriterCommand;
import jeql.command.io.XMLWriterCommand;
import jeql.command.io.kml.KMLReaderCommand;
import jeql.command.io.kml.KMLWriterCommand;
import jeql.command.plot.PlotCommand;
import jeql.command.test.AssertCommand;
import jeql.command.test.AssertEqualCommand;
import jeql.command.util.MemCommand;
import jeql.command.util.MemorizeCommand;
import jeql.command.util.UnionCommand;
import jeql.man.ManCommand;
import jeql.std.geom.PolygonizeCommand;
import jeql.util.ClassUtil;

public class CommandRegistry 
{
  public static final String CMD_SUFFIX = "Command";

  private Map procMap = new TreeMap();
  
  public CommandRegistry() {
    init();
  }

  private void init()
  {
    register(MemCommand.class);
    register(MemorizeCommand.class);
    register(UnionCommand.class);
    register(ManCommand.class);
    
    register(DbfReaderCommand.class);
    register(CSVReaderCommand.class);
    register(CSVWriterCommand.class);
    register(GMLWriterCommand.class);
    register(HtmlWriterCommand.class);
    register(KMLReaderCommand.class);
    register(KMLWriterCommand.class);
    register(PrintCommand.class);
    register(ShapefileReaderCommand.class);
    register(ShapefileWriterCommand.class);
    register(STFReaderCommand.class);
    register(STFWriterCommand.class);
    register(TextReaderCommand.class);
    register(TextWriterCommand.class);
    register(XMLWriterCommand.class);
    
    register(DbMetadata.class);
    register(DbReader.class);
    register(DbWriter.class);
    register(DbExec.class);
    register(PostgisReader.class);
    
    register(ChartCommand.class);
    register(PolygonizeCommand.class);
    register(PlotCommand.class);
    
    register(AssertCommand.class);
    register(AssertEqualCommand.class);
  }
  
  public void register(Class procClass)
  {
    register(procClass, false);
  }
  
  public void register(Class procClass, boolean allowReplacement)
  {
    String name = ClassUtil.classname(procClass);
    String jqlCmdName = FunctionRegistry.stripSuffix(name, CMD_SUFFIX);
    register(jqlCmdName, procClass, allowReplacement);
  }
  
  public void register(String cmdName, Class procClass, boolean allowReplacement)
  {
    // only register once
    if (! allowReplacement && procMap.containsKey(cmdName))
      throw new ConfigurationException("Command class " + cmdName 
          + " is already registered");
    
    procMap.put(cmdName, new CommandInvoker(cmdName, procClass));
  }

  public boolean hasProcedure(String name)
  {
    return procMap.containsKey(name);
  }
  
  public CommandInvoker getCommand(String name)
  {
     return (CommandInvoker) procMap.get(name);
  }
  
  public Collection getCommandNames()
  {
    return procMap.keySet();
  }
}

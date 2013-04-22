package jeql.workbench.model;

import java.io.IOException;

import jeql.util.IOUtil;

public class WorkbenchModel
{
  private static final String IMM_SCRIPT_NAME_PREFIX = "script";
  private static int immScriptCount = 1;
  
  private Settings settings = new Settings();
  
  public static String getNewScriptName()
  {
    return IMM_SCRIPT_NAME_PREFIX + immScriptCount++;
  }

  ScriptFileList scriptList = new ScriptFileList();
  private String scriptFile = "";
  private String script;
  
  public WorkbenchModel()
  {
    
  }
  
  public Settings getSettings() 
  {
    return settings;
  }
  public void setScriptFile(String scriptFile) 
  { 
    this.scriptFile = scriptFile; 
  }
  
  public String getScriptFile() { return scriptFile; }
  
  public void updateFileList()
  {
    scriptList.init();
  }
  
  public void load()
  {
    //scriptList.init();
    try {
      script = IOUtil.readFile(scriptFile, true);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String getScript() { return script; }

  public ScriptFileList getScriptList()
  {
    return scriptList;
  }
  
}

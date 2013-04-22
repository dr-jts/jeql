package jeql.workbench.model;

import java.io.File;

public class ScriptFile
{
  private String scriptFile;
  
  public ScriptFile(String scriptFile)
  {
    this.scriptFile = scriptFile;
  }
  
  public ScriptFile(File file)
  {
    this.scriptFile = file.getName();
  }
  
  public String toString()
  {
    return scriptFile;
  }
  
}

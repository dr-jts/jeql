package jeql.workbench.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jeql.util.FileUtil;

public class ScriptFileList
{
  private List<ScriptFile> script;
  
  public ScriptFileList()
  {
    
  }
  
  public void init()
  {
    script = new ArrayList<ScriptFile>();
    
    // TODO: pass this in from outside
    String cwd = System.getProperty("user.dir");
    File[] files = FileUtil.listFiles(cwd,  "jql", false);
    //File[] files = FileUtil.listFiles(cwd,  (String) null, false);
    for (File f : files) {
      script.add(new ScriptFile(f));
    }
    // testing only
    /*
    script.add(new ScriptFile(scriptFile));
    script.add(new ScriptFile(scriptFile));
    script.add(new ScriptFile(scriptFile));
    */
  }
  
  public List<ScriptFile> getList()
  {
    return script;
  }
  
  public int size()
  {
    return script.size();
  }
  
  
}

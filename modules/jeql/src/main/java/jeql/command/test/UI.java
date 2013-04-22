package jeql.command.test;

import javax.swing.JOptionPane;

import jeql.api.command.Command;
import jeql.engine.Scope;

public class UI 
implements Command
{

  public UI() {
  }

  public void execute(Scope scope)
  {
    JOptionPane.showInputDialog("Hello World!");
  }


}

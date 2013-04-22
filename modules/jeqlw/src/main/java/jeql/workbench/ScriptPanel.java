package jeql.workbench;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import jeql.workbench.images.IconLoader;
import jeql.workbench.model.ScriptSource;
import jeql.workbench.ui.assist.CodeSnippet;

public class ScriptPanel extends JPanel
{
  private JEditorPane scriptText = new JEditorPane();
  private ScriptStatusBar statusBar = new ScriptStatusBar();
  private boolean isEditable = false;
  private ScriptSource source;
  private boolean isInitialization = true;
  
  public ScriptPanel(boolean isEditable, ScriptSource source)
  {
    setEditable(isEditable);
    this.source = source;
    try {
      initUI();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initUI() throws Exception
  {
    //scriptText.setFont(new java.awt.Font("Monospaced", 0, 12));
    scriptText.setFont(new java.awt.Font("Lucida Console", 0, 12));
    //scriptText.setFont(new java.awt.Font("Courier New", 0, 12));
    
    scriptText.setEditable(isEditable);
    scriptText.setBorder(new EmptyBorder(4, 4, 4, 4));

    setMinimumSize(new Dimension(400, 50));
    setPreferredSize(new Dimension(900, 300));

    setLayout(new BorderLayout());
    add(new JScrollPane(scriptText), BorderLayout.CENTER);
    add(statusBar, BorderLayout.SOUTH);
    
    
    scriptText.addCaretListener(new CaretListener() {

      public void caretUpdate(CaretEvent arg0)
      {
        updateRowCol();
      }
    });
    Document doc = scriptText.getDocument();
    doc.addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent arg0)
      {
        setModified();
      }
      public void insertUpdate(DocumentEvent arg0)
      {
        setModified();
      }
      public void removeUpdate(DocumentEvent arg0)
      {
        setModified();
      }
    });
  }

  public void setEditable(boolean isEditable)
  {
    this.isEditable = isEditable;
    if (! isEditable) 
      scriptText.setBackground(WorkbenchConstants.BACKGROUND);
  }
  
  private void setModified()
  {
    if (! isEditable) return;
    
    if (! isInitialization) {
      source.setModified(true);
    }
    isInitialization = false;
    Workbench.view().scriptUpdateModified();
  }
  
  public void setText(String text)
  {
    scriptText.setText(text);
    scriptText.setCaretPosition(0);
  }
  
  public void loadText()
  {
    setText(source.getText());
  }
  
  public String getText()
  {
    return scriptText.getText();
  }

  public String getTitle()
  {
    return source.getTitle();
  }

  public ImageIcon getIcon()
  {
    if (getTitle().equals(WorkbenchConstants.INSPECT_TAB_NAME))
      return IconLoader.INSPECT_TEXT;
    return IconLoader.SCRIPT_SMALL;
  }
  
  public ScriptSource getSource()
  {
    return source;
  }
  
  public boolean isInternal()
  {
    // TODO - use better logic here
    return isEditable;
  }
  
  public void updateRowCol()
  {
    int pos = scriptText.getCaretPosition();
    Document doc = scriptText.getDocument();
    PlainDocument plainDoc = (PlainDocument) doc;
    int row = plainDoc.getDefaultRootElement().getElementIndex(pos);
    int lineOffset = plainDoc.getDefaultRootElement().getElement(row).getStartOffset();
    int col = pos - lineOffset + 1;
    //Workbench.view().scriptCaretUpdate(lineNum + 1, col + 1);      }
    statusBar.setRow(row + 1, col);
  }
  
  public void insertCodeSnippet(CodeSnippet code)
  {
    int pos = scriptText.getCaretPosition();
    Document doc = scriptText.getDocument();
    try {
      if (code.isSplitCode()) {
        doc.insertString(pos, code.getCode1(), null);
        int pos2 = scriptText.getCaretPosition();
        doc.insertString(pos2, code.getCode2(), null);
        scriptText.setCaretPosition(pos2);
      }
      else {
        doc.insertString(pos, code.getCode(), null);
      }
    }
    catch (BadLocationException e) {
      // TODO - ok to ignore this exception?
      e.printStackTrace();
    }
    // help user by switching focus to inserted text
    boolean gotFocus = scriptText.requestFocusInWindow();
  }

  public void save()
  {
    // TODO Auto-generated method stub
    source.setText(getText());
    source.save();
  }
}

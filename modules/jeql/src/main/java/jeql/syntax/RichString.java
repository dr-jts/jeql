package jeql.syntax;

import java.util.ArrayList;
import java.util.List;

import jeql.engine.Scope;
import jeql.engine.UndefinedVariableException;
import jeql.syntax.util.StringLiteralUtil;

/**
 * Represents a RichString,
 * and provides lexing from the tokenImage contents
 * (without surrounding quotes).
 * A RichString supports the standard Java-style character quoting.
 * It can also spread across multiple lines in the
 * defining file, which will be preserved as EOLs in the
 * string value.
 * A RichString can contain "$" references to variables
 * defined in the current scope, which will be inserted as string values.
 * 
 * @author Martin Davis
 *
 */
public class RichString 
{
  // lexical scanning states
  private static final int STATE_STR = 1;
  private static final int STATE_VAR = 2;
  private static final int STATE_VAR_PAREN = 3;
  
  // markers for section types
  private static final String TYPE_STRING = "string";
  private static final String TYPE_VAR = "var";
  
  private static boolean isVarChar(char c)
  {
    if (Character.isLetterOrDigit(c) || c == '_')
      return true;
    return false;
  }
  
  private String strLiteral;
  private List sectionVal = new ArrayList();
  private List sectionType = new ArrayList();
  
  public RichString(String tokenContents) 
  {
    this.strLiteral = tokenContents;
    parse(strLiteral);
    checkVars();
    unescapeStrings();
  }

  private void parse(String strLiteral)
  {
    int pos = 0;
    int len = strLiteral.length();
    int state = STATE_STR;
    int sectionStartPos = 0;
    
    while (true) {
      char c = 0;
      if (pos < len)
        c = strLiteral.charAt(pos);
      pos++;
      
      switch (state) {
      case STATE_STR:
        switch (c) {
        case '\\':
          // scan past escaped char
          pos++;
          break;
        case '$':
          state = STATE_VAR;
          add(TYPE_STRING, sectionStartPos, pos-1);
          sectionStartPos = pos;
          break;
        case 0:
          add(TYPE_STRING, sectionStartPos, pos-1);
          return;
        }
        break;
      case STATE_VAR:
        if (c == '{') {
          state = STATE_VAR_PAREN;
          sectionStartPos = pos;
        }
        else if (isVarChar(c)) {
          // scan this char of variable
        }
        else if (c == 0) {
          add(TYPE_VAR, sectionStartPos, pos-1);
          sectionStartPos = pos;
          return;
        }
        else {
          // not var char - finish var scanning
          state = STATE_STR;
          add(TYPE_VAR, sectionStartPos, pos-1);
          sectionStartPos = pos - 1;
        }
        break;
      case STATE_VAR_PAREN:
        if (c == '}') {
          // finish var scanning
          state = STATE_STR;
          add(TYPE_VAR, sectionStartPos, pos-1);
          sectionStartPos = pos;
        }
        else if (c == 0) {
          add(TYPE_VAR, sectionStartPos, pos-1);
          return;          
        }
        // otherwise scan char as variable char.
        break;
      }
    }
  }
  
  private void add(Object typeCode, int start, int end)
  {
    String section = strLiteral.substring(start, end);
    sectionVal.add(section);
    sectionType.add(typeCode);
  }

  private void checkVars()
  {
    // should do something here
  }
  
  private void unescapeStrings() {
    for (int i = 0; i < sectionVal.size(); i++) {
      if (sectionType.get(i) == TYPE_STRING) {
        String s = (String) sectionVal.get(i);
        sectionVal.set(i, StringLiteralUtil.decodeEscapedString(s));
      }
    }
  }

  public Object eval(Scope scope) 
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < sectionVal.size(); i++) {
      String s = (String) sectionVal.get(i);
      if (sectionType.get(i) == TYPE_STRING) {
        buf.append(s);
      } 
      else {
        // variable reference - insert value
        if (! scope.hasVariable(s)) {
          throw new UndefinedVariableException(s);
        }
        Object o = scope.getVariable(s);
        // if variable is null, add empty string to avoid error
        if (o != null)
          buf.append(o.toString());
      }
    }
    return buf.toString();
  }

}

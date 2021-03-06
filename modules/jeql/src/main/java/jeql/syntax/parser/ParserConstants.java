/* Generated By:JavaCC: Do not edit this line. ParserConstants.java */
package jeql.syntax.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int LINE_COMMENT = 5;
  /** RegularExpression Id. */
  int MULTI_LINE_COMMENT = 6;
  /** RegularExpression Id. */
  int K_AND = 7;
  /** RegularExpression Id. */
  int K_AS = 8;
  /** RegularExpression Id. */
  int K_ASC = 9;
  /** RegularExpression Id. */
  int K_BY = 10;
  /** RegularExpression Id. */
  int K_CASE = 11;
  /** RegularExpression Id. */
  int K_DESC = 12;
  /** RegularExpression Id. */
  int K_DISTINCT = 13;
  /** RegularExpression Id. */
  int K_ELSE = 14;
  /** RegularExpression Id. */
  int K_END = 15;
  /** RegularExpression Id. */
  int K_EXCEPT = 16;
  /** RegularExpression Id. */
  int K_EXISTS = 17;
  /** RegularExpression Id. */
  int K_FROM = 18;
  /** RegularExpression Id. */
  int K_GROUP = 19;
  /** RegularExpression Id. */
  int K_IMPORT = 20;
  /** RegularExpression Id. */
  int K_IN = 21;
  /** RegularExpression Id. */
  int K_INNER = 22;
  /** RegularExpression Id. */
  int K_JOIN = 23;
  /** RegularExpression Id. */
  int K_LET = 24;
  /** RegularExpression Id. */
  int K_LEFT = 25;
  /** RegularExpression Id. */
  int K_LIMIT = 26;
  /** RegularExpression Id. */
  int K_NOT = 27;
  /** RegularExpression Id. */
  int K_OFFSET = 28;
  /** RegularExpression Id. */
  int K_ON = 29;
  /** RegularExpression Id. */
  int K_OR = 30;
  /** RegularExpression Id. */
  int K_ORDER = 31;
  /** RegularExpression Id. */
  int K_OUTER = 32;
  /** RegularExpression Id. */
  int K_PROGRAM = 33;
  /** RegularExpression Id. */
  int K_RIGHT = 34;
  /** RegularExpression Id. */
  int K_SELECT = 35;
  /** RegularExpression Id. */
  int K_SPLIT = 36;
  /** RegularExpression Id. */
  int K_TABLE = 37;
  /** RegularExpression Id. */
  int K_THEN = 38;
  /** RegularExpression Id. */
  int K_VALUES = 39;
  /** RegularExpression Id. */
  int K_WHEN = 40;
  /** RegularExpression Id. */
  int K_WHERE = 41;
  /** RegularExpression Id. */
  int K_WITH = 42;
  /** RegularExpression Id. */
  int K_XOR = 43;
  /** RegularExpression Id. */
  int K_EMPTY = 44;
  /** RegularExpression Id. */
  int K_LINEARRING = 45;
  /** RegularExpression Id. */
  int K_LINESTRING = 46;
  /** RegularExpression Id. */
  int K_POLYGON = 47;
  /** RegularExpression Id. */
  int K_POINT = 48;
  /** RegularExpression Id. */
  int K_MULTIPOINT = 49;
  /** RegularExpression Id. */
  int K_MULTILINESTRING = 50;
  /** RegularExpression Id. */
  int K_MULTIPOLYGON = 51;
  /** RegularExpression Id. */
  int K_GEOMETRYCOLLECTION = 52;
  /** RegularExpression Id. */
  int K_BOX = 53;
  /** RegularExpression Id. */
  int S_NUMBER = 54;
  /** RegularExpression Id. */
  int FLOAT = 55;
  /** RegularExpression Id. */
  int INTEGER = 56;
  /** RegularExpression Id. */
  int DIGIT = 57;
  /** RegularExpression Id. */
  int S_CONST_LITERAL = 58;
  /** RegularExpression Id. */
  int S_IDENTIFIER = 59;
  /** RegularExpression Id. */
  int S_QUOTED_IDENTIFIER = 60;
  /** RegularExpression Id. */
  int S_IDENTIFIER_KEY = 61;
  /** RegularExpression Id. */
  int LETTER = 62;
  /** RegularExpression Id. */
  int SPECIAL_CHARS = 63;
  /** RegularExpression Id. */
  int S_STRING_LITERAL = 64;
  /** RegularExpression Id. */
  int S_RAW_STRING_LITERAL = 65;
  /** RegularExpression Id. */
  int S_RICH_STRING_LITERAL = 66;
  /** RegularExpression Id. */
  int ASSIGN = 67;
  /** RegularExpression Id. */
  int ASTERISK = 68;
  /** RegularExpression Id. */
  int COLON = 69;
  /** RegularExpression Id. */
  int COMMA = 70;
  /** RegularExpression Id. */
  int DOT = 71;
  /** RegularExpression Id. */
  int GREATER = 72;
  /** RegularExpression Id. */
  int GREATEREQUAL = 73;
  /** RegularExpression Id. */
  int LESS = 74;
  /** RegularExpression Id. */
  int LESSEQUAL = 75;
  /** RegularExpression Id. */
  int EQUAL = 76;
  /** RegularExpression Id. */
  int MINUS = 77;
  /** RegularExpression Id. */
  int NOTEQUAL = 78;
  /** RegularExpression Id. */
  int NOTEQUAL2 = 79;
  /** RegularExpression Id. */
  int OPENBRACE = 80;
  /** RegularExpression Id. */
  int CLOSEBRACE = 81;
  /** RegularExpression Id. */
  int OPENBRACKET = 82;
  /** RegularExpression Id. */
  int CLOSEBRACKET = 83;
  /** RegularExpression Id. */
  int OPENPAREN = 84;
  /** RegularExpression Id. */
  int CLOSEPAREN = 85;
  /** RegularExpression Id. */
  int PERCENT = 86;
  /** RegularExpression Id. */
  int PLUS = 87;
  /** RegularExpression Id. */
  int REGEX_FIND = 88;
  /** RegularExpression Id. */
  int REGEX_MATCH = 89;
  /** RegularExpression Id. */
  int QUESTIONMARK = 90;
  /** RegularExpression Id. */
  int SLASH = 91;
  /** RegularExpression Id. */
  int SEMICOLON = 92;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\r\"",
    "\"\\n\"",
    "<LINE_COMMENT>",
    "<MULTI_LINE_COMMENT>",
    "\"and\"",
    "\"as\"",
    "\"asc\"",
    "\"by\"",
    "\"case\"",
    "\"desc\"",
    "\"distinct\"",
    "\"else\"",
    "\"end\"",
    "\"except\"",
    "\"exists\"",
    "\"from\"",
    "\"group\"",
    "\"import\"",
    "\"in\"",
    "\"inner\"",
    "\"join\"",
    "\"let\"",
    "\"left\"",
    "\"limit\"",
    "\"not\"",
    "\"offset\"",
    "\"on\"",
    "\"or\"",
    "\"order\"",
    "\"outer\"",
    "\"program\"",
    "\"right\"",
    "\"select\"",
    "\"split\"",
    "\"table\"",
    "\"then\"",
    "\"values\"",
    "\"when\"",
    "\"where\"",
    "\"with\"",
    "\"xor\"",
    "\"EMPTY\"",
    "\"LINEARRING\"",
    "\"LINESTRING\"",
    "\"POLYGON\"",
    "\"POINT\"",
    "\"MULTIPOINT\"",
    "\"MULTILINESTRING\"",
    "\"MULTIPOLYGON\"",
    "\"GEOMETRYCOLLECTION\"",
    "\"BOX\"",
    "<S_NUMBER>",
    "<FLOAT>",
    "<INTEGER>",
    "<DIGIT>",
    "<S_CONST_LITERAL>",
    "<S_IDENTIFIER>",
    "<S_QUOTED_IDENTIFIER>",
    "<S_IDENTIFIER_KEY>",
    "<LETTER>",
    "<SPECIAL_CHARS>",
    "<S_STRING_LITERAL>",
    "<S_RAW_STRING_LITERAL>",
    "<S_RICH_STRING_LITERAL>",
    "\"=\"",
    "\"*\"",
    "\":\"",
    "\",\"",
    "\".\"",
    "\">\"",
    "\">=\"",
    "\"<\"",
    "\"<=\"",
    "\"==\"",
    "\"-\"",
    "\"!=\"",
    "\"<>\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\"(\"",
    "\")\"",
    "\"%\"",
    "\"+\"",
    "\"~\"",
    "\"~=\"",
    "\"?\"",
    "\"/\"",
    "\";\"",
    "\".*\"",
  };

}

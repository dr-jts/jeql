package jeql.api.row;

import jeql.engine.TableConstants;
import jeql.util.ClassUtil;

/**
 * The list of column names and types for the {@link Row}s in a {@link RowList}.
 * 
 * @author Martin Davis
 *
 */
public class RowSchema 
{
  public static boolean isValidColumnName(String string)
  {
    // check not number
    // check no spaces?
    // check chars in [0-9a-zA-Z_]?
    return true;
  }
  
  public static String getDefaultColumnName(int i)
  {
    return TableConstants.DEFAULT_COL_PREFIX + i;
  }

  public static RowSchema getDefaultNamedSchema(Class[] types)
  {
    String[] names = new String[types.length];
    for (int i = 0; i < types.length; i++) {
      names[i] = getDefaultColumnName(i + 1);
    }
    return new RowSchema(names, types);
  }
  
  private String[] colName;
  private Class[] colType;
  
  public RowSchema(int size) 
  {
    colName = new String[size];
    colType = new Class[size];
  }
  
  public RowSchema(String[]name, Class[] types) 
  {
    this(name.length);
    if (name.length != types.length) {
      throw new IllegalStateException("Schemas must have same number of names and types");
    }
    System.arraycopy(name, 0, colName, 0, name.length);
    System.arraycopy(types, 0, colType, 0, types.length);
  }
    
  public RowSchema(String[]name, RowSchema schema) 
  {
    this(name, schema.colType);
  }
    
  /**
   * Creates a RowSchema containing one column of the given type
   * @param type
   */
  public RowSchema(String name, Class type) {
    this(1);
    setColumnDef(0, name, type);
  }
  
  public RowSchema(RowSchema schema)
  {
    this(schema.size());
    copy(schema, this, 0);
  }
  
  public RowSchema(RowSchema schema0, RowSchema schema1)
  {
    this(schema0.size() + schema1.size());
    copy(schema0, this, 0);
    copy(schema1, this, schema0.size());
  }
  
  public void copy(RowSchema src, RowSchema dest)
  {
    copy(src, dest, 0);
  }
  
  public void copy(RowSchema src, RowSchema dest, int startIndex)
  {
    for (int i = 0; i < src.size(); i++) {
      dest.setColumnDef(i + startIndex, src.getName(i), src.getType(i));
    }
  }
  
  public int size() { return colType.length; }
  
  public void setColumnDef(int i, String name, Class typeClass)
  {
    colName[i] = name;
    colType[i] = typeClass;
  }
  
  public String getName(int i) { return colName[i]; }

  public Class getType(int i) { return colType[i]; }

  public boolean equals(Object o)
  {
    RowSchema schema = (RowSchema) o;
    if (size() != schema.size())
      return false;
    for (int i = 0; i < size(); i++) {
      Class t1 = getType(i);
      Class t2 = schema.getType(i);
      if (t1 != t2) return false;
    }
    return true;
  }
  
  public boolean equalsWithNames(Object o)
  {
    RowSchema schema = (RowSchema) o;
    if (size() != schema.size())
      return false;
    for (int i = 0; i < size(); i++) {
      Class t1 = getType(i);
      Class t2 = schema.getType(i);
      if (t1 != t2) return false;
      if (! getName(i).equals(schema.getName(i)))
        return false;
    }
    return true;
  }
  
  public int getColIndex(String name) {
    for (int i = 0; i < colType.length; i++) {
      if (colName[i].equals(name))
        return i;
    }
    return -1;
  }
  
  /**
   * Gets the index of a given named column
   * 
   * @param name of column
   * @return index of column name
   * @return -1 if column does not exist
   */
  public int getColIndexIgnoreCase(String name) {
    for (int i = 0; i < colType.length; i++) {
      if (colName[i].equalsIgnoreCase(name))
        return i;
    }
    return -1;
  }
  
  public boolean hasCol(String name)
  {
    return getColIndex(name) >= 0;
  }
  
  public boolean hasColIgnoreCase(String name)
  {
    return getColIndexIgnoreCase(name) >= 0;
  }
  
  public String toString() 
  {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < size(); i++) {
      if (i > 0)
        buf.append(", ");
      buf.append(getName(i) + ":" + ClassUtil.classname(getType(i)));
    }
    return buf.toString();
  }


}

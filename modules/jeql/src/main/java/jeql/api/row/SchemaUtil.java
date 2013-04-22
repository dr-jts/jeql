package jeql.api.row;


public class SchemaUtil 
{

  /**
   * Finds the index of the first column which has the specified type
   * 
   * @param schema schema
   * @param type column type to find
   * @return index of first column of required type
   * @return -1 if no column of the required type exists
   */
  public static int getColumnWithType(RowSchema schema, Class type)
  {
    for (int i = 0; i < schema.size(); i++) {
      if (schema.getType(i) == type)
        return i;
    }
    return -1;
  }
  
  public static int getNumericColumn(RowSchema schema, int blackListIndex)
  {
    for (int i = 0; i < schema.size(); i++) {
      if (isNumeric(schema, i) && i != blackListIndex)
        return i;
    }
    return -1;
  }
  
  public static boolean isNumeric(RowSchema schema, int colIndex)
  {
    Class cls = schema.getType(colIndex);
    return Number.class.isAssignableFrom(cls);
  }
  
  public static int getColumnIndex(RowSchema schema, String name)
  {
    return getColumnIndex(schema, name, null);
  }
  public static int getColumnIndex(RowSchema schema, String name1, String name2)
  {
    int index = -1;
    if (name1 != null) {
      index = schema.getColIndexIgnoreCase(name1);
      if (index >= 0) return index;
    }
    if (name2 != null) { 
      index = schema.getColIndexIgnoreCase(name2);
    }
    return index;
  }
  
  public static int getColumnIndex(RowSchema schema, String name1, String name2, String name3)
  {
    int index = -1;
    if (name1 != null) {
      index = schema.getColIndexIgnoreCase(name1);
      if (index >= 0) return index;
    }
    if (name2 != null) {
      index = schema.getColIndexIgnoreCase(name2);
      if (index >= 0) return index;
    }
    if (name3 != null) { 
      index = schema.getColIndexIgnoreCase(name3);
    }
    return index;
  }

}

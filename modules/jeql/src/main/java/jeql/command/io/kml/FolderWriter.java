package jeql.command.io.kml;

import java.io.IOException;
import java.io.PrintWriter;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;

/**
 * Adds folder prologue and epilogue elements to 
 * a stream of rows which contain appropriate columns
 * specifying folder partitioning.
 * 
 * @author Martin Davis
 *
 */
class FolderWriter
{
  private boolean doWriteFolders = false; 
  private String currentName = null;
  private int folderGroupColIndex = -1;
  private int folderVisibilityIndex = -1;
  private int folderOpenIndex = -1;
  private boolean isInFolder = false;
  
  public FolderWriter(RowSchema rs)
  {
    initColumns(rs);
    if (folderGroupColIndex >= 0) {
      doWriteFolders = true;
    }
  }
  
  private void initColumns(RowSchema rs)
  {
    // can use either id or name for folder grouping
    folderGroupColIndex = rs.getColIndexIgnoreCase(KMLCol.KML_FOLDER_ID);
    if (folderGroupColIndex < 0) {
      folderGroupColIndex = rs.getColIndexIgnoreCase(KMLCol.KML_FOLDER_NAME);
    }
    folderVisibilityIndex = rs.getColIndexIgnoreCase(KMLCol.KML_FOLDER_VISIBILITY);    
    folderOpenIndex = rs.getColIndexIgnoreCase(KMLCol.KML_FOLDER_OPEN);    
  }
  
  public void nextRow(Row row, PrintWriter writer)
  throws IOException 
  {
    if (! doWriteFolders) return;
    
    String folderName = (String) row.getValue(folderGroupColIndex);
    
    // no change in name
    if (currentName != null
        && currentName.equals(folderName)) return;
    
    if (isInFolder) {
      writeEpilogue(writer);
    }
    
    // at this point we know we are starting a new folder
    writer.println();
    writer.println("<Folder>");
    writer.println("  <name>" + folderName + "</name>");
    XMLWriter.writeElement(writer, 2, KMLCol.KML_VISIBILITY, folderVisibilityIndex, row, null, false);
    XMLWriter.writeElement(writer, 2, KMLCol.KML_OPEN, folderOpenIndex, row, null, false);
    currentName = folderName;
    isInFolder = true;
  }
  
  public void finishedRows(PrintWriter writer)
  throws IOException 
  {
    writeEpilogue(writer);
  }

  private void writeEpilogue(PrintWriter writer)
  {
    if (! doWriteFolders) return;
    if (! isInFolder) return;
    writer.println("</Folder>");
  }
}
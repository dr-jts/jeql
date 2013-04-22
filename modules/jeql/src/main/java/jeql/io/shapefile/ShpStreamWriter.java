package jeql.io.shapefile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import jeql.io.EndianDataOutputStream;
import jeql.std.function.FileFunction;

import org.geotools.shapefile.PointHandler;
import org.geotools.shapefile.ShapeHandler;
import org.geotools.shapefile.ShapefileHeader;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Writes a stream of geometries to a .shp file. 
 * Geometries are written in a stream, non-memory resident fashion.
 * The shp file header is rewritten with the final correct values.
 */
public class ShpStreamWriter {
  private static final int SHP_HDR_LEN_WORDS = 50;
  private static final int SHP_RECORD_HDR_LEN_WORDS = 4;

  private URL baseURL;
  private String filenameSHX;
  private String filename;
  private int coordDimension = 2;
  private ShapeHandler handler = null;
  private EndianDataOutputStream outStream;
  private EndianDataOutputStream outStreamSHX;
  private int fileLen = 0;
  private int filePos = 0;
  private int shapeType = -1;
  private int shapeCount = 0;
  private Envelope fileEnv = new Envelope();
  private int geomIndex = 0;

  /**
   * Creates a writer to the given url
   * 
   * @param url
   *          The url of the shapefile
   */
  public ShpStreamWriter(URL url) {
    this(url.getFile());
    baseURL = url;
  }

  public ShpStreamWriter(String filename) {
    this.filename = filename;
    filenameSHX = FileFunction.pathNoExt(filename) + ".shx";
  }

  private EndianDataOutputStream createOutputStream(String filename) throws IOException {
    BufferedOutputStream in = new BufferedOutputStream(new FileOutputStream(
        filename));
    EndianDataOutputStream os = new EndianDataOutputStream(in);
    return os;
  }

  private void initSHP() throws IOException
  {
    if (outStream != null) return;
    outStream = createOutputStream(filename);
    ShapefileHeader.write(outStream, 0, 0, new Envelope());
    filePos = SHP_HDR_LEN_WORDS; // header length in WORDS
  }
  
  private void initSHX() throws IOException
  {
    if (outStreamSHX != null) return;
    outStreamSHX = createOutputStream(filenameSHX);
    ShapefileHeader.write(outStreamSHX, 0, 0, new Envelope());
  }
  
  private void initHandler(Geometry geom)
  throws Exception
  {
    if (handler != null) return;
    
    // TODO: fix to correctly determine type
    if (geom == null) {
      handler = new PointHandler(); // default
    } else {
      coordDimension = ShapefileWriter.determineCoordinateDimension(geom);
      handler = Shapefile.getShapeHandler(geom, coordDimension);
    }
    shapeType = handler.getShapeType();
  }
  
  public void write(Geometry geom)
      throws IOException, Exception {
    initSHP();
    initSHX();
    initHandler(geom);
    
    // update counters
    fileEnv.expandToInclude(geom.getEnvelopeInternal());
    shapeCount++;
    
    // write SHP record
    outStream.writeIntBE(geomIndex + 1);
    geomIndex += 1;
    int len = handler.getLength(geom);
    outStream.writeIntBE(len);
    fileLen += SHP_RECORD_HDR_LEN_WORDS; // length of record header in WORDS
    handler.write(geom, outStream);
    fileLen += len; // length of shape in WORDS
    
    // write SHX record
    outStreamSHX.writeIntBE(filePos);
    outStreamSHX.writeIntBE(len);
    filePos = filePos + len + SHP_RECORD_HDR_LEN_WORDS;

  }

  private static final String HDR_WRITE_MODE = "rws";
  
  public void close() throws IOException {
    outStream.flush();
    outStream.close();
    
    // rewrite correct SHP header
    RandomAccessFile rafshp = new RandomAccessFile(filename, HDR_WRITE_MODE);
    rafshp.write(ShapefileHeader.write(fileLen, shapeType, fileEnv));
    rafshp.close();
    
    outStreamSHX.flush();
    outStreamSHX.close();

    // rewrite correct SHP header
    RandomAccessFile rafSHX = new RandomAccessFile(filenameSHX, HDR_WRITE_MODE);
    rafSHX.write(ShapefileHeader.writeToIndex(shapeCount, shapeType, fileEnv));
    rafSHX.close();
    

  }


}

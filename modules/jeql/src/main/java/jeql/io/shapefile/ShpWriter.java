package jeql.io.shapefile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import jeql.io.EndianDataOutputStream;

import org.geotools.shapefile.PointHandler;
import org.geotools.shapefile.ShapeHandler;
import org.geotools.shapefile.ShapefileHeader;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

/**
 * Writes a list of geometries to a .shp file.
 * Requires all geometries to be resident in memory, 
 * in order to write shp header at start of file.
 */
public class ShpWriter 
{
  private URL baseURL;

  /**
   * Creates a writer to the given url
   * @param url The url of the shapefile
   */
  public ShpWriter(URL url) {
    baseURL = url;
  }

  private EndianDataOutputStream getOutputStream() throws IOException {
    BufferedOutputStream in = new BufferedOutputStream(new FileOutputStream(
        baseURL.getFile()));
    EndianDataOutputStream os = new EndianDataOutputStream(in);
    return os;
  }

  /**
   * Saves a shapefile to the output stream.
   * 
   * @param coordDimension  2=x,y ; 3=x,y,m ; 4=x,y,z,m
   */
  public void write(GeometryCollection geometries, int coordDimension)
      throws IOException, Exception {
    EndianDataOutputStream outStream = getOutputStream();
    ShapefileHeader header = new ShapefileHeader(geometries,
        coordDimension);
    header.write(outStream);
    int pos = 50; // header length in WORDS
    //records;
    //body;
    //header;
    int numShapes = geometries.getNumGeometries();
    ShapeHandler handler;

    if (geometries.getNumGeometries() == 0) {
      handler = new PointHandler(); //default
    } else {
      handler = Shapefile.getShapeHandler(geometries.getGeometryN(0),
          coordDimension);
    }

    for (int i = 0; i < numShapes; i++) {
      Geometry body = geometries.getGeometryN(i);
      outStream.writeIntBE(i + 1);
      outStream.writeIntBE(handler.getLength(body));
      pos += 4; // length of header in WORDS
      handler.write(body, outStream);
      pos += handler.getLength(body); // length of shape in WORDS
    }
    outStream.flush();
    outStream.close();
  }

  //ShapeFileDimentions =>    2=x,y ; 3=x,y,m ; 4=x,y,z,m
  public synchronized void writeIndex(GeometryCollection geometries,
      int coordDimension, 
      String shxPath) throws IOException,
      Exception {
    Geometry geom;

    BufferedOutputStream in = new BufferedOutputStream(new FileOutputStream(shxPath));
    EndianDataOutputStream outStream = new EndianDataOutputStream(in);
    
    ShapeHandler handler;
    int nrecords = geometries.getNumGeometries();
    ShapefileHeader mainHeader = new ShapefileHeader(geometries,
        coordDimension);

    if (geometries.getNumGeometries() == 0) {
      handler = new PointHandler(); //default
    } 
    else {
      handler = Shapefile.getShapeHandler(geometries.getGeometryN(0),
          coordDimension);
    }

    // mainHeader.fileLength = 50 + 4*nrecords;

    mainHeader.writeToIndex(outStream);
    int pos = 50;
    int len = 0;

    for (int i = 0; i < nrecords; i++) {
      geom = geometries.getGeometryN(i);
      len = handler.getLength(geom);

      outStream.writeIntBE(pos);
      outStream.writeIntBE(len);
      pos = pos + len + 4;
    }
    outStream.flush();
    outStream.close();
  }

}

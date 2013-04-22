package jeql.io.shapefile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jeql.io.EndianDataOutputStream;

import org.geotools.shapefile.PointHandler;
import org.geotools.shapefile.ShapeHandler;
import org.geotools.shapefile.Shapefile;
import org.geotools.shapefile.ShapefileHeader;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

/**
 * Writes a shapefile in a streaming fashion.
 * 
 * NOT COMPLETE Need to deal with requirement to write length and BBox in
 * header!
 * 
 * @author Martin Davis
 * 
 */
public class ShapefileStreamWriter {
  EndianDataOutputStream outStream = null;

  File file;

  public ShapefileStreamWriter(String filename) {
    file = new File(filename);
  }

  private EndianDataOutputStream getOutputStream(File file) throws IOException {
    BufferedOutputStream in = new BufferedOutputStream(new FileOutputStream(
        file));
    EndianDataOutputStream sfile = new EndianDataOutputStream(in);
    return sfile;
  }

  private EndianDataOutputStream getOutStream() throws IOException {
    if (outStream == null) {
      outStream = getOutputStream(file);
    }
    return outStream;
  }

  public void write(GeometryCollection geometries, int shapeFileDimension)
      throws IOException, Exception {
    EndianDataOutputStream file = getOutStream();
    ShapefileHeader mainHeader = new ShapefileHeader(geometries,
        shapeFileDimension);
    mainHeader.write(file);
    int pos = 50; // header length in WORDS
    // records;
    // body;
    // header;
    int numShapes = geometries.getNumGeometries();
    Geometry body;
    ShapeHandler handler;

    if (geometries.getNumGeometries() == 0) {
      handler = new PointHandler(); // default
    } else {
      handler = Shapefile.getShapeHandler(geometries.getGeometryN(0),
          shapeFileDimension);
    }

    for (int i = 0; i < numShapes; i++) {
      body = geometries.getGeometryN(i);
      file.writeIntBE(i + 1);
      file.writeIntBE(handler.getLength(body));
      pos += 4; // length of header in WORDS
      handler.write(body, file);
      pos += handler.getLength(body); // length of shape in WORDS
    }
    file.flush();
    file.close();
  }

  // ShapeFileDimentions => 2=x,y ; 3=x,y,m ; 4=x,y,z,m
  public synchronized void writeIndex(GeometryCollection geometries,
      EndianDataOutputStream file, int ShapeFileDimentions) throws IOException,
      Exception {
    Geometry geom;

    ShapeHandler handler;
    int nrecords = geometries.getNumGeometries();
    ShapefileHeader mainHeader = new ShapefileHeader(geometries,
        ShapeFileDimentions);

    if (geometries.getNumGeometries() == 0) {
      handler = new PointHandler(); // default
    } else {
      handler = Shapefile.getShapeHandler(geometries.getGeometryN(0),
          ShapeFileDimentions);
    }

    // mainHeader.fileLength = 50 + 4*nrecords;

    mainHeader.writeToIndex(file);
    int pos = 50;
    int len = 0;

    // file.setLittleEndianMode(false);

    for (int i = 0; i < nrecords; i++) {
      geom = geometries.getGeometryN(i);
      len = handler.getLength(geom);

      file.writeIntBE(pos);
      file.writeIntBE(len);
      pos = pos + len + 4;
    }
    file.flush();
    file.close();
  }

}

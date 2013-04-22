/*
 * Header.java
 *
 * Created on February 12, 2002, 3:29 PM
 */

package org.geotools.shapefile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jeql.io.EndianDataInputStream;
import jeql.io.EndianDataOutputStream;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryCollection;

/**
 * 
 * @author jamesm
 */
public class ShapefileHeader {
  private final static boolean DEBUG = false;

  private int fileCode = -1;
  public int fileLength = -1;
  private int indexLength = -1;
  private int version = -1;
  private int shapeType = -1;
  private Envelope bounds;

  public ShapefileHeader(EndianDataInputStream file) throws IOException {
    // file.setLittleEndianMode(false);
    fileCode = file.readIntBE();
    // if(DEBUG)System.out.println("Sfh->Filecode "+fileCode);
    if (fileCode != Shapefile.SHAPEFILE_ID)
      System.err.println("Sfh->WARNING filecode " + fileCode
          + " not a match for documented shapefile code "
          + Shapefile.SHAPEFILE_ID);

    for (int i = 0; i < 5; i++) {
      int tmp = file.readIntBE();
      // if(DEBUG)System.out.println("Sfh->blank "+tmp);
    }
    fileLength = file.readIntBE();

    // file.setLittleEndianMode(true);
    version = file.readIntLE();
    shapeType = file.readIntLE();

    // read in and for now ignore the bounding box
    for (int i = 0; i < 4; i++) {
      file.readDoubleLE();
    }

    // skip remaining unused bytes
    // file.setLittleEndianMode(false);//well they may not be unused forever...
    file.skipBytes(32);
  }

  public ShapefileHeader(GeometryCollection geometries, int dims)
      throws Exception {
    ShapeHandler handler;
    if (geometries.getNumGeometries() == 0) {
      handler = new PointHandler(); // default
    } else {
      handler = Shapefile.getShapeHandler(geometries.getGeometryN(0), dims);
    }
    int numShapes = geometries.getNumGeometries();
    shapeType = handler.getShapeType();
    version = Shapefile.VERSION;
    fileCode = Shapefile.SHAPEFILE_ID;
    bounds = geometries.getEnvelopeInternal();
    fileLength = 0;
    for (int i = 0; i < numShapes; i++) {
      fileLength += handler.getLength(geometries.getGeometryN(i));
      fileLength += 4;// for each header
    }
    fileLength += 50;// space used by this, the main header
    indexLength = 50 + (4 * numShapes);
  }

  public void setFileLength(int fileLength) {
    this.fileLength = fileLength;
  }

  public void write(EndianDataOutputStream file) throws IOException {
    write(file, fileLength, shapeType, bounds);
  }
  
  /*
  public void OLDwrite(EndianDataOutputStream file) throws IOException {
    int pos = 0;
    file.writeIntBE(Shapefile.SHAPEFILE_ID);
    pos += 4;
    for (int i = 0; i < 5; i++) {
      file.writeIntBE(0);// Skip unused part of header
      pos += 4;
    }
    file.writeIntBE(fileLength);
    pos += 4;
    // file.setLittleEndianMode(true);
    file.writeIntLE(Shapefile.VERSION);
    pos += 4;
    file.writeIntLE(shapeType);
    pos += 4;
    // write the bounding box
    file.writeDoubleLE(bounds.getMinX());
    file.writeDoubleLE(bounds.getMinY());
    file.writeDoubleLE(bounds.getMaxX());
    file.writeDoubleLE(bounds.getMaxY());
    pos += 8 * 4;

    // skip remaining unused bytes
    for (int i = 0; i < 4; i++) {
      file.writeDoubleLE(0.0);// Skip unused part of header
      pos += 8;
    }

    if (DEBUG)
      System.out.println("Sfh->Position " + pos);
  }
  */
  
  public static void write(EndianDataOutputStream file,
      int fileLength, int shapeType, Envelope bounds) throws IOException {
    int pos = 0;
    file.writeIntBE(Shapefile.SHAPEFILE_ID);
    pos += 4;
    for (int i = 0; i < 5; i++) {
      file.writeIntBE(0);// Skip unused part of header
      pos += 4;
    }
    file.writeIntBE(fileLength);
    pos += 4;
    // file.setLittleEndianMode(true);
    file.writeIntLE(Shapefile.VERSION);
    pos += 4;
    file.writeIntLE(shapeType);
    pos += 4;
    // write the bounding box
    file.writeDoubleLE(bounds.getMinX());
    file.writeDoubleLE(bounds.getMinY());
    file.writeDoubleLE(bounds.getMaxX());
    file.writeDoubleLE(bounds.getMaxY());
    pos += 8 * 4;

    // skip remaining unused bytes
    for (int i = 0; i < 4; i++) {
      file.writeDoubleLE(0.0);// Skip unused part of header
      pos += 8;
    }

    if (DEBUG)
      System.out.println("Sfh->Position " + pos);
  }

  public static byte[] write(
      int fileLength, int shapeType, Envelope bounds) throws IOException
    {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      EndianDataOutputStream edos = new EndianDataOutputStream(bos);
      write(edos, fileLength, shapeType, bounds);
      return bos.toByteArray();
    }
  
  public static byte[] writeToIndex(
      int numShapes, int shapeType, Envelope bounds) throws IOException
    {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      EndianDataOutputStream edos = new EndianDataOutputStream(bos);
      writeToIndex(edos, numShapes, shapeType, bounds);
      return bos.toByteArray();
    }
  
  public void writeToIndex(EndianDataOutputStream file) throws IOException {
    int pos = 0;
    // file.setLittleEndianMode(false);
    file.writeIntBE(fileCode);
    pos += 4;
    for (int i = 0; i < 5; i++) {
      file.writeIntBE(0);// Skip unused part of header
      pos += 4;
    }
    file.writeIntBE(indexLength);
    pos += 4;
    // file.setLittleEndianMode(true);
    file.writeIntLE(version);
    pos += 4;
    file.writeIntLE(shapeType);
    pos += 4;
    // write the bounding box
    pos += 8;
    file.writeDoubleLE(bounds.getMinX());
    pos += 8;
    file.writeDoubleLE(bounds.getMinY());
    pos += 8;
    file.writeDoubleLE(bounds.getMaxX());
    pos += 8;
    file.writeDoubleLE(bounds.getMaxY());
    /*
     * for(int i = 0;i<4;i++){ pos+=8; file.writeDouble(bounds[i]); }
     */

    // skip remaining unused bytes
    // file.setLittleEndianMode(false);//well they may not be unused forever...
    for (int i = 0; i < 4; i++) {
      file.writeDoubleLE(0.0);// Skip unused part of header
      pos += 8;
    }
    if (DEBUG)
      System.out.println("Sfh->Index Position " + pos);
  }

  public static void writeToIndex(EndianDataOutputStream file,
      int numShapes, int shapeType, Envelope bounds) throws IOException {
    int pos = 0;
    // file.setLittleEndianMode(false);
    file.writeIntBE(Shapefile.SHAPEFILE_ID);
    pos += 4;
    for (int i = 0; i < 5; i++) {
      file.writeIntBE(0);// Skip unused part of header
      pos += 4;
    }
    int indexLength = 50 + (4 * numShapes);
    file.writeIntBE(indexLength);
    pos += 4;
    // file.setLittleEndianMode(true);
    file.writeIntLE(Shapefile.VERSION);
    pos += 4;
    file.writeIntLE(shapeType);
    pos += 4;
    // write the bounding box
    pos += 8;
    file.writeDoubleLE(bounds.getMinX());
    pos += 8;
    file.writeDoubleLE(bounds.getMinY());
    pos += 8;
    file.writeDoubleLE(bounds.getMaxX());
    pos += 8;
    file.writeDoubleLE(bounds.getMaxY());
    /*
     * for(int i = 0;i<4;i++){ pos+=8; file.writeDouble(bounds[i]); }
     */

    // skip remaining unused bytes
    // file.setLittleEndianMode(false);//well they may not be unused forever...
    for (int i = 0; i < 4; i++) {
      file.writeDoubleLE(0.0);// Skip unused part of header
      pos += 8;
    }
    if (DEBUG)
      System.out.println("Sfh->Index Position " + pos);
  }

  public int getShapeType() {
    return shapeType;
  }

  public int getVersion() {
    return version;
  }

  public Envelope getBounds() {
    return bounds;
  }

  public String toString() {
    String res = new String("Sf-->type " + fileCode + " size " + fileLength
        + " version " + version + " Shape Type " + shapeType);
    return res;
  }
}

package org.geotools.shapefile;
import jeql.io.EndianDataInputStream;
import jeql.io.EndianDataOutputStream;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public interface ShapeHandler {
    public int getShapeType();
    public Geometry read(EndianDataInputStream file,GeometryFactory geometryFactory,int contentLength) throws java.io.IOException,InvalidShapefileException;
    public void write(Geometry geometry,EndianDataOutputStream file) throws java.io.IOException;
    public int getLength(Geometry geometry); //length in 16bit words
}

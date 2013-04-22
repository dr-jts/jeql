package org.geotools.shapefile;

import java.io.IOException;

import jeql.io.EndianDataInputStream;
import jeql.io.EndianDataOutputStream;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Wrapper for a Shapefile point.
 */
public class PointHandler implements ShapeHandler{
    
    int Ncoords=2; //2 = x,y ;  3= x,y,m ; 4 = x,y,z,m
    int myShapeType = -1;
    
    public PointHandler(int type) throws InvalidShapefileException
    {
        if ( (type != 1) && (type != 11) && (type != 21))// 2d, 2d+m, 3d+m
            throw new InvalidShapefileException("PointHandler constructor: expected a type of 1, 11 or 21");
        myShapeType = type;
    }
    
    public PointHandler()
    {
        myShapeType = 1; //2d
    }
    
    public Geometry read(EndianDataInputStream file,GeometryFactory geometryFactory,int contentLength) throws IOException,InvalidShapefileException
    {
      //  file.setLittleEndianMode(true);
	int actualReadWords = 0; //actual number of words read (word = 16bits)
	
        int shapeType = file.readIntLE();
		actualReadWords += 2;
       
        if (shapeType != myShapeType)
            throw new InvalidShapefileException("pointhandler.read() - handler's shapetype doesnt match file's");
        
        double x = file.readDoubleLE();
        double y = file.readDoubleLE();
        double m , z = Double.NaN;
		actualReadWords += 8;
        
        if ( shapeType ==11 )
        {
            z= file.readDoubleLE();
			actualReadWords += 4;
        }
        if ( shapeType >=11 )
        {
            m = file.readDoubleLE();
			actualReadWords += 4;
        }
        
	//verify that we have read everything we need
	while (actualReadWords < contentLength)
	{
		  int junk2 = file.readShortBE();	
		  actualReadWords += 1;
	}
        
        return geometryFactory.createPoint(new Coordinate(x,y,z));
    }
    
    public void write(Geometry geometry,EndianDataOutputStream file)throws IOException{
       // file.setLittleEndianMode(true);
        file.writeIntLE(getShapeType());
        Coordinate c = geometry.getCoordinates()[0];
        file.writeDoubleLE(c.x);
        file.writeDoubleLE(c.y);
       
        if  (myShapeType ==11) 
        {
             if (Double.isNaN(c.z)) // nan means not defined
                 file.writeDoubleLE(0.0);
             else
                 file.writeDoubleLE(c.z); 
        }
        if ( (myShapeType ==11) || (myShapeType ==21) )
        {
             file.writeDoubleLE(-10E40); //M
        }
    }
    
    /**
     * Returns the shapefile shape type value for a point
     * @return int Shapefile.POINT
     */
    public  int getShapeType(){  
        return myShapeType;
    }
    
    /**
     * Calcuates the record length of this object.
     * @return int The length of the record that this shapepoint will take up in a shapefile
     **/
    public int getLength(Geometry geometry){
        if (myShapeType == Shapefile.POINT)
            return 10;
        if (myShapeType == Shapefile.POINTM)
            return 14;
        
        return 18;
    }
}

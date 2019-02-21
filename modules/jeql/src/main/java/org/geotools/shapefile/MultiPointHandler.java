/*
 * MultiPointHandler.java
 *
 * Created on July 17, 2002, 4:13 PM
 */

package org.geotools.shapefile;

import java.io.IOException;

import jeql.io.EndianDataInputStream;
import jeql.io.EndianDataOutputStream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.PrecisionModel;


/**
 *
 * @author  dblasby
 */
public class MultiPointHandler  implements ShapeHandler  {
    int myShapeType= -1;
    
    /** Creates new MultiPointHandler */
    public MultiPointHandler() {
        myShapeType = 8;
    }
    
        public MultiPointHandler(int type) throws InvalidShapefileException
        {
            if  ( (type != 8) &&  (type != 18) &&  (type != 28) )
                throw new InvalidShapefileException("Multipointhandler constructor - expected type to be 8, 18, or 28");
            
        myShapeType = type;
    }
    
    public Geometry read(EndianDataInputStream file,GeometryFactory geometryFactory,int contentLength) throws IOException,InvalidShapefileException{
        //file.setLittleEndianMode(true);
	
		int actualReadWords = 0; //actual number of words read (word = 16bits)
	
        int shapeType = file.readIntLE();  
		actualReadWords += 2;
        
        if (shapeType ==0)
            return  new MultiPoint(null,new PrecisionModel(),0);
        if (shapeType != myShapeType)
        {
            throw new InvalidShapefileException("Multipointhandler.read() - expected type code "+myShapeType+" but got "+shapeType);
        }
        //read bbox
        file.readDoubleLE();
        file.readDoubleLE();
        file.readDoubleLE();
        file.readDoubleLE();
        
		actualReadWords += 4*4;
        
        int numpoints = file.readIntLE(); 
		actualReadWords += 2;
	 
        Coordinate[] coords = new Coordinate[numpoints];
        for (int t=0;t<numpoints;t++)
        {    
          
                double x = file.readDoubleLE();
                double y = file.readDoubleLE();
				actualReadWords += 8;
                coords[t] = new Coordinate(x,y);
        }
        if (myShapeType == 18)
        {
            file.readDoubleLE(); //z min/max
            file.readDoubleLE();
			actualReadWords += 8;
            for (int t=0;t<numpoints;t++)
            { 
                       double z =  file.readDoubleLE();//z
						actualReadWords += 4;
                       coords[t].z = z;
            }
        }
        
        
        if (myShapeType >= 18)
        {
           // int fullLength = numpoints * 8 + 20 +8 +4*numpoints + 8 +4*numpoints;
			int fullLength;
			if (myShapeType == 18)
			{
				//multipoint Z (with m)
				fullLength = 20 + (numpoints * 8)  +8 +4*numpoints + 8 +4*numpoints;
			}
			else
			{
				//multipoint M (with M)
				fullLength = 20 + (numpoints * 8)  +8 +4*numpoints;
			}
			
            if (contentLength >= fullLength)  //is the M portion actually there?
            {
                file.readDoubleLE(); //m min/max
                file.readDoubleLE();
				actualReadWords += 8;
                for (int t=0;t<numpoints;t++)
                { 
                            file.readDoubleLE();//m
							actualReadWords += 4;
                }
            }
        }
        
	//verify that we have read everything we need
	while (actualReadWords < contentLength)
	{
		  int junk2 = file.readShortBE();	
		  actualReadWords += 1;
	}
	
        return geometryFactory.createMultiPoint(coords);
    }
    
    double[] zMinMax(Geometry g)
    {
        double zmin,zmax;
        boolean validZFound = false;
        Coordinate[] cs = g.getCoordinates();
        double[] result = new double[2];
        
        zmin = Double.NaN;
        zmax = Double.NaN;
        double z;
        
        for (int t=0;t<cs.length; t++)
        {
            z= cs[t].z ;
            if (!(Double.isNaN( z ) ))
            {
                if (validZFound)
                {
                    if (z < zmin)
                        zmin = z;
                    if (z > zmax)
                        zmax = z;
                }
                else
                {
                    validZFound = true;
                    zmin =  z ;
                    zmax =  z ;
                }
            }
           
        }
        
        result[0] = (zmin);
        result[1] = (zmax);
        return result;
        
    }
    
    
    public void write(Geometry geometry,EndianDataOutputStream file)throws IOException{

        MultiPoint mp = (MultiPoint) geometry;
        
        //file.setLittleEndianMode(true);
        
        file.writeIntLE(getShapeType());
        
        Envelope box = mp.getEnvelopeInternal();
        file.writeDoubleLE(box.getMinX());
        file.writeDoubleLE(box.getMinY());
        file.writeDoubleLE(box.getMaxX());
        file.writeDoubleLE(box.getMaxY());
        
        int numParts = mp.getNumGeometries();
        file.writeIntLE(numParts);
        
        
        for (int t=0;t<mp.getNumGeometries(); t++)
        {
            Coordinate c = (mp.getGeometryN(t)).getCoordinate();
            file.writeDoubleLE(c.x);
            file.writeDoubleLE(c.y);            
        }
        if (myShapeType == 18)
        {
            double[] zExtreame = zMinMax(mp);
            if (Double.isNaN(zExtreame[0] ))
            {
                file.writeDoubleLE(0.0);
                file.writeDoubleLE(0.0);
            }
            else
            {
                file.writeDoubleLE(zExtreame[0]);
                file.writeDoubleLE(zExtreame[1]);
            }
            for (int t=0;t<mp.getNumGeometries(); t++)
            {
                Coordinate c = (mp.getGeometryN(t)).getCoordinate();
                double z = c.z;
                if (Double.isNaN(z))
                     file.writeDoubleLE(0.0);
                else
                     file.writeDoubleLE(z);
            }
        }
        if (myShapeType >= 18)
        {
                file.writeDoubleLE(-10E40);
                file.writeDoubleLE(-10E40);
                 for (int t=0;t<mp.getNumGeometries(); t++)
                 {   
                     file.writeDoubleLE(-10E40);
                 }
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
        MultiPoint mp = (MultiPoint) geometry;
    
        if (myShapeType == 8)
            return mp.getNumGeometries() * 8 + 20;
        if (myShapeType == 28)
            return mp.getNumGeometries() * 8 + 20 +8 +4*mp.getNumGeometries();
        
        return mp.getNumGeometries() * 8 + 20 +8 +4*mp.getNumGeometries() + 8 +4*mp.getNumGeometries() ;
    }
}

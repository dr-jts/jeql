package ca.bc.coordsys;


import org.locationtech.jts.util.Assert;

/** 
 * This class represents a coordinate system.
 */
public class CoordinateSystem implements Comparable {
    private Projection projection;
    private String name;
    private int epsgCode;
    
	public static final CoordinateSystem UNSPECIFIED = new CoordinateSystem("Unspecified",
	    0, null) {
        private static final long serialVersionUID = -811718450919581831L;
	    public Projection getProjection() {
	        throw new UnsupportedOperationException();
	    }
	
	    public int getEPSGCode() {
	        throw new UnsupportedOperationException();
	    }
	};
    
    public CoordinateSystem(String name, int epsgCode, Projection projection) {
        this.name = name;
        this.projection = projection;
        this.epsgCode = epsgCode;
    }
    
    public String toString() {
        return name;
    }
    public String getName() {
        return name;
    }

    public Projection getProjection() {
        return projection;
    }

    public int getEPSGCode() {
        return epsgCode;
    }

	public int compareTo(Object o) {
        Assert.isTrue(o instanceof CoordinateSystem);
        if (this == o) { return 0; }
        if (this == UNSPECIFIED) { return -1; }
        if (o == UNSPECIFIED) { return 1; }
		return toString().compareTo(o.toString());
	}

}

package ca.bc.coordsys.impl;

import ca.bc.coordsys.Geographic;
import ca.bc.coordsys.Planar;
import ca.bc.coordsys.Projection;

/**
 * Implements the Geographic projection.
 * *
 */
public class LatLong extends Projection {
    public Geographic asGeographic(Planar p, Geographic g) {
        g.lon = p.x;
        g.lat = p.y;
        return g;
    }
    public Planar asPlanar(Geographic g, Planar p) {
        p.x = g.lon;
        p.y = g.lat;
        return p;        
    }
}

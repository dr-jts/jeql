package ca.bc.coordsys;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;


/**
 * The source and destination coordinate reference systems must have
 * the same datum (for example, WGS 84).
 */
public class Reprojector {
    private static Reprojector instance = new Reprojector();

    private Reprojector() {
    }

    public static Reprojector instance() {
        return instance;
    }

    public boolean wouldChangeValues(CoordinateSystem source,
        CoordinateSystem destination) {
        if (source == CoordinateSystem.UNSPECIFIED) {
            return false;
        }

        if (destination == CoordinateSystem.UNSPECIFIED) {
            return false;
        }

        if (source == destination) {
            return false;
        }

        return true;
    }

    public void reproject(Coordinate coordinate, CoordinateSystem source,
        CoordinateSystem destination) {
        if (!wouldChangeValues(source, destination)) {
            return;
        }

        Planar result = destination.getProjection().asPlanar(source.getProjection()
                                                                   .asGeographic(new Planar(
                        coordinate.x, coordinate.y), new Geographic()),
                new Planar());
        coordinate.x = result.x;
        coordinate.y = result.y;
    }

    public void reproject(Geometry geometry, final CoordinateSystem source,
        final CoordinateSystem destination) {
        if (!wouldChangeValues(source, destination)) {
            return;
        }

        geometry.apply(new CoordinateFilter() {
                public void filter(Coordinate coord) {
                    reproject(coord, source, destination);
                }
            });
        geometry.setSRID(destination.getEPSGCode());
        geometry.geometryChanged();
    }
}

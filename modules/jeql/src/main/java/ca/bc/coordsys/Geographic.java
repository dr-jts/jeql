package ca.bc.coordsys;

public class Geographic {

  public double lat, lon, hgt;

  public Geographic() {
    lat = 0;
    lon = 0;
    hgt = 0;
  }

  public Geographic(double _lat, double _lon) {
    lat = _lat;
    lon = _lon;
    hgt = 0;
  }

  public String toString() {
    return lat + ", " + lon;
  }
}

package server.types;

import java.util.List;

// Geometry class representing the value corresponding to the "geometry" key in the GeoJSON data
public class Geometry {
  private String type;
  private List<List<List<List<String>>>> coordinates;

  // returns the coordinates field
  public List<List<List<List<String>>>> getCoordinates() {
    return this.coordinates;
  }
}

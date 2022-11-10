package server.types;

import java.util.HashMap;
import java.util.Map;

// Feature class representing the value corresponding to the "features" key in the GeoJSON data
public class Feature {
  private String type;
  private Geometry geometry;
  private Map properties;

  // returns the geometry field
  public Geometry getGeometry() {
    return this.geometry;
  }
}

package server.types;

import java.util.Map;

// Feature class representing the value corresponding to the "features" key in the GeoJSON data
public class Feature {
  private final String type;
  private final Geometry geometry;
  private final Map properties;

  public Feature(String type, Geometry geometry, Map properties) {
    this.type = type;
    this.geometry = geometry;
    this.properties = properties;
  }

  public String getType() {
    return this.type;
  }

  public Map getProperties() {
    return this.properties;
  }

  // returns the geometry field
  public Geometry getGeometry() {
    return this.geometry;
  }
}

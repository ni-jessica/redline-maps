package server.types;

import java.util.HashMap;
import java.util.Map;

public class Feature {
  private String type;
  private Geometry geometry;
  private Map properties;


  public Geometry getGeometry() {
    return this.geometry;
  }
}

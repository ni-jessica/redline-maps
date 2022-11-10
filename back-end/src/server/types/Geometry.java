package server.types;

import java.util.List;

public class Geometry {
  private String type;
  private List<List<List<List<String>>>> coordinates;

  public List<List<List<List<String>>>> getCoordinates() {
    return this.coordinates;
  }
}

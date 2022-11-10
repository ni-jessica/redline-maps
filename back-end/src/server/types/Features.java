package server.types;

import java.util.List;

// Features class representing the key label corresponding to the "features" key in the GeoJSON data
public class Features {
  private List<Feature> features;

  // returns the features field
  public List<Feature> getFeatures() {
    return this.features;
  };
}

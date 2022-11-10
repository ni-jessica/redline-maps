package server.handlers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.ServerUtilities;
import server.types.Geometry;
import spark.Request;
import spark.Response;
import spark.Route;
import server.errors.BadDatasourceError;
import server.errors.BadJsonError;
import server.errors.BadRequestError;
import server.types.Feature;

public class FilterHandler implements Route{

  private Double lonMin;
  private Double latMin;
  private Double latMax;
  private Double lonMax;
  private final String dataPath;

  /**
   * constructor for WeatherHandler
   */
  public FilterHandler(String dataPath) {
    this.dataPath = dataPath;
  }

  /**
   * Get the temperature information from the National Weather API
   *
   * @param request  the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String latMin = request.queryParams("latMin");
    String latMax = request.queryParams("latMax");
    String lonMin = request.queryParams("lonMin");
    String lonMax = request.queryParams("lonMax");

    // checking if fields are in correct coordinate format
    try {
      this.latMin = Double.parseDouble(latMin);
      this.latMax = Double.parseDouble(latMax);
      this.lonMin = Double.parseDouble(lonMin);
      this.lonMax = Double.parseDouble(lonMax);
    } catch (Exception e) {
      return new BadJsonError().serialize();
    }

    // checking if fields are present
    if (this.latMin == null || this.latMax == null || this.lonMin == null || this.lonMax == null ) {
      return new BadRequestError().serialize();
    }

    try {
      List<Feature> featureList = ServerUtilities.deserializeFeatures(this.dataPath).getFeatures();
      List<Feature> filteredFeatureList = this.filter(featureList);

//      Map<String, Object> output = new HashMap<>();
//      output.put("type", "FeatureCollection");
//      output.put("features", filteredFeatureList);

      return new ServerUtilities.FeaturesSuccessResponse(filteredFeatureList).serialize();

    } catch (Exception e) {
      System.out.println(e);
      return new BadDatasourceError().serialize();
    }
  }

  public List<Feature> filter(List<Feature> featureList) {
    List<Feature> filteredFeatureList = new LinkedList<>();
    for (Feature feature : featureList) {
      Geometry geometry = feature.getGeometry();
      if (geometry == null) {
        continue;
      }
      // TODO: check for nullness

      List<List<String>> coordinateList = geometry.getCoordinates().get(0).get(0);

      for (List<String> pair : coordinateList) {
        // TODO: verify these values are something
        Double lat = Double.parseDouble(pair.get(0));
        Double lon = Double.parseDouble(pair.get(1));

        if (this.latMax >= lat && this.latMin <= lat && this.lonMax >= lon && this.lonMin <= lon) {
          filteredFeatureList.add(feature);
        }
      }
    }
    return filteredFeatureList;
  }
}


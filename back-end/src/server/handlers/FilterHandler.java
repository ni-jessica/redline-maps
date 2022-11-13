package server.handlers;

import java.util.LinkedList;
import java.util.List;
import server.ServerUtilities;
import server.types.Geometry;
import spark.Request;
import spark.Response;
import spark.Route;
import server.errors.BadDatasourceError;
import server.errors.BadJsonError;
import server.errors.BadRequestError;
import server.types.Feature;

// handles the filter API endpoint
public class FilterHandler implements Route{

  private Double lonMin;
  private Double latMin;
  private Double latMax;
  private Double lonMax;
  private final String dataPath;

  /**
   * Constructor for FilterHandler
   */
  public FilterHandler(String dataPath) {
    this.dataPath = dataPath;
  }

  /**
   * Constructor for FilterHandler for testing
   */
  public FilterHandler(String dataPath, Double latMin, Double latMax, Double lonMin, Double lonMax) {
    this.dataPath = dataPath;
    this.latMin = latMin;
    this.latMax = latMax;
    this.lonMin = lonMin;
    this.lonMax = lonMax;
  }

  /**
   * Gets the filtered red line information from the GeoJSON data or displays appropriate error
   *
   * @param request the request to handle
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

      return new ServerUtilities.FeaturesSuccessResponse(filteredFeatureList).serialize();

    } catch (Exception e) {
      System.out.println(e);
      return new BadDatasourceError().serialize();
    }
  }

  /**
   * filters the data's list of Feature to only have the ones whose coordinates are contained
   * within the provided bounds
   * @param featureList the data's List of Feature
   * @return the filtered featureList
   */
  public List<Feature> filter(List<Feature> featureList) {
    List<Feature> filteredFeatureList = new LinkedList<>();
    for (Feature feature : featureList) {
      Geometry geometry = feature.getGeometry();
      // skip this feature if it doesn't have geometry (and therefore doesn't have coordinates)
      if (geometry == null) {
        continue;
      }

      List<List<String>> coordinateList = geometry.getCoordinates().get(0).get(0);

      Integer validCoordinates = 0;
      for (List<String> pair : coordinateList) {
        // empty coordinate list
        if (pair.isEmpty()) {
          continue;
        }
        Double lat = Double.parseDouble(pair.get(0));
        Double lon = Double.parseDouble(pair.get(1));

        if (this.latMax >= lat && this.latMin <= lat && this.lonMax >= lon && this.lonMin <= lon) {
          validCoordinates += 1;
          // continue checking coordinates if this is not the last in the list
          if (!validCoordinates.equals(coordinateList.size())) {
            continue;
          } else {
            filteredFeatureList.add(new Feature(
                    feature.getType(),
                    feature.getGeometry(),
                    feature.getProperties())); // if all coordinates were valid, add feature to the list
          }
        } else {
          break;
        }
      }
    }
    return filteredFeatureList;
  }
}


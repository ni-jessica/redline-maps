package server.handlers;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.ServerUtilities;
import spark.Request;
import spark.Response;
import spark.Route;
import server.errors.BadDatasourceError;
import server.errors.BadJsonError;
import server.errors.BadRequestError;
import data.*;
import server.types.Feature;
import server.types.Features;
import server.types.Geometry;

public class FilterHandler implements Route{

  private Double lonMin;
  private Double latMin;
  private Double latMax;
  private Double lonMax;
  private static Moshi moshi;
  private JsonReader reader;

  /**
   * constructor for WeatherHandler
   */
  public FilterHandler(JsonReader reader) {
    moshi = new Moshi.Builder().build();
    this.reader = reader;
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

    // checking if fields are present
    if (this.latMin == null || this.latMax == null || this.lonMin == null || this.lonMax == null ) {
      return new BadRequestError().serialize();
    }

    // checking if fields are in correct coordinate format
    try {
      this.latMin = Double.parseDouble(latMin);
      this.latMax = Double.parseDouble(latMax);
      this.lonMin = Double.parseDouble(lonMin);
      this.lonMax = Double.parseDouble(lonMax);
    } catch (Exception e) {
      return new BadJsonError().serialize();
    }

    try {
      List<Feature> featureList = ServerUtilities.deserializeFeatures(this.reader).getFeatures();

      List<Feature> filteredFeatureList = this.filter(featureList);

      Map<String, Object> output = new HashMap<>();
      output.put("type", "FeatureCollection");
      output.put("features", filteredFeatureList);

      return ServerUtilities.serialize(output);

    } catch (Exception e) {
      return new BadDatasourceError().serialize();
    }
  }

  public List<Feature> filter(List<Feature> featureList) {
    List<Feature> filteredFeatureList = new LinkedList<>();
    for (Feature feature : featureList) {
      List<List<List<List<String>>>> coordinates = feature.getGeometry().getCoordinates();

      List<List<String>> coordinateList = coordinates.get(0).get(0);

      // TODO: check for nullness

      for (List<String> pair : coordinateList) {
        // TODO: verify these values are something
        Double lat = Double.parseDouble(pair.get(0));
        Double lon = Double.parseDouble(pair.get(1));

        if (this.latMax >= lat && this.latMin <= lat && this.lonMax <= lon && this.lonMin <= lon) {
          filteredFeatureList.add(feature);
        }
      }
    }
    return filteredFeatureList;
  }
}


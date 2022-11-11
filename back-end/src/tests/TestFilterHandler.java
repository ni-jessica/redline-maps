package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.ServerUtilities;
import server.errors.BadDatasourceError;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.FilterHandler;
import server.types.Feature;
import server.types.Geometry;
import spark.Spark;

public class TestFilterHandler {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() {
    Spark.get("filter", new FilterHandler("src/data/fullDownload.json"));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/filter");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   * @param apiCall the call string, including endpoint
   *                (NOTE: this would be better if it had more structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  @Test
  /* testing weather connection */
  public void testAPINoFilter() throws IOException {
    HttpURLConnection clientConnection = tryRequest("filter");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    BadDatasourceError response =
        moshi.adapter(BadDatasourceError.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    clientConnection.disconnect();
  }

  @Test
  public void testValidCoords() throws IOException {
    HttpURLConnection clientConnection = tryRequest("filter?latMin=-90&latMax=90&lonMin=-180&lonMax=180");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    ServerUtilities.FeaturesSuccessResponse response =
        moshi.adapter(ServerUtilities.FeaturesSuccessResponse.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assert response != null;
    assert response.features().size() != 0;
    clientConnection.disconnect();
  }

  @Test
  public void testFilterEmpty() throws IOException {
    FilterHandler filterEmpty = new FilterHandler("../data/mockDataEmpty.json", 0.0, 0.0, 0.0, 0.0);

    List<List<List<List<String>>>> coordinates = new ArrayList<>();
    coordinates.add(new ArrayList<>());
    coordinates.get(0).add(new ArrayList<>());
    coordinates.get(0).get(0).add(new ArrayList<>());

    Map properties = new HashMap();

    Feature feature = new Feature("Feature", new Geometry("MultiPolygon", coordinates), properties);
    List<Feature> mockListFeature = new ArrayList(List.of(feature));
    assertEquals(new LinkedList<>(), filterEmpty.filter(mockListFeature));
  }


}

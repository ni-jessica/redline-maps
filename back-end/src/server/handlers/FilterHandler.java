package server.handlers;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import spark.Request;
import spark.Response;
import spark.Route;
import server.errors.BadDatasourceError;
import server.errors.BadJsonError;
import server.errors.BadRequestError;

public class FilterHandler implements Route{

  private String latMin;
  private String latMax;
  private String lonMin;
  private String lonMax;
  private static Moshi moshi;

  /**
   * constructor for WeatherHandler
   */
  public FilterHandler() {
    moshi = new Moshi.Builder().build();
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
    this.latMin = request.queryParams("latMin");
    this.latMax = request.queryParams("latMax");
    this.lonMin = request.queryParams("lonMin");
    this.lonMax = request.queryParams("lonMax");

    // checking if fields are present
    if (this.latMin == null || this.latMax == null || this.lonMin == null || this.lonMax == null ) {
      return new BadRequestError().serialize();
    }

    // checking if fields are in correct coordinate format
    try {
      Float checkValidLat = Float.parseFloat(this.lat);
      Float checkValidLon = Float.parseFloat(this.lon);
    } catch (Exception e) {
      return new BadJsonError().serialize();
    }

    try {
      // get info for forecast endpoint
      /* for a different external APi, replace the URI with the API link of your choice and update
      the fields accordingly, ie. for the NWS API it requires latitude and longitude fields */
      HttpRequest weatherRequest = HttpRequest.newBuilder()
          .uri(new URI("https://api.weather.gov/points/" + this.lat + "," + this.lon))
          .GET().
          build();
      HttpResponse<String> weatherResponse = HttpClient.newBuilder().build()
          .send(weatherRequest, BodyHandlers.ofString());
      String forecastURI = this.deserializeWeather(weatherResponse).properties.forecast;

      // make request for forecast
      HttpRequest forecastRequest = HttpRequest.newBuilder()
          .uri(new URI(forecastURI))
          .GET().
          build();
      HttpResponse<String> forecastResponse = HttpClient.newBuilder().build()
          .send(forecastRequest, BodyHandlers.ofString());

      // send forecast response to success response
      Period forecast = this.deserializeForecast(forecastResponse).properties.periods.get(0);
      return new WeatherSuccessResponse(
          this.lat + ", " + this.lon,
          forecast.temperature.toString() + forecast.temperatureUnit
      ).serialize();
    } catch (Exception e) {
      return new BadDatasourceError().serialize();
    }
  }

  /**
   * Deserializes the response received from the weather URI
   *
   * @param weatherResponse the response to deserialize
   * @return response content, deserialized from Json
   * @throws IOException
   */
  Weather deserializeWeather(HttpResponse<String> weatherResponse) throws IOException {
    try {
      return moshi.adapter(Weather.class).fromJson(weatherResponse.body());
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Deserializes the response received from the forecast URI
   *
   * @param forecastResponse the response to deserialize
   * @return response content, deserialized from Json
   * @throws IOException
   */
  Forecast deserializeForecast(HttpResponse<String> forecastResponse) throws IOException {
    try {
      return moshi.adapter(Forecast.class).fromJson(forecastResponse.body());
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Response object to send, containing the temperature of the input location
   */
  public record WeatherSuccessResponse(String result, String coordinates, String temperature) {

    public WeatherSuccessResponse(String coordinates, String temperature) {
      this("success", coordinates, temperature);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        return moshi.adapter(WeatherSuccessResponse.class).indent("   ").toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}


package server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import server.types.Feature;
import server.types.Features;

public class ServerUtilities {

  private static final Moshi moshi = new Moshi.Builder().build();
  private static final JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(
      Types.newParameterizedType(Map.class, String.class, Object.class));

//  public static String serialize(Map<String, Object> map) {
//    return jsonAdapter.toJson(map);
//  }

  public record FeaturesSuccessResponse(String type, List<Feature> features) {

    public FeaturesSuccessResponse(List<Feature> features) {
      this("FeatureCollection", features);
    }

    /**
     * @return this response, serialized as Json
     */
    public String serialize() {
      try {
        return moshi.adapter(FeaturesSuccessResponse.class).toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Deserializes the response received from the weather URI
   *
   * @return response content, deserialized from Json
   * @throws IOException
   */
  public static Features deserializeFeatures(String file) throws IOException {
      String dataPath = new String(Files.readAllBytes(Paths.get(file)));
      return moshi.adapter(Features.class).fromJson(dataPath);
  }
}

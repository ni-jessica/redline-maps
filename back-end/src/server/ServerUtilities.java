package server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.Map;
import server.types.Features;

public class ServerUtilities {

  private static final Moshi moshi = new Moshi.Builder().build();
  private static final JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(
      Types.newParameterizedType(Map.class, String.class, Object.class));

  /**
   * Serializes a Map into a two-dimensional JSON array with type String
   *
   * @param map a map with String keys and Object values
   * @return a two-dimensional JSON array
   */
  public static String serialize(Map<String, Object> map) {
    return jsonAdapter.toJson(map);
  }

  /**
   * Deserializes the response received from the weather URI
   *
   * @return response content, deserialized from Json
   * @throws IOException
   */
  public static Features deserializeFeatures(JsonReader reader) throws IOException {
      return moshi.adapter(Features.class).fromJson(reader);
  }
}

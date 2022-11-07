package server.errors;

import com.squareup.moshi.Moshi;

/* throws an error for invalid JSON requests */
public class BadJsonError {
  String error;

  public BadJsonError() {
    this.error = "error_bad_json";
  }

  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(BadJsonError.class).toJson(this);
  }


}

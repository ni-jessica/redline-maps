package server.errors;

import com.squareup.moshi.Moshi;

/* throws an error for requests that cannot be fulfilled */
public class BadRequestError {
  String error;

  public BadRequestError() {
    this.error = "error_bad_request";
  }

  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(BadRequestError.class).toJson(this);
  }


}

package server.errors;

import com.squareup.moshi.Moshi;

/* throws an error for when a resource cannot be accesses/does not exist */
public class BadDatasourceError {
  String error;

  public BadDatasourceError() {
    this.error = "error_datasource";
  }

  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(BadDatasourceError.class).toJson(this);
  }

}
package server;

import static spark.Spark.after;

import spark.Spark;
import server.handlers.FilterHandler;
import server.errors.BadJsonError;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various handlers.
 */
public class Server {
  public static void main(String[] args) {
    Spark.port(3232);
    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "GET");
    });

    //http://localhost:3232/filter?latMin=-80.7456&latMax=-78.7456&lonMin=32.0892&lonMax=37.0892
    /* setup endpoints */
    String dataPath = "src/data/fullDownload.json";
    Spark.get("filter", new FilterHandler(dataPath));
    Spark.get("*", (request, response) -> new BadJsonError().serialize());
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}


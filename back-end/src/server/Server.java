package server;

import static spark.Spark.after;

import java.util.HashMap;
import java.util.Map;
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

    /* setup endpoints */
    Spark.get("filter", new FilterHandler());
    Spark.get("*", (request, response) -> new BadJsonError().serialize());
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}


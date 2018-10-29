package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Saying;
import play.libs.Json;
import play.mvc.*;

public abstract class AbstractSayingController extends Controller {

  static final String CONTENT_TYPE = "application/hal+json";

  static final JsonNode LINKS_RESPONSE = Json.parse("{\n" +
      "  \"_links\": {\n" +
      "    \"self\": { \"href\": \"/sayings\" },\n" +
      "    \"random\": { \"href\": \"/sayings/random\" }\n" +
      "  }\n" +
      "}");

  private static final JsonNode RANDOM_URI_JSON = Json.parse("{ \"href\": \"/sayings/random\" }");

  public abstract Object links();

  public abstract Object get(String id);

  public abstract Object getRandom();

  @BodyParser.Of(BodyParser.Json.class)
  public abstract Object rate(String id);

  JsonNode prepareSayingResponse(Saying saying) {
    String id = saying.getId().toString();
    String selfUri = String.format("{ \"href\": \"/sayings/%s\" }", id);
    String rateUri = String.format("{ \"href\": \"/sayings/%s/rate\" }", id);

    ObjectNode links = Json.newObject();
    links.set("self", Json.parse(selfUri));
    links.set("rate", Json.parse(rateUri));
    links.set("random", RANDOM_URI_JSON);

    ObjectNode result = Json.newObject();
    result.set("saying", Json.toJson(saying));
    result.set("_links", links);
    return result;
  }
}

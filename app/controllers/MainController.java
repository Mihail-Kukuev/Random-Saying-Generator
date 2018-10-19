package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.UUID;
import javax.inject.Inject;
import db.SayingRepository;
import models.Saying;
import play.libs.Json;
import play.mvc.*;


public class MainController extends Controller {

  private static final String CONTENT_TYPE = "application/hal+json";

  private static final JsonNode DEFAULT_RESPONSE = Json.parse("{\n" +
      "  \"_links\": {\n" +
      "    \"self\": { \"href\": \"/sayings\" },\n" +
      "    \"random\": { \"href\": \"/sayings/random\" }\n" +
      "  }\n" +
      "}");

  private static final JsonNode RANDOM_URI_JSON = Json.parse("{ \"href\": \"/sayings/random\" }");

  private SayingRepository sayingRepository;

  @Inject
  public MainController(SayingRepository sayingRepository) {
    this.sayingRepository = sayingRepository;
  }

  public Result links() {
    return ok(DEFAULT_RESPONSE).as(CONTENT_TYPE);
  }

  public Result randomSaying() {
    Saying randomSaying = sayingRepository.getRandom();
    JsonNode response = prepareSayingResponse(randomSaying);
    return ok(response).as(CONTENT_TYPE);
  }

  public Result findSaying(String id) {
    Saying saying = sayingRepository.find(UUID.fromString(id));
    if (saying == null) {
      return notFound();
    }
    JsonNode response = prepareSayingResponse(saying);
    return ok(response).as(CONTENT_TYPE);
  }

  @BodyParser.Of(BodyParser.Json.class)
  public Result rateSaying(String id) {
    int rate = request().body().asJson().get("rate").intValue();
    if (rate != 1 & rate != -1) {
      return badRequest();
    }

    boolean success = sayingRepository.rate(UUID.fromString(id), rate);
    return success ? noContent() : notFound();
  }

  private JsonNode prepareSayingResponse(Saying saying) {
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

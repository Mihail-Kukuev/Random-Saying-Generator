package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;
import javax.inject.Inject;
import database.repository.SayingRepository;
import models.Saying;
import play.mvc.*;


public class SayingController extends AbstractSayingController {

  private final SayingRepository sayingRepository;

  @Inject
  public SayingController(SayingRepository sayingRepository) {
    this.sayingRepository = sayingRepository;
  }

  @Override
  public Result links() {
    return ok(LINKS_RESPONSE).as(CONTENT_TYPE);
  }

  @Override
  public Result get(String id) {
    Saying saying = sayingRepository.find(UUID.fromString(id));
    if (saying == null) {
      return notFound();
    }
    JsonNode response = prepareSayingResponse(saying);
    return ok(response).as(CONTENT_TYPE);
  }

  @Override
  public Result getRandom() {
    Saying randomSaying = sayingRepository.getRandom();
    JsonNode response = prepareSayingResponse(randomSaying);
    return ok(response).as(CONTENT_TYPE);
  }

  @Override
  @BodyParser.Of(BodyParser.Json.class)
  public Result rate(String id) {
    int rate = request().body().asJson().get("rate").intValue();
    if (rate != 1 & rate != -1) {
      return badRequest();
    }

    boolean success = sayingRepository.rate(UUID.fromString(id), rate);
    return success ? noContent() : notFound();
  }
}

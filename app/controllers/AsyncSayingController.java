package controllers;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import database.repository.AsyncSayingRepository;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;


public class AsyncSayingController extends AbstractSayingController {

  private final AsyncSayingRepository sayingRepository;
  private final HttpExecutionContext ec;

  @Inject
  public AsyncSayingController(AsyncSayingRepository sayingRepository, HttpExecutionContext ec) {
    this.sayingRepository = sayingRepository;
    this.ec = ec;
  }

  @Override
  public CompletionStage<Result> links() {
    return supplyAsync(() -> ok(LINKS_RESPONSE).as(CONTENT_TYPE), ec.current());
  }

  @Override
  public CompletionStage<Result> get(String id) {
    return sayingRepository.find(UUID.fromString(id))
        .thenApplyAsync(saying -> saying == null
            ? notFound()
            : ok(prepareSayingResponse(saying)).as(CONTENT_TYPE), ec.current());
  }

  @Override
  public CompletionStage<Result> getRandom() {
    return sayingRepository.getRandom()
        .thenApplyAsync(super::prepareSayingResponse, ec.current())
        .thenApply(response -> ok(response).as(CONTENT_TYPE));
  }

  @Override
  @BodyParser.Of(BodyParser.Json.class)
  public CompletionStage<Result> rate(String id) {
    int rate = request().body().asJson().get("rate").intValue();
    if (rate != 1 & rate != -1) {
      return supplyAsync(Results::badRequest, ec.current());
    }

    return sayingRepository.rate(UUID.fromString(id), rate)
        .thenApplyAsync(success -> success ? noContent() : notFound(), ec.current());
  }
}

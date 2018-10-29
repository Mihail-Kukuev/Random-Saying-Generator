package controllers;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import database.repository.SayingRepository;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import database.DatabaseExecutionContext;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;


public class FullyAsyncSayingController extends AbstractSayingController {

  private final SayingRepository sayingRepository;
  private final HttpExecutionContext httpExContext;
  private final DatabaseExecutionContext dbExecutor;

  @Inject
  public FullyAsyncSayingController(SayingRepository sayingRepository,
      HttpExecutionContext httpExContext, DatabaseExecutionContext dbExecutor) {
    this.sayingRepository = sayingRepository;
    this.httpExContext = httpExContext;
    this.dbExecutor = dbExecutor;
  }

  @Override
  public CompletionStage<Result> links() {
    return supplyAsync(() -> ok(LINKS_RESPONSE).as(CONTENT_TYPE), httpExContext.current());
  }

  @Override
  public CompletionStage<Result> get(String id) {
    return supplyAsync(() -> sayingRepository.find(UUID.fromString(id)), dbExecutor)
        .thenApplyAsync(saying -> saying == null
            ? notFound()
            : ok(prepareSayingResponse(saying)).as(CONTENT_TYPE), httpExContext.current());
  }

  @Override
  public CompletionStage<Result> getRandom() {
    return supplyAsync(sayingRepository::getRandom, dbExecutor)
        .thenApplyAsync(super::prepareSayingResponse, httpExContext.current())
        .thenApply(response -> ok(response).as(CONTENT_TYPE));
  }

  @Override
  @BodyParser.Of(BodyParser.Json.class)
  public CompletionStage<Result> rate(String id) {
    int rate = request().body().asJson().get("rate").intValue();
    if (rate != 1 & rate != -1) {
      return supplyAsync(Results::badRequest, httpExContext.current());
    }

    return supplyAsync(() -> sayingRepository.rate(UUID.fromString(id), rate), dbExecutor)
        .thenApplyAsync(success -> success ? noContent() : notFound(), httpExContext.current());
  }
}

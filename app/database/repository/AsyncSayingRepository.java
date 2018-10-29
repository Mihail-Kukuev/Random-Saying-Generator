package database.repository;

import com.datastax.driver.core.*;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import database.CassandraConnector;
import database.DatabaseExecutionContext;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import models.Saying;

public class AsyncSayingRepository extends AbstractSayingRepository {

  private final DatabaseExecutionContext executor;

  @Inject
  public AsyncSayingRepository(CassandraConnector connector, DatabaseExecutionContext executor) {
    super(connector);
    this.executor = executor;
  }

  @Override
  public CompletableFuture<Saying> find(UUID id) {
    return executeAsync(contentByIdStatement.bind(id)).thenApply(ResultSet::one)
        .thenCompose(contentRow -> contentRow == null
            ? CompletableFuture.completedFuture(null)
            : executeAsync(ratingByIdStatement.bind(id))
                .thenApply(ratingResultSet -> retrieveSaying(contentRow, ratingResultSet.one())));
  }

  @Override
  public CompletableFuture<Saying> getRandom() {
    return executeAsync(randomContentStatement).thenApply(ResultSet::one)
        .thenCompose(contentRow -> {
          if (contentRow == null) {
            return getRandom();
          }
          UUID id = contentRow.getUUID("id");
          return executeAsync(ratingByIdStatement.bind(id))
              .thenApply(resultSet -> retrieveSaying(contentRow, resultSet.one()));
        });
  }

  @Override
  public CompletableFuture<Boolean> rate(UUID id, int rate) {
    BoundStatement rateStatement = rate > 0 ? incLikesStatement : incDislikesStatement;
    return executeAsync(rateStatement.bind(id)).thenApply(ResultSet::wasApplied);
  }

  private CompletableFuture<ResultSet> executeAsync(BoundStatement statement) {
    return listenableToCompletable(getSession().executeAsync(statement));
  }

  private <T> CompletableFuture<T> listenableToCompletable(ListenableFuture<T> listenableFuture) {
    CompletableFuture<T> completableFuture = new CompletableFuture<T>() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = listenableFuture.cancel(mayInterruptIfRunning);
        super.cancel(cancelled);
        return cancelled;
      }
    };

    Futures.addCallback(listenableFuture, new FutureCallback<T>() {
      @Override
      public void onSuccess(T result) {
        completableFuture.complete(result);
      }

      @Override
      public void onFailure(Throwable ex) {
        completableFuture.completeExceptionally(ex);
      }
    }, executor);

    return completableFuture;
  }
}

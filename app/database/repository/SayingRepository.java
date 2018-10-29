package database.repository;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import database.CassandraConnector;
import java.util.UUID;
import models.Saying;
import javax.inject.Inject;

public class SayingRepository extends AbstractSayingRepository {

  @Inject
  public SayingRepository(CassandraConnector connector) {
    super(connector);
  }

  @Override
  public Saying find(UUID id) {
    Row contentRow = getSession().execute(contentByIdStatement.bind(id)).one();
    if (contentRow == null) {
      return null;
    }
    Row ratingRow = getSession().execute(ratingByIdStatement.bind(id)).one();
    return retrieveSaying(contentRow, ratingRow);
  }

  @Override
  public Saying getRandom() {
    while (true) {
      Row contentRow = getSession().execute(randomContentStatement).one();
      if (contentRow != null) {
        UUID id = contentRow.getUUID("id");
        Row ratingRow = getSession().execute(ratingByIdStatement.bind(id)).one();
        return retrieveSaying(contentRow, ratingRow);
      }
    }
  }

  @Override
  public Boolean rate(UUID id, int rate) {
    BoundStatement rateStatement = rate > 0 ? incLikesStatement : incDislikesStatement;
    return getSession().execute(rateStatement.bind(id)).wasApplied();
  }
}

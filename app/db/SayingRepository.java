package db;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.UUID;
import javax.inject.Inject;
import models.Saying;

public class SayingRepository {

  private CassandraConnector connector;

  private BoundStatement findContentByIdStatement;
  private BoundStatement findRatingByIdStatement;
  private BoundStatement getRandomContentStatement;
  private BoundStatement incLikesStatement;
  private BoundStatement incDislikesStatement;

  @Inject
  public SayingRepository(CassandraConnector connector) {
    this.connector = connector;
    initSession();
    prepareStatements();
  }

  public Saying find(UUID id) {
    Row contentRow = getSession().execute(findContentByIdStatement.bind(id)).one();
    if (contentRow == null) {
      return null;
    }
    Row ratingRow = getSession().execute(findRatingByIdStatement.bind(id)).one();
    return retrieveSaying(contentRow, ratingRow);
  }

  public Saying getRandom() {
    while (true) {
      Row contentRow = getSession().execute(getRandomContentStatement).one();
      if (contentRow != null) {
        UUID id = contentRow.getUUID("id");
        Row ratingRow = getSession().execute(findRatingByIdStatement.bind(id)).one();
        return retrieveSaying(contentRow, ratingRow);
      }
    }
  }

  public boolean rate(UUID id, int rate) {
    BoundStatement rateStatement = rate > 0 ? incLikesStatement : incDislikesStatement;
    return getSession().execute(rateStatement.bind(id)).wasApplied();
  }

  private Saying retrieveSaying(Row contentRow, Row ratingRow) {
    Saying saying = new Saying();
    saying.setId(contentRow.getUUID("id"));
    saying.setText(contentRow.getString("text"));
    saying.setAuthor(contentRow.getString("author"));
    saying.setLikes(ratingRow.getLong("likes"));
    saying.setDislikes(ratingRow.getLong("dislikes"));
    return saying;
  }

  private Session getSession() {
//    if (connector.getSession() == null) {
//      initSession();
//    }
    return connector.getSession();
  }

  private void initSession() {
    Config conf = ConfigFactory.load("additional.conf");
    String contactPoint = conf.getString("cassandra.contact-point");
    String port = conf.getString("cassandra.port");
    connector.connect(contactPoint, Integer.valueOf(port), "random_sayings_generator");
  }

  private void prepareStatements() {
    incLikesStatement = prepareAndBind("UPDATE sayings_rating SET likes = likes + 1 WHERE id = ?");
    incDislikesStatement = prepareAndBind(
        "UPDATE sayings_rating SET dislikes = dislikes + 1 WHERE id = ?");
    findContentByIdStatement = prepareAndBind("SELECT * FROM sayings_content WHERE id = ? LIMIT 1");
    findRatingByIdStatement = prepareAndBind("SELECT * FROM sayings_rating WHERE id = ? LIMIT 1");
    getRandomContentStatement = prepareAndBind(
        "SELECT * FROM sayings_content WHERE token(id) > token(uuid()) LIMIT 1");
  }

  private BoundStatement prepareAndBind(String query) {
    return getSession().prepare(query).setConsistencyLevel(ConsistencyLevel.ONE).bind();
//    Consistency level ANY is not yet supported for counter table
  }
}

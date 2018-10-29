package database.repository;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import database.CassandraConnector;
import java.util.UUID;
import models.Saying;

public abstract class AbstractSayingRepository {
  private final CassandraConnector connector;

  BoundStatement contentByIdStatement;
  BoundStatement ratingByIdStatement;
  BoundStatement randomContentStatement;
  BoundStatement incLikesStatement;
  BoundStatement incDislikesStatement;

  public AbstractSayingRepository(CassandraConnector connector) {
    this.connector = connector;
    initSession();
    prepareStatements();
  }

  public abstract Object find(UUID id);

  public abstract Object getRandom();

  public abstract Object rate(UUID id, int rate);

  Session getSession() {
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
    contentByIdStatement = prepareAndBind("SELECT * FROM sayings_content WHERE id = ? LIMIT 1");
    ratingByIdStatement = prepareAndBind("SELECT * FROM sayings_rating WHERE id = ? LIMIT 1");
    randomContentStatement = prepareAndBind(
        "SELECT * FROM sayings_content WHERE token(id) > token(uuid()) LIMIT 1");
  }

  private BoundStatement prepareAndBind(String query) {
    return getSession().prepare(query).setConsistencyLevel(ConsistencyLevel.ONE).bind();
  }

  Saying retrieveSaying(Row contentRow, Row ratingRow) {
    Saying saying = new Saying();
    saying.setId(contentRow.getUUID("id"));
    saying.setText(contentRow.getString("text"));
    saying.setAuthor(contentRow.getString("author"));
    saying.setLikes(ratingRow.getLong("likes"));
    saying.setDislikes(ratingRow.getLong("dislikes"));
    return saying;
  }
}

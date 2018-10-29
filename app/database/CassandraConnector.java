package database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ThreadingOptions;
import com.datastax.driver.core.policies.LatencyAwarePolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;

public class CassandraConnector {

  private Cluster cluster;
  private Session session;

  public void connect(String node, Integer port, String keyspace) {
    Builder builder = Cluster.builder()
        .addContactPoint(node)
        .withPort(port);

    cluster = builder.build();
    session = keyspace == null ? cluster.connect() : cluster.connect(keyspace);
  }

  public Session getSession() {
    return this.session;
  }

  public void close() {
    if (session != null) {
      session.close();
    }
    if (cluster != null) {
      cluster.close();
    }
  }
}
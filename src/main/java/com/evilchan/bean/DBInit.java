package com.evilchan.bean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.mysqlclient.MySQLPool;

@ApplicationScoped
public class DBInit {

  private final MySQLPool client;
  private final boolean schemaCreate;

  public DBInit(MySQLPool client,
      @ConfigProperty(name = "myapp.schema.create", defaultValue = "true") boolean schemaCreate) {
    this.client = client;
    this.schemaCreate = schemaCreate;
  }

  void onStart(@Observes StartupEvent ev) {
    if (schemaCreate) {
      initdb();
    }
  }

  private void initdb() {
    client.query("DROP TABLE IF EXISTS fruits").execute()
        .flatMap(r -> client.query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Kiwi')").execute())
        .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Durian')").execute())
        .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Pomelo')").execute())
        .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Lychee')").execute()).await().indefinitely();
  }
}
package com.evilchan.entites;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.stream.StreamSupport;

public class Fruit {

  private Long id;

  private String name;

  public Fruit() {
  }

  public Fruit(String name) {
    this.name = name;
  }

  public Fruit(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public static Multi<Fruit> findAll(MySQLPool client) {
    return client.query("SELECT id, name FROM fruits ORDER BY name ASC").execute().onItem()
        .transformToMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
        .onItem().transform(Fruit::from);
  }

  public static Uni<Fruit> findById(MySQLPool client, Long id) {
    return client.preparedQuery("SELECT id, name FROM fruits WHERE id = ?").execute(Tuple.of(id)).onItem()
        .transform(RowSet::iterator).onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
  }

  public Uni<Long> save(MySQLPool client) {
    return client.preparedQuery("INSERT INTO fruits (name) VALUES (?)").execute(Tuple.of(name))
        .flatMap(r -> client.query("SELECT LAST_INSERT_ID() as id").execute()).onItem()
        .transform(mysqlRowSet -> mysqlRowSet.iterator().next().getLong("id"));
  }

  public Uni<Boolean> update(MySQLPool client) {
    return client.preparedQuery("UPDATE fruits SET name = ? WHERE id = ?").execute(Tuple.of(name, id)).onItem()
        .transform(mysqlRowSet -> mysqlRowSet.rowCount() == 1);
  }

  public static Uni<Boolean> delete(MySQLPool client, Long id) {
    return client.preparedQuery("DELETE FROM fruits WHERE id = ?").execute(Tuple.of(id)).onItem()
        .transform(mysqlRowSet -> mysqlRowSet.rowCount() == 1);
  }

  private static Fruit from(Row row) {
    return new Fruit(row.getLong("id"), row.getString("name"));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}

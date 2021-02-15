package com.evilchan;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.evilchan.entites.Fruit;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mutiny.mysqlclient.MySQLPool;

@Path("fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

  private final MySQLPool client;
  private final Logger log = LoggerFactory.getLogger(getClass());

  public FruitResource(MySQLPool client) {
    this.client = client;
  }

  @GET
  public Multi<Fruit> get() {
    return Fruit.findAll(client);
  }

  @GET
  @Path("{id}")
  public Uni<Response> getSingle(@PathParam("id") Long id) {
    return Fruit.findById(client, id).onItem()
        .transform(fruit -> fruit != null ? Response.ok(fruit) : Response.status(Status.NOT_FOUND)).onItem()
        .transform(ResponseBuilder::build);
  }

  @POST
  public Uni<Response> create(Fruit fruit) {
    return fruit.save(client).onItem().transform(id -> {
      log.info("id: " + id);
      return URI.create("/fruits/" + id);
    }).onItem().transform(uri -> Response.created(uri).build());
  }

  @PUT
  @Path("{id}")
  public Uni<Response> update(@PathParam("id") Long id, Fruit fruit) {
    return fruit.update(client).onItem().transform(updated -> updated ? Status.OK : Status.NOT_FOUND).onItem()
        .transform(status -> Response.status(status).build());
  }

  @DELETE
  @Path("{id}")
  public Uni<Response> delete(@PathParam("id") Long id) {
    log.info("id: " + id);

    return Fruit.delete(client, id).onItem().transform(deleted -> deleted ? Status.NO_CONTENT : Status.NOT_FOUND)
        .onItem().transform(status -> Response.status(status).build());
  }
}

package com.evilchan;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.evilchan.services.ReactiveRoleService;

import org.jboss.resteasy.annotations.SseElementType;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/hello")
public class RoleResource {

  @Inject
  ReactiveRoleService service;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/greeting/{name}")
  public Uni<String> greeting(@PathParam("name") String name) {
    return service.greeting(name);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/greeting/{count}/{name}")
  public Multi<String> greetings(@PathParam("count") int count, @PathParam("name") String name) {
    return service.greetings(count, name);
  }

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @SseElementType(MediaType.TEXT_PLAIN)
  @Path("/stream/{count}/{name}")
  public Multi<String> greetingsAsStream(@PathParam("count") int count, @PathParam("name") String name) {
    return service.greetings(count, name);
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "Hello";
  }
}
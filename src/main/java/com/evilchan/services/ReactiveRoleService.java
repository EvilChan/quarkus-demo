package com.evilchan.services;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ReactiveRoleService {

  public Uni<String> greeting(String name) {
    return Uni.createFrom().item(name).onItem().transform(n -> String.format("hello %s", n));
  }

  public Multi<String> greetings(int count, String name) {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1)).onItem()
        .transform(n -> String.format("hello %s - %d", name, n)).transform().byTakingFirstItems(count);
  }
}
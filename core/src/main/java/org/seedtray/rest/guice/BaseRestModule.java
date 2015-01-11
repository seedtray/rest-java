package org.seedtray.rest.guice;

import java.util.Set;

import org.seedtray.rest.servlet.RestFilter;

import com.google.common.collect.Sets;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;

public abstract class BaseRestModule extends ServletModule {

  private final Set<Class<?>> resources = Sets.newHashSet();

  @Override
  protected void configureServlets() {
    bind(RestFilter.class).asEagerSingleton();
    filter("/*").through(RestFilter.class);

    configureResources();

    bind(new TypeLiteral<Set<Class<?>>>() {})
        .annotatedWith(Names.named("resources")).toInstance(resources);
  }

  protected abstract void configureResources();

  protected void serveResource(Class<?> resource) {
    resources.add(resource);
  }
}
package org.seedtray.rest;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

public abstract class RestModule extends ServletModule {

  static final String RESOURCES_KEY = "resources";
  private final Set<Class<?>> resources = Sets.newHashSet();

  @Override
  protected void configureServlets() {
    bind(RestFilter.class).asEagerSingleton();
    filter("/*").through(RestFilter.class);

    configureResources();

    bind(new TypeLiteral<Set<Class<?>>>() {}).annotatedWith(Names.named(RESOURCES_KEY))
        .toInstance(resources);
  }

  @Provides @RequestScoped UrlPattern.Parameters provideMatchResult() {
    return RestFilter.getMatchResult();
  }

  protected abstract void configureResources();

  protected void serveResource(Class<?> resource) {
    resources.add(resource);
  }
}
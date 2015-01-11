package org.seedtray.rest.sample;

import org.seedtray.rest.guice.BaseRestModule;

import com.google.inject.AbstractModule;

public class SampleModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new RestModule());
  }

  public static class RestModule extends BaseRestModule {
    @Override
    protected void configureResources() {
      serveResource(IdResource.class);
    }
  }

}

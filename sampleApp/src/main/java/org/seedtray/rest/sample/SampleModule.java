package org.seedtray.rest.sample;

import org.seedtray.rest.RestModule;

import com.google.inject.AbstractModule;

public class SampleModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new SampleRestModule());
  }

  public static class SampleRestModule extends RestModule {
    @Override
    protected void configureResources() {
      serveResource(IdResource.class);
    }
  }

}

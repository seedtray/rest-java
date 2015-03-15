package org.seedtray.rest.sample;

import org.seedtray.rest.RestModule;

import com.google.inject.AbstractModule;

public class SampleModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new RestModule() {
      @Override
      protected void configureResources() {
        serveResource(IdResource.class);
      }
    });
  }

}

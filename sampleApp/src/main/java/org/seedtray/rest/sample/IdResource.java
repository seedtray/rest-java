package org.seedtray.rest.sample;

import org.seedtray.rest.HttpMethod;
import org.seedtray.rest.UrlPattern.Parameters;
import org.seedtray.rest.annotation.Endpoint;
import org.seedtray.rest.annotation.Resource;

import com.google.inject.Inject;
import com.google.inject.Provider;

@Resource("/{id}")
public class IdResource {

  private final Provider<Parameters> matchResult;

  @Inject
  public IdResource(Provider<Parameters> matchResult) {
    this.matchResult = matchResult;
  }

  @Endpoint(method = HttpMethod.GET, path = "/get")
  public void getId() {
    System.out.println("id is " + matchResult.get().getParameter("id"));
  }
}
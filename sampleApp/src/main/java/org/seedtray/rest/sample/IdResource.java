package org.seedtray.rest.sample;

import org.seedtray.rest.UrlPattern.Parameters;
import org.seedtray.rest.annotation.Path;

import com.google.inject.Inject;
import com.google.inject.Provider;

@Path("/{id}")
public class IdResource {

  private final Provider<Parameters> matchResult;

  @Inject
  public IdResource(Provider<Parameters> matchResult) {
    this.matchResult = matchResult;
  }

  @Path("/get")
  public void getId() {
    System.out.println("Holaaaaa " + matchResult.get().getParameter("id") + "!!");
  }
}
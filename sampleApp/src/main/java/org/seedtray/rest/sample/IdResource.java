package org.seedtray.rest.sample;

import org.seedtray.rest.annotation.RestMethod;
import org.seedtray.rest.annotation.RestResource;

@RestResource(path = "/id")
public class IdResource {

  @RestMethod(path = "get")
  public void getId() {
    System.out.println("Holaaaaa!");
  }
}
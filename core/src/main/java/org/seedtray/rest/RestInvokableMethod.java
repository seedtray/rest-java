package org.seedtray.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class and method metadata needed to handle a request.
 */
class RestInvokableMethod {

  private final Class<?> resource;
  private final Method method;

  public RestInvokableMethod(Class<?> resource, Method method) {
    this.resource = checkNotNull(resource);
    this.method = checkNotNull(method);
  }

  public Class<?> getResource() {
    return resource;
  }

  public void execute(Object receiver) {
    try {
      method.invoke(receiver);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
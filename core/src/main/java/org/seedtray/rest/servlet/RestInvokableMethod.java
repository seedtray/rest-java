package org.seedtray.rest.servlet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seedtray.rest.UrlPattern.MatchResult;

public class RestInvokableMethod {

  private Class<?> resource;
  private Method method;

  public RestInvokableMethod(Class<?> resource, Method method) {
    this.resource = checkNotNull(resource);
    this.method = checkNotNull(method);
  }

  public Class<?> getResource() {
    return resource;
  }

  public void execute(Object receiver, MatchResult matchResult, HttpServletRequest request,
      HttpServletResponse response) {

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

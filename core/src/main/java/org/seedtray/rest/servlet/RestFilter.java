package org.seedtray.rest.servlet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seedtray.rest.UrlPattern;
import org.seedtray.rest.UrlPattern.MatchResult;
import org.seedtray.rest.annotation.RestMethod;
import org.seedtray.rest.annotation.RestResource;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class RestFilter implements Filter {

  private final Map<UrlPattern, RestInvokableMethod> methodsByUrlPattern;
  private final Injector guiceInjector;

  @Inject
  public RestFilter(Injector guiceInjector, @Named("resources") Set<Class<?>> resources) {
    this.guiceInjector = checkNotNull(guiceInjector);

    ImmutableMap.Builder<UrlPattern, RestInvokableMethod> builder = ImmutableMap.builder();

    for (Class<?> resource : resources) {
      checkArgument(resource.isAnnotationPresent(RestResource.class),
          "Missing @RestResource annotation: " + resource.getName());
      for (Method method : resource.getMethods()) {
        if (method.isAnnotationPresent(RestMethod.class)) {
          builder.put(getMethodUrlPattern(resource, method),
              new RestInvokableMethod(resource, method));
        }
      }
    }
    methodsByUrlPattern = builder.build();
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String path = request.getServletPath();
    for (UrlPattern pattern : methodsByUrlPattern.keySet()) {
      Optional<MatchResult> matchResult = pattern.match(path);
      if (matchResult.isPresent()) {
        RestInvokableMethod invokableMethod = methodsByUrlPattern.get(pattern);
        Object instance = guiceInjector.getInstance(invokableMethod.getResource());
        invokableMethod.execute(instance, matchResult.get(), request, response);
      }
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

  private UrlPattern getMethodUrlPattern(Class<?> resourceClass, Method method) {
    RestResource resourceInfo = resourceClass.getAnnotation(RestResource.class);
    RestMethod methodInfo = method.getAnnotation(RestMethod.class);
    return new UrlPattern(resourceInfo.path() + "/" + methodInfo.path());
  }

}
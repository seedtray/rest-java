package org.seedtray.rest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.seedtray.rest.annotation.Endpoint;
import org.seedtray.rest.UrlPattern.Parameters;
import org.seedtray.rest.annotation.Resource;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.OutOfScopeException;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceFilter;

/**
 * An HTTP {@link Filter} that routes requests to methods annotated with {@link Endpoint} of classes annotated
 * with {@link Resource}.
 */
public class RestFilter implements Filter {

  static final ThreadLocal<Context> localContext = new ThreadLocal<Context>();

  private final Table<String, UrlPattern, RestInvokableMethod> methodTable;
  private final Injector guiceInjector;

  @Inject
  public RestFilter(Injector guiceInjector,
      @Named(RestModule.RESOURCES_KEY) Set<Class<?>> resources) {

    this.guiceInjector = checkNotNull(guiceInjector);

    ImmutableTable.Builder<String, UrlPattern, RestInvokableMethod> builder = ImmutableTable.builder();
    for (Class<?> resource : resources) {
      checkArgument(resource.isAnnotationPresent(Resource.class),
          "Missing @Resource annotation: " + resource.getName());
      for (Method method : resource.getMethods()) {
        if (method.isAnnotationPresent(Endpoint.class)) {
          Endpoint endpoint = method.getAnnotation(Endpoint.class);
          builder.put(endpoint.method(), getMethodUrlPattern(resource, method),
              new RestInvokableMethod(resource, method));
        }
      }
    }

    methodTable = builder.build();
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Context previous = localContext.get();
    try {
      if (request instanceof HttpServletRequest) {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
      }
    } finally {
      localContext.set(previous);
    }
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String path = request.getServletPath();
    String httpMethod = request.getMethod();
    for (UrlPattern pattern : methodTable.row(httpMethod).keySet()) {
      Optional<Parameters> matchResult = pattern.match(path);
      if (matchResult.isPresent()) {
        Parameters result = matchResult.get();
        localContext.set(new Context(result));
        RestInvokableMethod invokableMethod = methodTable.get(httpMethod, pattern);
        Object instance = guiceInjector.getInstance(invokableMethod.getResource());
        invokableMethod.execute(instance);
      }
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

  private UrlPattern getMethodUrlPattern(Class<?> resourceClass, Method method) {
    Resource resourceAnnotation = resourceClass.getAnnotation(Resource.class);
    Endpoint methodAnnotation = method.getAnnotation(Endpoint.class);
    return new UrlPattern(resourceAnnotation.value() + methodAnnotation.path());
  }

  public static Parameters getMatchResult() {
    return getContext().matchResult;
  }

  private static Context getContext() {
    Context context = localContext.get();
    if (context == null) {
      throw new OutOfScopeException("Cannot access scoped object. Either you"
          + " are not inside an HTTP Servlet request, or you may"
          + " have forgotten to apply " + GuiceFilter.class.getName()
          + " as a servlet filter for this request.");
    }
    return context;
  }

  private static class Context {

    final Parameters matchResult;

    Context(Parameters matchResult) {
      this.matchResult = checkNotNull(matchResult);
    }
  }

}
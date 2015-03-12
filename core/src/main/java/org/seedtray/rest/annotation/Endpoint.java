package org.seedtray.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for methods of a {@link Resource} class.
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
@Documented
public @interface Endpoint {
  /**
   * Typically one of the standard HTTP methods defined in {@link org.seedtray.rest.HttpMethod}.
   */
  String method();

  /**
   * A relative path within this resource that is mapped to the annotated method.
   */
  String path();
}

package org.seedtray.rest.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A URL pattern path.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RUNTIME)
public @interface Path {
  String value();
}

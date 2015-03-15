package org.seedtray.rest.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for REST resources.
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
public @interface Resource {
  String value();
}

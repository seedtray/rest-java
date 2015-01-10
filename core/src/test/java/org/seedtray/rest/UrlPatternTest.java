package org.seedtray.rest;

import org.seedtray.rest.UrlPattern.MatchResult;

import com.google.common.base.Optional;

import junit.framework.TestCase;

/**
 * Unit tests of {@link UrlPattern}.
 */
public class UrlPatternTest extends TestCase {

  public void testUrlPattern_noRegexp() {
    assertUrlFails("/service", "");
    assertUrlMatches("/service", "/service");
    assertUrlFails("/service", "/service/");
    assertParam("/{all}", "/media", "all", "media");
  }

  public void testUrlPattern_regexpPatterns() {
    assertParam("/profiles/{id}", "/profiles/1", "id", "1");
    assertParam("/profiles/{id}/{action}", "/profiles/1/delete", "id", "1");
    assertParam("/profiles/{id}/{action}", "/profiles/1/delete", "action", "delete");

    assertParam("/profiles/{id : [\\d]}", "/profiles/1", "id", "1");
    assertUrlFails("/profiles/{id : [\\d]}", "/profiles/12");
    assertUrlFails("/profiles/{id : [\\d]}", "/profiles/1/");
    assertParam("/profiles/{id : [\\d]}/{action}", "/profiles/1/delete", "action", "delete");
    assertParam("/profiles/{id : [\\d]}/{action}", "/profiles/1/delete", "id", "1");
    assertParam("/profiles/{id : [\\d]}/{action : [\\w+]}", "/profiles/1/delete", "id", "1");
    assertParam("/profiles/{id : [\\d]}/{action : [\\w+]}", "/profiles/1/delete", "action",
        "delete");
    assertParam("/profiles/{id : [\\d]}/{action : [\\w*]}", "/profiles/1/", "action", "");
    assertParam("/profiles/{id : [\\d]}/{action : [\\w*]}", "/profiles/1/", "id", "1");
    assertParam("/profiles/{id}/{action : [\\w+]}", "/profiles/1/get", "id", "1");
    assertParam("/profiles/{id}/{action : [\\w+]}", "/profiles/1/get", "action", "get");
    assertParam("/profiles/{id}/{action : [\\w*]}", "/profiles/1/", "action", "");
    assertParam("/profiles/{path : [[\\w/]*]}", "/profiles/some/path", "path", "some/path");
  }

  private MatchResult assertUrlMatches(String template, String url) {
    Optional<MatchResult> result = new UrlPattern(template).match(url);
    assertTrue(result.isPresent());
    return result.get();
  }

  private void assertUrlFails(String template, String url) {
    Optional<MatchResult> result = new UrlPattern(template).match(url);
    assertFalse(result.isPresent());
  }

  private void assertParam(String template, String url, String name, String value) {
    MatchResult result = assertUrlMatches(template, url);
    assertEquals(value, result.getParameter(name));
  }
}
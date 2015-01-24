package org.seedtray.rest;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A URL pattern.
 * TODO(julian): Add more docs. Document pattern grammar.
 */
public class UrlPattern {

  /**
   * Url pattern parameters.
   */
  public static class Parameters {

    private static final Parameters EMPTY =
        new Parameters(ImmutableMap.<String, String>of());
    private final Map<String, String> params;

    public Parameters(Map<String, String> params) {
      this.params = ImmutableMap.copyOf(params);
    }

    public String getParameter(String name) {
      return params.get(name);
    }
  }

  private final static Pattern PATH_REGEX = 
      Pattern.compile("\\{(\\w+)(?:\\s*\\:\\s*\\[(.+?)?\\])?\\}");

  private final Pattern pattern;
  private final ImmutableList<String> names;

  public UrlPattern(String urlTemplate) {

    Matcher m = PATH_REGEX.matcher(urlTemplate);

    StringBuffer regexBuffer = new StringBuffer();
    Builder<String> namesBuilder = ImmutableList.builder();

    // The index of the last match.
    int lastMatch = 0;

    while (m.find()) {

      String paramName = m.group(1);
      String regexp = m.group(2);

      if (regexp != null) {
        checkRegexp(regexp, paramName);
      }

      if (lastMatch != m.start()) {
        String prefix = urlTemplate.substring(lastMatch, m.start());
        regexBuffer.append(Pattern.quote(prefix));
      }

      regexp = firstNonNull(regexp, "[^/]+");

      namesBuilder.add(paramName);

      regexBuffer.append("(" + regexp + ")");
      lastMatch = m.end();
    }

    if (lastMatch != urlTemplate.length()) {
      String prefix = urlTemplate.substring(lastMatch, urlTemplate.length());
      regexBuffer.append(Pattern.quote(prefix));
    }

    pattern = Pattern.compile("^" + regexBuffer.toString() + "$");
    names = namesBuilder.build();
  }

  private void checkRegexp(String regexp, String paramName) {
    Pattern p;
    try {
      p = Pattern.compile(regexp);
    } catch (PatternSyntaxException e) {
      throw new IllegalArgumentException(
          String.format("Parameter %s has invalid pattern: %s", paramName, regexp));
    }

    checkArgument((p.matcher("").groupCount() == 0),
        String.format("Invalid parameter %s pattern. Patterns cannot contain capturing groups: %s",
            paramName, regexp));
  }

  public Optional<Parameters> match(String url) {
    Matcher m = pattern.matcher(url);

    if (!m.matches()) {
      return Optional.absent();
    }

    if (names.isEmpty()) {
      return Optional.of(Parameters.EMPTY);
    }

    int numParams = m.groupCount();
    Map<String, String> params = Maps.newHashMapWithExpectedSize(numParams);
    for (int i = 0; i < m.groupCount(); i++) {
      params.put(names.get(i), m.group(i + 1));
    }

    return Optional.of(new Parameters(params));
  }
}
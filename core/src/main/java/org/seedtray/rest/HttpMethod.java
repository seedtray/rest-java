package org.seedtray.rest;

/**
 * Standard HTTP methods.
 * @see <a href="http://www.ietf.org/rfc/rfc2616.txt">RFC 2616</a>
 */
public class HttpMethod {

  private HttpMethod() {
  }

  /**
   * HTTP GET method.
   */
  public static final String GET = "GET";

  /**
   * HTTP POST method.
   */
  public static final String POST = "POST";

  /**
   * HTTP PUT method.
   */
  public static final String PUT = "PUT";

  /**
   * HTTP DELETE method.
   */
  public static final String DELETE = "DELETE";

  /**
   * HTTP HEAD method.
   */
  public static final String HEAD = "HEAD";

  /**
   * HTTP OPTIONS method.
   */
  public static final String OPTIONS = "OPTIONS";

  /**
   * HTTP TRACE method.
   */
  public static final String TRACE = "TRACE";

  /**
   * HTTP CONNECT method.
   */
  public static final String CONNECT = "CONNECT";

}

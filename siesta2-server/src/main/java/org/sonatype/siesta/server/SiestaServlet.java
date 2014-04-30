package org.sonatype.siesta.server;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Siesta servlet.
 *
 * @since 2.0
 */
@Named
@Singleton
public class SiestaServlet
  extends ServletContainer
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Inject
  public SiestaServlet(final ResourceConfig resourceConfig) {
    super(checkNotNull(resourceConfig));
  }

  @Override
  public void service(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException
  {
    checkNotNull(request);
    checkNotNull(response);

    // Log the request URI+URL muck
    String uri = request.getRequestURI();
    if (request.getQueryString() != null) {
      uri = String.format("%s?%s", uri, request.getQueryString());
    }

    if (log.isDebugEnabled()) {
      log.debug("Processing: {} {} ({})", request.getMethod(), uri, request.getRequestURL());
    }

    MDC.put(getClass().getName(), uri);
    try {
      super.service(request, response);
    }
    finally {
      MDC.remove(getClass().getName());
    }
  }
}
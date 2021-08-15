package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bugsnag.callbacks.JaxrsCallback;
import com.bugsnag.callbacks.ServletCallback;
import com.bugsnag.filters.BugsnagContainerRequestFilter;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

public class JaxrsCallbackTest {

    private Bugsnag bugsnag;

    /**
     * Generate a new request instance which will be read by the servlet
     * context and callback
     */
    @Before
    public void setUp() throws URISyntaxException {
        bugsnag = new Bugsnag("apikey", false);
        bugsnag.setDelivery(null);
        bugsnag.setRequestCallback("jaxrs");

        HttpRequest request = mock(HttpRequest.class);

        MultivaluedMap<String, String> params = new Headers<String>();
        params.put("account", Collections.singletonList("Acme Co"));
        params.put("name", Collections.singletonList("Bill"));
        when(request.getFormParameters()).thenReturn(params);

        when(request.getHttpMethod()).thenReturn("PATCH");
        when(request.getUri()).thenReturn(new ResteasyUriInfo(new URI("/foo/bar")));
        when(request.getRemoteAddress()).thenReturn("12.0.4.57");

        MultivaluedMap<String, String> headers = new Headers<String>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("Content-Length", Collections.singletonList("54"));
        headers.put("X-Custom-Header", Arrays.asList("some-data-1", "some-data-2"));
        headers.put("Authorization", Collections.singletonList("Basic ABC123"));
        headers.put("Cookie", Collections.singletonList("name1=val1; name2=val2"));
        ResteasyHttpHeaders httpHeaders = new ResteasyHttpHeaders(headers);
        when(request.getHttpHeaders()).thenReturn(httpHeaders);
        when(request.getMutableHeaders()).thenReturn(headers);

        PreMatchContainerRequestContext context = mock(PreMatchContainerRequestContext.class);
        when(context.getHttpRequest()).thenReturn(request);
        BugsnagContainerRequestFilter filter = new BugsnagContainerRequestFilter();
        filter.filter(context);
    }

    /**
     * Close test Bugsnag
     */
    @After
    public void closeBugsnag() {
        bugsnag.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequestMetadataAdded() {
        Report report = generateReport(new java.lang.Exception("Spline reticulation failed"));
        JaxrsCallback callback = new JaxrsCallback();
        callback.beforeNotify(report);

        Map<String, Object> metadata = report.getMetaData();
        assertTrue(metadata.containsKey("request"));

        Map<String, Object> request = (Map<String, Object>) metadata.get("request");
        assertEquals("/foo/bar", request.get("url"));
        assertEquals("PATCH", request.get("method"));
        assertEquals("12.0.4.57", request.get("clientIp"));

        assertTrue(request.containsKey("headers"));
        Map<String, String> headers = (Map<String, String>) request.get("headers");
        assertEquals("application/json", headers.get("Content-Type"));
        assertEquals("54", headers.get("Content-Length"));
        assertEquals("some-data-1,some-data-2", headers.get("X-Custom-Header"));

        // Make sure that actual Authorization header value is not in the report
        assertEquals("[FILTERED]", headers.get("Authorization"));

        // Make sure that actual cookies are not in the report
        assertEquals("[FILTERED]", headers.get("Cookie"));

        assertTrue(request.containsKey("params"));
        Map<String, List<String>> params = (Map<String, List<String>>) request.get("params");
        assertTrue(params.containsKey("account"));
        List<String> account = params.get("account");
        assertEquals("Acme Co", account.get(0));

        assertTrue(params.containsKey("name"));
        List<String> name = params.get("name");
        assertEquals("Bill", name.get(0));
    }

    @Test
    public void testRequestContextSet() {
        Report report = generateReport(new java.lang.Exception("Spline reticulation failed"));
        JaxrsCallback callback = new JaxrsCallback();
        callback.beforeNotify(report);

        assertEquals("PATCH /foo/bar", report.getContext());
    }

    @Test
    public void testExistingContextNotOverridden() {
        Report report = generateReport(new java.lang.Exception("Spline reticulation failed"));
        report.setContext("Honey nut corn flakes");
        ServletCallback callback = new ServletCallback();
        callback.beforeNotify(report);

        assertEquals("Honey nut corn flakes", report.getContext());
    }

    private Report generateReport(java.lang.Exception exception) {
        return bugsnag.buildReport(exception);
    }
}

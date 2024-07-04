package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bugsnag.callbacks.JakartaServletCallback;
import com.bugsnag.servlet.jakarta.BugsnagServletRequestListener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JakartaServletCallbackTest {

    private Bugsnag bugsnag;

    /**
     * Generate a new request instance which will be read by the servlet
     * context and callback
     */
    @Before
    public void setUp() {
        bugsnag = new Bugsnag("apikey", false);
        bugsnag.setDelivery(null);

        HttpServletRequest request = mock(HttpServletRequest.class);

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("account", new String[]{"Acme Co"});
        params.put("name", new String[]{"Bill"});
        when(request.getParameterMap()).thenReturn(params);

        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURL()).thenReturn(new StringBuffer("/foo/bar"));
        when(request.getRequestURI()).thenReturn("/foo/bar");
        when(request.getRemoteAddr()).thenReturn("12.0.4.57");

        when(request.getHeaderNames()).thenReturn(
                stringsToEnumeration(
                        "Content-Type",
                        "Content-Length",
                        "X-Custom-Header",
                        "Authorization",
                        "Cookie"));
        when(request.getHeaders("Content-Type")).thenReturn(
                stringsToEnumeration("application/json"));
        when(request.getHeaders("Content-Length")).thenReturn(
                stringsToEnumeration("54"));
        when(request.getHeaders("X-Custom-Header")).thenReturn(
                stringsToEnumeration("some-data-1", "some-data-2"));
        when(request.getHeaders("Authorization")).thenReturn(
                stringsToEnumeration("Basic ABC123"));
        when(request.getHeaders("Cookie")).thenReturn(
                stringsToEnumeration("name1=val1; name2=val2"));

        ServletContext context = mock(ServletContext.class);
        BugsnagServletRequestListener listener = new BugsnagServletRequestListener();
        listener.requestInitialized(new ServletRequestEvent(context, request));
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
        JakartaServletCallback callback = new JakartaServletCallback();
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

        headers.put("ipAddress", "User:Password");
        headers.put("logLevel", "123456ABCDEF");
        headers.put("ipaddress", "User:Password");
        headers.put("loglevel", "123456ABCDEF");
        // Make sure that actual Authorization header value is not in the report
        assertEquals("[FILTERED]", headers.get("ipAddress"));

        // Make sure that actual cookies are not in the report
        assertEquals("[FILTERED]", headers.get("logLevel"));

        assertTrue(request.containsKey("params"));
        Map<String, String[]> params = (Map<String, String[]>) request.get("params");
        assertTrue(params.containsKey("account"));
        String[] account = params.get("account");
        assertEquals("Acme Co", account[0]);

        assertTrue(params.containsKey("name"));
        String[] name = params.get("name");
        assertEquals("Bill", name[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequestMetadataAddedRedacted() {
        Report report = generateReport(new java.lang.Exception("Spline reticulation failed"));
        JakartaServletCallback callback = new JakartaServletCallback();
        callback.beforeNotify(report);

        Map<String, Object> metadata = report.getRedactedMetaData();
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
        assertEquals("[REDACTED]", headers.get("Authorization"));

        // Make sure that actual cookies are not in the report
        assertEquals("[REDACTED]", headers.get("Cookie"));

        assertTrue(request.containsKey("params"));
        Map<String, String[]> params = (Map<String, String[]>) request.get("params");
        assertTrue(params.containsKey("account"));
        String[] account = params.get("account");
        assertEquals("Acme Co", account[0]);

        assertTrue(params.containsKey("name"));
        String[] name = params.get("name");
        assertEquals("Bill", name[0]);
    }

    @Test
    public void testRequestContextSet() {
        Report report = generateReport(new java.lang.Exception("Spline reticulation failed"));
        JakartaServletCallback callback = new JakartaServletCallback();
        callback.beforeNotify(report);

        assertEquals("PATCH /foo/bar", report.getContext());
    }

    @Test
    public void testExistingContextNotOverridden() {
        Report report = generateReport(new java.lang.Exception("Spline reticulation failed"));
        report.setContext("Honey nut corn flakes");
        JakartaServletCallback callback = new JakartaServletCallback();
        callback.beforeNotify(report);

        assertEquals("Honey nut corn flakes", report.getContext());
    }

    private Report generateReport(java.lang.Exception exception) {
        return bugsnag.buildReport(exception);
    }

    private Enumeration<String> stringsToEnumeration(String... strings) {
        return Collections.enumeration(Arrays.asList(strings));
    }
}

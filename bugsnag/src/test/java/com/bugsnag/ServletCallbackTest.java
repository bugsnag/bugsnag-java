package com.bugsnag.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.ServletCallback;

import org.junit.Before;
import org.junit.Test;

import java.lang.StringBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

public class ServletCallbackTest {

    /**
     * Generate a new request instance which will be read by the servlet
     * context and callback
     */
    @Before
    public void setUp() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("account", new String[]{"Acme Co"});
        params.put("name", new String[]{"Bill"});
        when(request.getParameterMap()).thenReturn(params);

        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURL()).thenReturn(new StringBuffer("/foo/bar"));
        when(request.getRequestURI()).thenReturn("/foo/bar");
        when(request.getRemoteAddr()).thenReturn("12.0.4.57");

        Enumeration<String> headers = new Vector<String>(
                Arrays.asList("Content-Type", "Content-Length")).elements();
        when(request.getHeaderNames()).thenReturn(headers);
        when(request.getHeader("Content-Type")).thenReturn("application/json");
        when(request.getHeader("Content-Length")).thenReturn("54");

        ServletContext context = mock(ServletContext.class);
        BugsnagServletRequestListener listener = new BugsnagServletRequestListener();
        listener.requestInitialized(new ServletRequestEvent(context, request));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequestMetadataAdded() {
        Report report = generateReport(new Exception("Spline reticulation failed"));
        ServletCallback callback = new ServletCallback();
        callback.beforeNotify(report);

        Map<String, Object> metadata = (Map<String, Object>)report.getMetaData();
        assertTrue(metadata.containsKey("request"));

        Map<String, Object> request = (Map<String, Object>)metadata.get("request");
        assertEquals("/foo/bar", request.get("url"));
        assertEquals("PATCH", request.get("method"));
        assertEquals("12.0.4.57", request.get("clientIp"));

        assertTrue(request.containsKey("headers"));
        Map<String, String> headers = (Map<String, String>)request.get("headers");
        assertEquals("application/json", headers.get("Content-Type"));
        assertEquals("54", headers.get("Content-Length"));

        assertTrue(request.containsKey("params"));
        Map<String, String[]> params = (Map<String, String[]>)request.get("params");
        assertTrue(params.containsKey("account"));
        String[] account = params.get("account");
        assertEquals("Acme Co", account[0]);

        assertTrue(params.containsKey("name"));
        String[] name = params.get("name");
        assertEquals("Bill", name[0]);
    }

    @Test
    public void testRequestContextSet() {
        Report report = generateReport(new Exception("Spline reticulation failed"));
        ServletCallback callback = new ServletCallback();
        callback.beforeNotify(report);

        assertEquals("PATCH /foo/bar", report.getContext());
    }

    @Test
    public void testExistingContextNotOverridden() {
        Report report = generateReport(new Exception("Spline reticulation failed"));
        report.setContext("Honey nut corn flakes");
        ServletCallback callback = new ServletCallback();
        callback.beforeNotify(report);

        assertEquals("Honey nut corn flakes", report.getContext());
    }

    Report generateReport(Exception exception) {
        Bugsnag bugsnag = new Bugsnag("apikey", false);
        bugsnag.setDelivery(null);

        return bugsnag.buildReport(exception);
    }
}

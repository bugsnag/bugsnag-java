package com.bugsnag;

import static com.bugsnag.TestUtils.anyMapOf;
import static com.bugsnag.TestUtils.verifyAndGetReport;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bugsnag.HandledState.SeverityReasonType;

import com.bugsnag.callbacks.OnErrorCallback;
import com.bugsnag.delivery.Delivery;

import com.bugsnag.serialization.Serializer;
import com.bugsnag.testapp.springboot.TestSpringBootApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Test that a Spring Boot application configured with the
 * {@link BugsnagSpringConfiguration} performs as expected.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestSpringBootApplication.class,
        webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringMvcTest {

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Bugsnag bugsnag;

    private Delivery delivery;

    private long sessionsStartedBeforeTest;

    /**
     * Initialize test state
     */
    @Before
    public void setUp() {
        delivery = mock(Delivery.class);

        bugsnag.setDelivery(delivery);
        bugsnag.getConfig().setAutoDetectErrors(true);
        bugsnag.getConfig().setAutoCaptureSessions(true);

        // Cannot reset the session count on the bugsnag bean for each test, so note
        // the current session count before the test starts instead.
        sessionsStartedBeforeTest = getSessionCount();
    }

    @Test
    public void bugsnagNotifyWhenUncaughtControllerException() {
        callRuntimeExceptionEndpoint();

        BugsnagEvent event = verifyAndGetReport(delivery);

        // Assert that the exception was detected correctly
        assertEquals("Test", event.getExceptionMessage());
        assertEquals("java.lang.RuntimeException", event.getExceptionName());

        // Assert that the severity, severity reason and unhandled values are correct
        assertEquals(Severity.ERROR.getValue(), event.getSeverity());
        assertEquals(
                SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE.toString(),
                event.getSeverityReason().getType());
        assertThat(
                event.getSeverityReason().getAttributes(),
                is(Collections.singletonMap("framework", "Spring")));
        assertTrue(event.getUnhandled());
    }

    @Test
    public void noBugsnagNotifyWhenSendUncaughtExceptionsFalse() {
        bugsnag.getConfig().setAutoDetectErrors(false);

        callRuntimeExceptionEndpoint();

        verifyNoReport();
    }

    @Test
    public void bugsnagSessionStartedWhenAutoCaptureSessions() {
        callRuntimeExceptionEndpoint();

        assertSessionsStarted(1);
    }

    @Test
    public void noBugsnagSessionStartedWhenAutoCaptureSessionsFalse() {
        bugsnag.getConfig().setAutoCaptureSessions(false);

        callRuntimeExceptionEndpoint();

        assertSessionsStarted(0);
    }

    @Test
    public void requestMetadataSetCorrectly() {
        callRuntimeExceptionEndpoint();

        BugsnagEvent event = verifyAndGetReport(delivery);

        // Check that the context is set to the HTTP method and URI of the endpoint
        assertEquals("GET /throw-runtime-exception", event.getContext());

        // Check that the request metadata is set as expected
        @SuppressWarnings(value = "unchecked") Map<String, Object> requestMetadata =
                (Map<String, Object>) event.getMetadata().get("request");
        assertEquals("http://localhost:" + randomServerPort + "/throw-runtime-exception",
                requestMetadata.get("url"));
        assertEquals("GET", requestMetadata.get("method"));
        assertEquals("127.0.0.1", requestMetadata.get("clientIp"));

        // Assert that the request params are as expected
        @SuppressWarnings(value = "unchecked") Map<String, String[]> params =
                (Map<String, String[]>) requestMetadata.get("params");
        assertEquals("paramVal1", params.get("param1")[0]);
        assertEquals("paramVal2", params.get("param2")[0]);

        // Assert that the request headers are as expected, including headers with
        // multiple values represented as a comma-separated string.
        @SuppressWarnings(value = "unchecked") Map<String, String> headers =
                (Map<String, String>) requestMetadata.get("headers");
        assertEquals("header1Val1,header1Val2", headers.get("header1"));
        assertEquals("header2Val1", headers.get("header2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void springVersionSetCorrectly() {
        callRuntimeExceptionEndpoint();

        BugsnagEvent event = verifyAndGetReport(delivery);

        // Check that the Spring version is set as expected
        Map<String, Object> deviceMetadata = event.getDevice();
        Map<String, Object> runtimeVersions =
                (Map<String, Object>) deviceMetadata.get("runtimeVersions");
        assertEquals(SpringVersion.getVersion(), runtimeVersions.get("springFramework"));
        assertEquals(SpringBootVersion.getVersion(), runtimeVersions.get("springBoot"));
    }

    @Test
    public void unhandledTypeMismatchExceptionSeverityInfo() {
        callUnhandledTypeMismatchExceptionEndpoint();

        BugsnagEvent event = verifyAndGetReport(delivery);

        assertTrue(event.getUnhandled());
        assertEquals("info", event.getSeverity());
        assertEquals("exceptionClass", event.getSeverityReason().getType());
        assertThat(event.getSeverityReason().getAttributes(),
                is(Collections.singletonMap("exceptionClass", "TypeMismatchException")));
    }

    @Test
    public void unhandledTypeMismatchExceptionCallbackSeverity()
            throws IllegalAccessException, NoSuchFieldException {
        BugsnagEvent event;
        OnErrorCallback callback = new OnErrorCallback() {
            @Override
            public boolean onError(BugsnagEvent report) {
                report.setSeverity(Severity.WARNING);
                return true;
            }
        };

        try {
            bugsnag.addOnError(callback);

            callUnhandledTypeMismatchExceptionEndpoint();

            event = verifyAndGetReport(delivery);
        } finally {
            // Remove the callback via reflection so that subsequent tests do not use it
            Field callbacksField = Configuration.class.getDeclaredField("callbacks");
            @SuppressWarnings(value = "unchecked") Collection<OnErrorCallback> callbacks =
                    (Collection<OnErrorCallback>) callbacksField.get(bugsnag.getConfig());
            callbacks.remove(callback);
        }

        assertTrue(event.getUnhandled());
        assertEquals("warning", event.getSeverity());
        assertEquals("userCallbackSetSeverity", event.getSeverityReason().getType());
    }

    @Test
    public void handledTypeMismatchExceptionUserSeverity() {
        callHandledTypeMismatchExceptionUserSeverityEndpoint();

        BugsnagEvent event = verifyAndGetReport(delivery);

        assertFalse(event.getUnhandled());
        assertEquals("warning", event.getSeverity());
        assertEquals("userSpecifiedSeverity", event.getSeverityReason().getType());
        assertThat(event.getSeverityReason().getAttributes(), is(Collections.EMPTY_MAP));
    }

    @Test
    public void handledTypeMismatchExceptionCallbackSeverity() {
        callHandledTypeMismatchExceptionCallbackSeverityEndpoint();

        BugsnagEvent event = verifyAndGetReport(delivery);

        assertFalse(event.getUnhandled());
        assertEquals("warning", event.getSeverity());
        assertEquals("userCallbackSetSeverity", event.getSeverityReason().getType());
    }

    private void callUnhandledTypeMismatchExceptionEndpoint() {
        this.restTemplate.getForEntity(
                "/throw-type-mismatch-exception", String.class);
    }

    private void callHandledTypeMismatchExceptionUserSeverityEndpoint() {
        this.restTemplate.getForEntity(
                "/handled-type-mismatch-exception-user-severity", String.class);
    }

    private void callHandledTypeMismatchExceptionCallbackSeverityEndpoint() {
        this.restTemplate.getForEntity(
                "/handled-type-mismatch-exception-callback-severity", String.class);
    }

    private void callRuntimeExceptionEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("header1", "header1Val1");
        headers.add("header1", "header1Val2");
        headers.add("header2", "header2Val1");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        this.restTemplate.exchange(
                "/throw-runtime-exception?param1=paramVal1&param2=paramVal2",
                HttpMethod.GET,
                entity,
                String.class);
    }

    private void verifyNoReport() {
        verify(delivery, times(0)).deliver(
                any(Serializer.class),
                any(),
                anyMapOf(String.class, String.class));
    }

    private void assertSessionsStarted(int sessionsStarted) {
        assertEquals(sessionsStartedBeforeTest + sessionsStarted, getSessionCount());
    }

    private long getSessionCount() {
        return bugsnag.getSessionTracker().getBatchCount() != null
            ? bugsnag.getSessionTracker().getBatchCount().getSessionsStarted() : 0;
    }
}

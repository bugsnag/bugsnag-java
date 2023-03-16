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

import com.bugsnag.callbacks.Callback;
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
import org.springframework.boot.web.server.LocalServerPort;
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
 * {@link com.bugsnag.BugsnagSpringConfiguration} performs as expected.
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
        bugsnag.getConfig().setSendUncaughtExceptions(true);
        bugsnag.getConfig().setAutoCaptureSessions(true);

        // Cannot reset the session count on the bugsnag bean for each test, so note
        // the current session count before the test starts instead.
        sessionsStartedBeforeTest = getSessionCount();
    }

    @Test
    public void bugsnagNotifyWhenUncaughtControllerException() {
        callRuntimeExceptionEndpoint();

        Report report = verifyAndGetReport(delivery);

        // Assert that the exception was detected correctly
        assertEquals("Test", report.getExceptionMessage());
        assertEquals("java.lang.RuntimeException", report.getExceptionName());

        // Assert that the severity, severity reason and unhandled values are correct
        assertEquals(Severity.ERROR.getValue(), report.getSeverity());
        assertEquals(
                SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE.toString(),
                report.getSeverityReason().getType());
        assertThat(
                report.getSeverityReason().getAttributes(),
                is(Collections.singletonMap("framework", "Spring")));
        assertTrue(report.getUnhandled());
    }

    @Test
    public void noBugsnagNotifyWhenSendUncaughtExceptionsFalse() {
        bugsnag.getConfig().setSendUncaughtExceptions(false);

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

        Report report = verifyAndGetReport(delivery);

        // Check that the context is set to the HTTP method and URI of the endpoint
        assertEquals("GET /throw-runtime-exception", report.getContext());

        // Check that the request metadata is set as expected
        @SuppressWarnings(value = "unchecked") Map<String, Object> requestMetadata =
                (Map<String, Object>) report.getMetaData().get("request");
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

        Report report = verifyAndGetReport(delivery);

        // Check that the Spring version is set as expected
        Map<String, Object> deviceMetadata = report.getDevice();
        Map<String, Object> runtimeVersions =
                (Map<String, Object>) deviceMetadata.get("runtimeVersions");
        assertEquals(SpringVersion.getVersion(), runtimeVersions.get("springFramework"));
        assertEquals(SpringBootVersion.getVersion(), runtimeVersions.get("springBoot"));
    }

    @Test
    public void unhandledTypeMismatchExceptionSeverityInfo() {
        callUnhandledTypeMismatchExceptionEndpoint();

        Report report = verifyAndGetReport(delivery);

        assertTrue(report.getUnhandled());
        assertEquals("info", report.getSeverity());
        assertEquals("exceptionClass", report.getSeverityReason().getType());
        assertThat(report.getSeverityReason().getAttributes(),
                is(Collections.singletonMap("exceptionClass", "TypeMismatchException")));
    }

    @Test
    public void unhandledTypeMismatchExceptionCallbackSeverity()
            throws IllegalAccessException, NoSuchFieldException {
        Report report;
        Callback callback = new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setSeverity(Severity.WARNING);
            }
        };

        try {
            bugsnag.addCallback(callback);

            callUnhandledTypeMismatchExceptionEndpoint();

            report = verifyAndGetReport(delivery);
        } finally {
            // Remove the callback via reflection so that subsequent tests do not use it
            Field callbacksField = Configuration.class.getDeclaredField("callbacks");
            @SuppressWarnings(value = "unchecked") Collection<Callback> callbacks =
                    (Collection<Callback>) callbacksField.get(bugsnag.getConfig());
            callbacks.remove(callback);
        }

        assertTrue(report.getUnhandled());
        assertEquals("warning", report.getSeverity());
        assertEquals("userCallbackSetSeverity", report.getSeverityReason().getType());
    }

    @Test
    public void handledTypeMismatchExceptionUserSeverity() {
        callHandledTypeMismatchExceptionUserSeverityEndpoint();

        Report report = verifyAndGetReport(delivery);

        assertFalse(report.getUnhandled());
        assertEquals("warning", report.getSeverity());
        assertEquals("userSpecifiedSeverity", report.getSeverityReason().getType());
        assertThat(report.getSeverityReason().getAttributes(), is(Collections.EMPTY_MAP));
    }

    @Test
    public void handledTypeMismatchExceptionCallbackSeverity() {
        callHandledTypeMismatchExceptionCallbackSeverityEndpoint();

        Report report = verifyAndGetReport(delivery);

        assertFalse(report.getUnhandled());
        assertEquals("warning", report.getSeverity());
        assertEquals("userCallbackSetSeverity", report.getSeverityReason().getType());
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

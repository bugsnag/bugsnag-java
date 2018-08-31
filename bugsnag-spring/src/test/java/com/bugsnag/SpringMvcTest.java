package com.bugsnag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.bugsnag.HandledState.SeverityReasonType;

import com.bugsnag.callbacks.Callback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class SpringMvcTest {

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    /**
     * Configures a mock MVC instance and a Bugsnag spy.
     */
    @Before
    public void setUp() {
        // Create mock MVC with a real application context to allow the test configuration
        // to be loaded.
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Mockito.reset(bugsnag);
        bugsnag.getConfig().setSendUncaughtExceptions(true);
        bugsnag.getConfig().setAutoCaptureSessions(true);
    }

    @Test
    public void bugsnagNotifyWhenUncaughtControllerException() throws java.lang.Exception {
        ArgumentCaptor<RuntimeException> exceptionCaptor =
                ArgumentCaptor.forClass(RuntimeException.class);
        ArgumentCaptor<HandledState> handledStateCaptor =
                ArgumentCaptor.forClass(HandledState.class);

        callRuntimeExceptionEndpoint();

        // Check that bugsnag notify was called once
        verify(bugsnag, times(1)).notify(
                exceptionCaptor.capture(),
                handledStateCaptor.capture());

        // Assert that the exception was detected correctly
        assertEquals("Test", exceptionCaptor.getValue().getMessage());

        // Assert that the severity, severity reason and unhandled values are correct
        HandledState handledState = handledStateCaptor.getValue();
        assertEquals(Severity.ERROR, handledState.getCurrentSeverity());
        assertEquals(
                SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE,
                handledState.getSeverityReasonType());
        assertThat(
                handledState.getSeverityReasonAttributes(),
                is(Collections.singletonMap("framework", "Spring")));
        assertTrue(handledState.isUnhandled());
    }

    @Test
    public void noBugsnagNotifyWhenSendUncaughtExceptionsFalse() throws java.lang.Exception {
        bugsnag.getConfig().setSendUncaughtExceptions(false);

        callRuntimeExceptionEndpoint();

        // Check that bugsnag notify was not called
        verify(bugsnag, times(0)).notify(any(Throwable.class), any(HandledState.class));
    }

    @Test
    public void bugsnagSessionStartedWhenAutoCaptureSessions() throws java.lang.Exception {
        callRuntimeExceptionEndpoint();

        verify(bugsnag, times(1)).startSession();
    }

    @Test
    public void noBugsnagSessionStartedWhenAutoCaptureSessionsFalse() throws java.lang.Exception {
        bugsnag.getConfig().setAutoCaptureSessions(false);

        callRuntimeExceptionEndpoint();

        verify(bugsnag, times(0)).startSession();
    }

    @Test
    public void requestMetadataSetCorrectly() throws java.lang.Exception {
        callRuntimeExceptionEndpoint();

        // Capture the report object which will hold the metadata
        Report report = getReport();

        // Check that the context is set to the HTTP method and URI of the endpoint
        assertEquals("GET /throw-runtime-exception", report.getContext());

        // Check that the request metadata is set as expected
        @SuppressWarnings(value = "unchecked") Map<String, Object> requestMetadata =
                (Map<String, Object>) report.getMetaData().get("request");
        assertEquals("http://localhost/throw-runtime-exception", requestMetadata.get("url"));
        assertEquals("GET", requestMetadata.get("method"));
        assertEquals("127.0.0.1", requestMetadata.get("clientIp"));

        // Assert that the request params are as expected
        @SuppressWarnings(value = "unchecked") Map<String, String[]> params =
                (Map<String, String[]>) requestMetadata.get("params");
        assertEquals("paramVal1", params.get("param1")[0]);
        assertEquals("paramVal2", params.get("param2")[0]);

        // Assert that the request headers are as expected
        @SuppressWarnings(value = "unchecked") Map<String, String> headers =
                (Map<String, String>) requestMetadata.get("headers");
        assertEquals("headerVal1", headers.get("header1"));
        assertEquals("headerVal2", headers.get("header2"));
    }

    @Test
    public void springVersionSetCorrectly() throws java.lang.Exception {
        callRuntimeExceptionEndpoint();

        // Capture the report object which will hold the metadata
        Report report = getReport();

        // Check that the Spring version is set as expected
        @SuppressWarnings(value = "unchecked") Map<String, Object> deviceMetadata =
                (Map<String, Object>) report.getMetaData().get("device");
        assertEquals(SpringVersion.getVersion(), deviceMetadata.get("springVersion"));
    }

    @Test
    public void unhandledTypeMismatchExceptionSeverityInfo() throws java.lang.Exception {
        callUnhandledTypeMismatchExceptionEndpoint();

        Report report = getReport();

        assertTrue(report.getUnhandled());
        assertEquals("info", report.getSeverity());
        assertEquals("exceptionClass", report.getSeverityReason().getType());
        assertThat(report.getSeverityReason().getAttributes(),
                is(Collections.singletonMap("exceptionClass", "TypeMismatchException")));
    }

    @Test
    public void handledTypeMismatchExceptionSeverityInfo() throws java.lang.Exception {
        callHandledTypeMismatchExceptionEndpoint();

        Report report = getReport();

        assertFalse(report.getUnhandled());
        assertEquals("info", report.getSeverity());
        assertEquals("exceptionClass", report.getSeverityReason().getType());
        assertThat(report.getSeverityReason().getAttributes(),
                is(Collections.singletonMap("exceptionClass", "TypeMismatchException")));
    }

    private void callUnhandledTypeMismatchExceptionEndpoint() throws java.lang.Exception {
        ResultActions result = mockMvc.perform(get("/throw-type-mismatch-exception"));
    }

    private void callHandledTypeMismatchExceptionEndpoint() throws java.lang.Exception {
        ResultActions result = mockMvc.perform(get("/handled-type-mismatch-exception"));
    }

    private void callRuntimeExceptionEndpoint() throws java.lang.Exception {
        try {
            // Call controller that will throw an exception
            // Set some example params and headers
            ResultActions result = mockMvc.perform(
                    get("/throw-runtime-exception?param1=paramVal1&param2=paramVal2")
                    .header("header1", "headerVal1")
                    .header("header2", "headerVal2"));
            throw new RuntimeException("Test controller should have thrown exception");
        } catch (NestedServletException ex) {
            // Expect an exception
        }
    }

    private Report getReport() {
        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(bugsnag, times(1)).notify(reportCaptor.capture(), (Callback) isNull());
        return reportCaptor.getValue();
    }
}

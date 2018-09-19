package com.bugsnag;

import static com.bugsnag.TestUtils.verifyAndGetReport;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bugsnag.HandledState.SeverityReasonType;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.testapp.springboot.TestSpringBootApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ErrorHandler;

import java.util.Collections;

/**
 * Test that a Spring Boot application configured with the
 * {@link BugsnagSpringConfiguration} performs as expected.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestSpringBootApplication.class,
        webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringScheduledTaskTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Bugsnag bugsnag;

    @MockBean
    private ErrorHandler mockErrorHandler;

    private Delivery delivery;

    /**
     * Initialize test state
     */
    @Before
    public void setUp() {
        delivery = mock(Delivery.class);
        bugsnag.setDelivery(delivery);
    }

    @Test
    public void bugsnagNotifyWhenScheduledTaskException() {
        this.restTemplate.getForEntity("/execute-scheduled-task", String.class);

        Report report = verifyAndGetReport(delivery);

        // Assert that the exception was detected correctly
        assertEquals("Scheduled test", report.getExceptionMessage());
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

        // Assert that the exception is passed to an existing exception handler
        ArgumentCaptor<RuntimeException> exceptionCaptor =
                ArgumentCaptor.forClass(RuntimeException.class);
        verify(mockErrorHandler, times(1)).handleError(exceptionCaptor.capture());
        assertEquals("Scheduled test", exceptionCaptor.getValue().getMessage());
    }
}

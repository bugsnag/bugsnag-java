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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ErrorHandler;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Test that a Spring Boot application configured with the
 * {@link BugsnagSpringJakartaConfiguration} performs as expected.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestSpringBootApplication.class)
public class SpringScheduledTaskTest {

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

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
    public void bugsnagNotifyWhenScheduledTaskException()
            throws ExecutionException, InterruptedException {

        // The task to schedule
        Runnable exampleRunnable = new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Scheduled test");
            }
        };

        // Run the task now and wait for it to finish
        scheduler.submit(exampleRunnable).get();

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

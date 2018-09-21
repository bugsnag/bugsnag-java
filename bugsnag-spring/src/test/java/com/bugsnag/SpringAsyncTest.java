package com.bugsnag;

import static com.bugsnag.TestUtils.verifyAndGetReport;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.bugsnag.HandledState.SeverityReasonType;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.testapp.springboot.AsyncService;
import com.bugsnag.testapp.springboot.TestSpringBootApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

/**
 * Test that a Spring Boot application configured with the
 * {@link BugsnagSpringConfiguration} performs as expected.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestSpringBootApplication.class)
public class SpringAsyncTest {

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private AsyncService asyncService;

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
    public void bugsnagNotifyWhenAsyncException() {
        asyncService.throwException();

        Report report = verifyAndGetReport(delivery);

        // Assert that the exception was detected correctly
        assertEquals("Async test", report.getExceptionMessage());
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
}

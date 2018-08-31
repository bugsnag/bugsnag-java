package com.bugsnag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.bugsnag.HandledState.SeverityReasonType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class BugsnagHandlerExceptionResolverTest {

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private Configuration config;

    /**
     * Configures a mock MVC instance and a Bugsnag mock.
     */
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Mockito.reset(bugsnag);
        config = new Configuration("apiKey");
        config.setSendUncaughtExceptions(true);
        when(bugsnag.getConfig()).thenReturn(config);
    }

    @Test
    public void uncaughtControllerErrorCausesBugsnagNotify() throws java.lang.Exception {
        ArgumentCaptor<RuntimeException> exceptionCaptor =
                ArgumentCaptor.forClass(RuntimeException.class);
        ArgumentCaptor<HandledState> handledStateCaptor =
                ArgumentCaptor.forClass(HandledState.class);
        try {
            // Call controller that will throw an exception
            mockMvc.perform(get("/test"));
            throw new RuntimeException("Test controller should have thrown exception");
        } catch (NestedServletException ex) {
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
    }
}

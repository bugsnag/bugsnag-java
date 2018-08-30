package com.bugsnag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.bugsnag.HandledState.SeverityReasonType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;

public class BugsnagHandlerExceptionResolverTest {

    private MockMvc mockMvc;

    private Bugsnag bugsnag = mock(Bugsnag.class);

    private Configuration config = new Configuration("apiKey");

    @Controller
    private class TestController {
        @RequestMapping("/test")
        public String test() {
            throw new RuntimeException("Test");
        }
    }

    /**
     * Configures a mock MVC instance and a Bugsnag mock.
     */
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setHandlerExceptionResolvers(new BugsnagHandlerExceptionResolver(bugsnag))
                .build();

        config.setSendUncaughtExceptions(true);
        when(bugsnag.getConfig()).thenReturn(config);
    }

    @Test
    public void test() throws java.lang.Exception {
        try {
            mockMvc.perform(get("/test"));
        } catch (NestedServletException ex) {
            ArgumentCaptor<RuntimeException> exceptionCaptor =
                    ArgumentCaptor.forClass(RuntimeException.class);
            ArgumentCaptor<HandledState> handledStateCaptor =
                    ArgumentCaptor.forClass(HandledState.class);
            verify(bugsnag, times(1)).notify(
                    exceptionCaptor.capture(),
                    handledStateCaptor.capture());
            HandledState handledState = handledStateCaptor.getValue();
            assertEquals("Test", exceptionCaptor.getValue().getMessage());
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

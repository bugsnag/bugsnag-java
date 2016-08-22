package com.bugsnag;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

@RunWith(JMockit.class)
public class BugsnagLoggerTest {
    @Test
    public void testShouldNotLogTraceOrDebug(@Mocked Client client) {
        Logger logger = new BugsnagLogger(client);

        assertThat(logger.isTraceEnabled(), is(false));
        assertThat(logger.isDebugEnabled(), is(false));
    }

    @Test
    public void testShouldLogInfoAndHigher(@Mocked Client client) {
        Logger logger = new BugsnagLogger(client);

        assertThat(logger.isInfoEnabled(), is(true));
        assertThat(logger.isWarnEnabled(), is(true));
        assertThat(logger.isErrorEnabled(), is(true));
    }

    @Test
    public void testInfoLoggingCallsTheClient(@Mocked final Client client) {
        final Logger logger = new BugsnagLogger(client);

        final Throwable e = new Exception();

        new Expectations() {{
            client.notify(e, "info"); times = 1;
        }};

        logger.info("", e);
    }

    @Test
    public void testWarnLoggingCallsTheClient(@Mocked final Client client) {
        final Logger logger = new BugsnagLogger(client);

        final Throwable e = new Exception();

        new Expectations() {{
            client.notify(e, "warning"); times = 1;
        }};

        logger.warn("", e);
    }

    @Test
    public void testErrorLoggingCallsTheClient(@Mocked final Client client) {
        final Logger logger = new BugsnagLogger(client);

        final Throwable e = new Exception();

        new Expectations() {{
            client.notify(e, "error"); times = 1;
        }};

        logger.error("", e);
    }
}
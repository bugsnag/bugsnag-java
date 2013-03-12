package com.bugsnag;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;

public class ErrorTest {
    @Test
    public void testShouldIgnore() {
        Client bugsnag = new Client("apikey");
        bugsnag.setIgnoreClasses("java.io.IOException");

        Error error = bugsnag.createError(new java.io.IOException("Test"), null);
        assertThat(error.shouldIgnore(), is(true));

        error = bugsnag.createError(new RuntimeException("Test"), null);
        assertThat(error.shouldIgnore(), is(false));
    }
}
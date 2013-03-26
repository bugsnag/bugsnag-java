package com.bugsnag;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;

public class ErrorTest {
    @Test
    public void testShouldIgnore() {
        Client bugsnag = new Client("apikey");
        bugsnag.setIgnoreClasses("java.io.IOException");

        assertThat(bugsnag.shouldIgnore(new java.io.IOException("Test")), is(true));

        assertThat(bugsnag.shouldIgnore(new RuntimeException("Test")), is(false));
    }
}
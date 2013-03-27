package com.bugsnag;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;

public class ConfigurationTest {
    @Test
    public void testshouldNotifyDefault() {
        Client bugsnag = new Client("apikey");

        assertThat(bugsnag.config.shouldNotify(), is(true));
    }

    @Test
    public void testshouldNotifyProduction() {
        Client bugsnag = new Client("apikey");
        bugsnag.setNotifyReleaseStages("production");
        bugsnag.setReleaseStage("production");

        assertThat(bugsnag.config.shouldNotify(), is(true));
    }

    @Test
    public void testshouldNotNotifyDevelopment() {
        Client bugsnag = new Client("apikey");
        bugsnag.setNotifyReleaseStages("production");
        bugsnag.setReleaseStage("development");

        assertThat(bugsnag.config.shouldNotify(), is(false));
    }
}
package com.bugsnag;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for feature flags in Bugsnag client
 */
public class BugsnagFeatureFlagTest {

    private Bugsnag bugsnag;

    @Before
    public void setUp() {
        bugsnag = new Bugsnag("api-key", false);
    }

    @After
    public void tearDown() {
        bugsnag.close();
    }

    @Test
    public void testAddFeatureFlag() {
        bugsnag.addFeatureFlag("flag1", "variant-a");

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("variant-a", flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlagWithoutVariant() {
        bugsnag.addFeatureFlag("flag1");

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals(null, flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlags() {
        List<FeatureFlag> flagsToAdd = new ArrayList<FeatureFlag>();
        flagsToAdd.add(new FeatureFlag("flag1", "variant-a"));
        flagsToAdd.add(new FeatureFlag("flag2", "variant-b"));

        bugsnag.addFeatureFlags(flagsToAdd);

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(2, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());
    }

    @Test
    public void testClearFeatureFlag() {
        bugsnag.addFeatureFlag("flag1", "variant-a");
        bugsnag.addFeatureFlag("flag2", "variant-b");
        bugsnag.clearFeatureFlag("flag1");

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag2", flags.get(0).getName());
    }

    @Test
    public void testClearFeatureFlags() {
        bugsnag.addFeatureFlag("flag1", "variant-a");
        bugsnag.addFeatureFlag("flag2", "variant-b");
        bugsnag.clearFeatureFlags();

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(0, flags.size());
    }

    @Test
    public void testClientFlagsInheritFromConfiguration() {
        Configuration config = bugsnag.getConfig();
        config.addFeatureFlag("config-flag", "config-variant");

        Bugsnag client = new Bugsnag("api-key", false);
        client.getConfig().addFeatureFlag("config-flag", "config-variant");

        Report report = client.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("config-flag", flags.get(0).getName());
        assertEquals("config-variant", flags.get(0).getVariant());

        client.close();
    }

    @Test
    public void testClientFlagsOverrideConfigurationFlags() {
        Configuration config = bugsnag.getConfig();
        config.addFeatureFlag("flag1", "config-variant");

        Bugsnag client = new Bugsnag("api-key", false);
        client.getConfig().addFeatureFlag("flag1", "config-variant");
        client.addFeatureFlag("flag1", "client-variant");

        Report report = client.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("client-variant", flags.get(0).getVariant());

        client.close();
    }

    @Test
    public void testFeatureFlagOrderPreservedAcrossScopes() {
        Configuration config = bugsnag.getConfig();
        config.addFeatureFlag("flag1", "config-variant");
        config.addFeatureFlag("flag2", "config-variant");

        Bugsnag client = new Bugsnag("api-key", false);
        client.getConfig().addFeatureFlag("flag1", "config-variant");
        client.getConfig().addFeatureFlag("flag2", "config-variant");
        client.addFeatureFlag("flag3", "client-variant");

        Report report = client.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(3, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());
        assertEquals("flag3", flags.get(2).getName());

        client.close();
    }
}

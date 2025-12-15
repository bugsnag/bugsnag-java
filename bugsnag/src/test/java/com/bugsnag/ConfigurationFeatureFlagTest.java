package com.bugsnag;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for feature flags in Configuration
 */
public class ConfigurationFeatureFlagTest {

    private Configuration config;

    @Before
    public void setUp() {
        config = new Configuration("api-key");
    }

    @Test
    public void testAddFeatureFlag() {
        config.addFeatureFlag("flag1", "variant-a");

        // Verify by creating a client and checking the flags are inherited
        Bugsnag bugsnag = new Bugsnag("api-key");
        bugsnag.getConfig().addFeatureFlag("flag1", "variant-a");

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("variant-a", flags.get(0).getVariant());

        bugsnag.close();
    }

    @Test
    public void testAddFeatureFlagWithoutVariant() {
        config.addFeatureFlag("flag1");

        Bugsnag bugsnag = new Bugsnag("api-key");
        bugsnag.getConfig().addFeatureFlag("flag1");

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals(null, flags.get(0).getVariant());

        bugsnag.close();
    }

    @Test
    public void testAddFeatureFlags() {
        List<FeatureFlag> flagsToAdd = new ArrayList<FeatureFlag>();
        flagsToAdd.add(new FeatureFlag("flag1", "variant-a"));
        flagsToAdd.add(new FeatureFlag("flag2", "variant-b"));

        config.addFeatureFlags(flagsToAdd);

        Bugsnag bugsnag = new Bugsnag("api-key");
        bugsnag.getConfig().addFeatureFlags(flagsToAdd);

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(2, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());

        bugsnag.close();
    }

    @Test
    public void testClearFeatureFlag() {
        config.addFeatureFlag("flag1", "variant-a");
        config.addFeatureFlag("flag2", "variant-b");
        config.clearFeatureFlag("flag1");

        Bugsnag bugsnag = new Bugsnag("api-key");
        bugsnag.getConfig().addFeatureFlag("flag1", "variant-a");
        bugsnag.getConfig().addFeatureFlag("flag2", "variant-b");
        bugsnag.getConfig().clearFeatureFlag("flag1");

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag2", flags.get(0).getName());

        bugsnag.close();
    }

    @Test
    public void testClearFeatureFlags() {
        config.addFeatureFlag("flag1", "variant-a");
        config.addFeatureFlag("flag2", "variant-b");
        config.clearFeatureFlags();

        Bugsnag bugsnag = new Bugsnag("api-key");
        bugsnag.getConfig().addFeatureFlag("flag1", "variant-a");
        bugsnag.getConfig().addFeatureFlag("flag2", "variant-b");
        bugsnag.getConfig().clearFeatureFlags();

        Report report = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = report.getFeatureFlags();

        assertEquals(0, flags.size());

        bugsnag.close();
    }
}

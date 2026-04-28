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

        // Verify the config has the flag
        BugsnagEvent event = new BugsnagEvent(config, new RuntimeException("Test"));
        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("variant-a", flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlagWithoutVariant() {
        config.addFeatureFlag("flag1");

        BugsnagEvent event = new BugsnagEvent(config, new RuntimeException("Test"));
        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals(null, flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlags() {
        List<FeatureFlag> flagsToAdd = new ArrayList<FeatureFlag>();
        flagsToAdd.add(FeatureFlag.of("flag1", "variant-a"));
        flagsToAdd.add(FeatureFlag.of("flag2", "variant-b"));

        config.addFeatureFlags(flagsToAdd);

        BugsnagEvent event = new BugsnagEvent(config, new RuntimeException("Test"));
        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(2, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());
    }

    @Test
    public void testClearFeatureFlag() {
        config.addFeatureFlag("flag1", "variant-a");
        config.addFeatureFlag("flag2", "variant-b");
        config.clearFeatureFlag("flag1");

        BugsnagEvent event = new BugsnagEvent(config, new RuntimeException("Test"));
        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag2", flags.get(0).getName());
    }

    @Test
    public void testClearFeatureFlags() {
        config.addFeatureFlag("flag1", "variant-a");
        config.addFeatureFlag("flag2", "variant-b");
        config.clearFeatureFlags();

        BugsnagEvent event = new BugsnagEvent(config, new RuntimeException("Test"));
        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(0, flags.size());
    }
}

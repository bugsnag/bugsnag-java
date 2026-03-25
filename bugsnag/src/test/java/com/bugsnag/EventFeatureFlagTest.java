package com.bugsnag;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for feature flags in Report (Event)
 */
public class EventFeatureFlagTest {

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
    public void testAddFeatureFlagOnReport() {
        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("report-flag", "report-variant");

        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("report-flag", flags.get(0).getName());
        assertEquals("report-variant", flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlagWithoutVariant() {
        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("report-flag");

        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("report-flag", flags.get(0).getName());
        assertEquals(null, flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlags() {
        List<FeatureFlag> flagsToAdd = new ArrayList<FeatureFlag>();
        flagsToAdd.add(FeatureFlag.of("flag1", "variant-a"));
        flagsToAdd.add(FeatureFlag.of("flag2", "variant-b"));

        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlags(flagsToAdd);

        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(2, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());
    }

    @Test
    public void testClearFeatureFlag() {
        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("flag1", "variant-a");
        event.addFeatureFlag("flag2", "variant-b");
        event.clearFeatureFlag("flag1");

        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag2", flags.get(0).getName());
    }

    @Test
    public void testClearFeatureFlags() {
        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("flag1", "variant-a");
        event.addFeatureFlag("flag2", "variant-b");
        event.clearFeatureFlags();

        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(0, flags.size());
    }

    @Test
    public void testReportFlagsInheritFromClient() {
        bugsnag.addFeatureFlag("client-flag", "client-variant");

        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("client-flag", flags.get(0).getName());
        assertEquals("client-variant", flags.get(0).getVariant());
    }

    @Test
    public void testReportFlagsOverrideClientFlags() {
        bugsnag.addFeatureFlag("flag1", "client-variant");

        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("flag1", "report-variant");

        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("report-variant", flags.get(0).getVariant());
    }

    @Test
    public void testFeatureFlagOrderAcrossAllScopes() {
        // Add flags to configuration
        bugsnag.getConfig().addFeatureFlag("flag1", "config-variant");
        bugsnag.getConfig().addFeatureFlag("flag2", "config-variant");

        // Add flags to client (one new, one override)
        bugsnag.addFeatureFlag("flag2", "client-variant");
        bugsnag.addFeatureFlag("flag3", "client-variant");

        // Add flags to report (one new, one override)
        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("flag3", "report-variant");
        event.addFeatureFlag("flag4", "report-variant");

        List<FeatureFlag> flags = event.getFeatureFlags();

        // Should have all 4 flags in the order they were first added
        assertEquals(4, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("config-variant", flags.get(0).getVariant());
        assertEquals("flag2", flags.get(1).getName());
        assertEquals("client-variant", flags.get(1).getVariant());
        assertEquals("flag3", flags.get(2).getName());
        assertEquals("report-variant", flags.get(2).getVariant());
        assertEquals("flag4", flags.get(3).getName());
        assertEquals("report-variant", flags.get(3).getVariant());
    }

    @Test
    public void testClearAndReAddChangesPosition() {
        bugsnag.getConfig().addFeatureFlag("flag1", "value1");
        bugsnag.getConfig().addFeatureFlag("flag2", "value2");
        bugsnag.getConfig().clearFeatureFlag("flag1");

        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("flag1", "value1-readded");

        List<FeatureFlag> flags = event.getFeatureFlags();

        // flag1 should now be at the end since it was removed and re-added
        assertEquals(2, flags.size());
        assertEquals("flag2", flags.get(0).getName());
        assertEquals("flag1", flags.get(1).getName());
        assertEquals("value1-readded", flags.get(1).getVariant());
    }

    @Test
    public void testFeatureFlagChaining() {
        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));

        event.addFeatureFlag("flag1", "variant-a")
              .addFeatureFlag("flag2", "variant-b")
              .addFeatureFlag("flag3");

        List<FeatureFlag> flags = event.getFeatureFlags();

        assertEquals(3, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());
        assertEquals("flag3", flags.get(2).getName());
    }

    @Test
    public void testMultipleScopesMaintainInsertionOrder() {
        // Config adds flag1 and flag2
        bugsnag.getConfig().addFeatureFlag("flag1", "value1");
        bugsnag.getConfig().addFeatureFlag("flag2", "value2");

        // Note: clearing flag from client doesn't remove it from config
        // It only affects the client's own feature flag store
        // When building a report, config flags are copied first

        // Report adds flag1 with updated value (overrides config value but keeps position)
        // and adds flag2 with updated value
        BugsnagEvent event = bugsnag.buildReport(new RuntimeException("Test"));
        event.addFeatureFlag("flag1", "value1-updated");
        event.addFeatureFlag("flag2", "value2-updated");

        List<FeatureFlag> flags = event.getFeatureFlags();

        // Both flags should maintain their original order from config
        assertEquals(2, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("value1-updated", flags.get(0).getVariant());
        assertEquals("flag2", flags.get(1).getName());
        assertEquals("value2-updated", flags.get(1).getVariant());
    }
}

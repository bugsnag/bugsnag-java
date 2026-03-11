package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for FeatureFlag
 */
public class FeatureFlagTest {

    @Test
    public void testFeatureFlagWithVariant() {
        FeatureFlag flag = FeatureFlag.of("test-flag", "variant-a");
        assertEquals("test-flag", flag.getName());
        assertEquals("variant-a", flag.getVariant());
    }

    @Test
    public void testFeatureFlagWithoutVariant() {
        FeatureFlag flag = FeatureFlag.of("test-flag");
        assertEquals("test-flag", flag.getName());
        assertNull(flag.getVariant());
    }

    @Test
    public void testFeatureFlagWithNullVariant() {
        FeatureFlag flag = FeatureFlag.of("test-flag", null);
        assertEquals("test-flag", flag.getName());
        assertNull(flag.getVariant());
    }

    @Test
    public void testFeatureFlagWithNullName() {
        assertNull(FeatureFlag.of(null));
    }

    @Test
    public void testFeatureFlagWithEmptyName() {
        assertNull(FeatureFlag.of(""));
    }

    @Test
    public void testFeatureFlagEquals() {
        FeatureFlag flag1 = FeatureFlag.of("test", "variant-a");
        FeatureFlag flag2 = FeatureFlag.of("test", "variant-a");
        FeatureFlag flag3 = FeatureFlag.of("test", "variant-b");
        FeatureFlag flag4 = FeatureFlag.of("other", "variant-a");

        assertEquals(flag1, flag2);
        assertTrue(!flag1.equals(flag3));
        assertTrue(!flag1.equals(flag4));
    }

    @Test
    public void testFeatureFlagHashCode() {
        FeatureFlag flag1 = FeatureFlag.of("test", "variant-a");
        FeatureFlag flag2 = FeatureFlag.of("test", "variant-a");
        assertEquals(flag1.hashCode(), flag2.hashCode());
    }

    @Test
    public void testFeatureFlagToString() {
        FeatureFlag flag = FeatureFlag.of("test-flag", "variant-a");
        String result = flag.toString();
        assertTrue(result.contains("test-flag"));
        assertTrue(result.contains("variant-a"));
    }
}

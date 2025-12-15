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
        FeatureFlag flag = new FeatureFlag("test-flag", "variant-a");
        assertEquals("test-flag", flag.getName());
        assertEquals("variant-a", flag.getVariant());
    }

    @Test
    public void testFeatureFlagWithoutVariant() {
        FeatureFlag flag = new FeatureFlag("test-flag");
        assertEquals("test-flag", flag.getName());
        assertNull(flag.getVariant());
    }

    @Test
    public void testFeatureFlagWithNullVariant() {
        FeatureFlag flag = new FeatureFlag("test-flag", null);
        assertEquals("test-flag", flag.getName());
        assertNull(flag.getVariant());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFeatureFlagWithNullName() {
        new FeatureFlag(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFeatureFlagWithEmptyName() {
        new FeatureFlag("");
    }

    @Test
    public void testFeatureFlagEquals() {
        FeatureFlag flag1 = new FeatureFlag("test", "variant-a");
        FeatureFlag flag2 = new FeatureFlag("test", "variant-a");
        FeatureFlag flag3 = new FeatureFlag("test", "variant-b");
        FeatureFlag flag4 = new FeatureFlag("other", "variant-a");

        assertEquals(flag1, flag2);
        assertTrue(!flag1.equals(flag3));
        assertTrue(!flag1.equals(flag4));
    }

    @Test
    public void testFeatureFlagHashCode() {
        FeatureFlag flag1 = new FeatureFlag("test", "variant-a");
        FeatureFlag flag2 = new FeatureFlag("test", "variant-a");
        assertEquals(flag1.hashCode(), flag2.hashCode());
    }

    @Test
    public void testFeatureFlagToString() {
        FeatureFlag flag = new FeatureFlag("test-flag", "variant-a");
        String result = flag.toString();
        assertTrue(result.contains("test-flag"));
        assertTrue(result.contains("variant-a"));
    }
}

package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for FeatureFlagStore
 */
public class FeatureFlagStoreTest {

    private FeatureFlagStore store;

    @Before
    public void setUp() {
        store = new FeatureFlagStore();
    }

    @Test
    public void testAddFeatureFlag() {
        store.addFeatureFlag("flag1", "variant-a");
        List<FeatureFlag> flags = store.toList();
        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("variant-a", flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlagWithNullVariant() {
        store.addFeatureFlag("flag1", null);
        List<FeatureFlag> flags = store.toList();
        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals(null, flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlagUpdatesVariant() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag1", "variant-b");
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(1, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("variant-b", flags.get(0).getVariant());
    }

    @Test
    public void testAddFeatureFlagMaintainsOrder() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag2", "variant-b");
        store.addFeatureFlag("flag3", "variant-c");
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(3, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());
        assertEquals("flag3", flags.get(2).getName());
    }

    @Test
    public void testAddFeatureFlagUpdateDoesNotChangeOrder() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag2", "variant-b");
        store.addFeatureFlag("flag1", "variant-updated");
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(2, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("variant-updated", flags.get(0).getVariant());
        assertEquals("flag2", flags.get(1).getName());
    }

    @Test
    public void testAddFeatureFlags() {
        List<FeatureFlag> flagsToAdd = new ArrayList<FeatureFlag>();
        flagsToAdd.add(new FeatureFlag("flag1", "variant-a"));
        flagsToAdd.add(new FeatureFlag("flag2", "variant-b"));
        
        store.addFeatureFlags(flagsToAdd);
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(2, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("flag2", flags.get(1).getName());
    }

    @Test
    public void testClearFeatureFlag() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag2", "variant-b");
        
        store.clearFeatureFlag("flag1");
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(1, flags.size());
        assertEquals("flag2", flags.get(0).getName());
    }

    @Test
    public void testClearFeatureFlagAndReAdd() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag2", "variant-b");
        
        store.clearFeatureFlag("flag1");
        store.addFeatureFlag("flag1", "variant-updated");
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(2, flags.size());
        assertEquals("flag2", flags.get(0).getName());
        assertEquals("flag1", flags.get(1).getName());
    }

    @Test
    public void testClearFeatureFlags() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag2", "variant-b");
        
        store.clearFeatureFlags();
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(0, flags.size());
    }

    @Test
    public void testCopy() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag2", "variant-b");
        
        FeatureFlagStore copy = store.copy();
        
        List<FeatureFlag> originalFlags = store.toList();
        List<FeatureFlag> copiedFlags = copy.toList();
        
        assertEquals(originalFlags.size(), copiedFlags.size());
        assertEquals("flag1", copiedFlags.get(0).getName());
        assertEquals("flag2", copiedFlags.get(1).getName());
        
        // Verify that modifying the copy doesn't affect the original
        copy.addFeatureFlag("flag3", "variant-c");
        assertEquals(2, store.toList().size());
        assertEquals(3, copy.toList().size());
    }

    @Test
    public void testMerge() {
        store.addFeatureFlag("flag1", "variant-a");
        store.addFeatureFlag("flag2", "variant-b");
        
        FeatureFlagStore other = new FeatureFlagStore();
        other.addFeatureFlag("flag2", "variant-updated");
        other.addFeatureFlag("flag3", "variant-c");
        
        store.merge(other);
        
        List<FeatureFlag> flags = store.toList();
        assertEquals(3, flags.size());
        assertEquals("flag1", flags.get(0).getName());
        assertEquals("variant-a", flags.get(0).getVariant());
        assertEquals("flag2", flags.get(1).getName());
        assertEquals("variant-updated", flags.get(1).getVariant());
        assertEquals("flag3", flags.get(2).getName());
        assertEquals("variant-c", flags.get(2).getVariant());
    }

    @Test
    public void testSize() {
        assertEquals(0, store.size());
        
        store.addFeatureFlag("flag1", "variant-a");
        assertEquals(1, store.size());
        
        store.addFeatureFlag("flag2", "variant-b");
        assertEquals(2, store.size());
        
        store.clearFeatureFlag("flag1");
        assertEquals(1, store.size());
        
        store.clearFeatureFlags();
        assertEquals(0, store.size());
    }
}

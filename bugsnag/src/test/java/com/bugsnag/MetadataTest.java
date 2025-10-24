package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.Map;

public class MetadataTest {

    @Test
    public void testEmptyMetadata() {
        Metadata metadata = new Metadata();
        assertEquals(0, metadata.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleTabSingleValue() {
        Metadata metadata = new Metadata();
        metadata.addToTab("tab-name", "key-1", "value-1");

        assertEquals(1, metadata.size());
        assertEquals(1, ((Map<String, Object>) metadata.get("tab-name")).size());
        assertEquals("value-1", ((Map<String, Object>) metadata.get("tab-name")).get("key-1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleTabMultipleValues() {
        Metadata metadata = new Metadata();
        metadata.addToTab("tab-name", "key-1", "value-1");
        metadata.addToTab("tab-name", "key-2", "value-2");

        assertEquals(1, metadata.size());
        assertEquals(2, ((Map<String, Object>) metadata.get("tab-name")).size());
        assertEquals("value-1", ((Map<String, Object>) metadata.get("tab-name")).get("key-1"));
        assertEquals("value-2", ((Map<String, Object>) metadata.get("tab-name")).get("key-2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleTabs() {
        Metadata metadata = new Metadata();
        metadata.addToTab("tab-name-1", "key-1", "value-1");
        metadata.addToTab("tab-name-2", "key-1", "value-1");

        assertEquals(2, metadata.size());
        assertEquals(1, ((Map<String, Object>) metadata.get("tab-name-1")).size());
        assertEquals(1, ((Map<String, Object>) metadata.get("tab-name-2")).size());
        assertEquals("value-1", ((Map<String, Object>) metadata.get("tab-name-1")).get("key-1"));
        assertEquals("value-1", ((Map<String, Object>) metadata.get("tab-name-1")).get("key-1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClearTab() {
        Metadata metadata = new Metadata();
        metadata.addToTab("tab-name-1", "key-1", "value-1");
        metadata.addToTab("tab-name-2", "key-1", "value-1");

        assertEquals(2, metadata.size());

        metadata.clearTab("tab-name-1");

        assertEquals(1, metadata.size());

        assertNull(metadata.get("tab-name-1"));
        assertEquals(1, ((Map<String, Object>) metadata.get("tab-name-2")).size());
    }
}

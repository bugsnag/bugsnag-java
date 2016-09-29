package com.bugsnag;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MetaDataTest {

    @Test
    public void testEmptyMetaData() {
        MetaData metaData = new MetaData();
        assertEquals(0, metaData.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleTabSingleValue() {
        MetaData metaData = new MetaData();
        metaData.addToTab("tab-name", "key-1", "value-1");

        assertEquals(1, metaData.size());
        assertEquals(1, ((Map<String, Object>)metaData.get("tab-name")).size());
        assertEquals("value-1", ((Map<String, Object>)metaData.get("tab-name")).get("key-1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleTabMultipleValues() {
        MetaData metaData = new MetaData();
        metaData.addToTab("tab-name", "key-1", "value-1");
        metaData.addToTab("tab-name", "key-2", "value-2");

        assertEquals(1, metaData.size());
        assertEquals(2, ((Map<String, Object>)metaData.get("tab-name")).size());
        assertEquals("value-1", ((Map<String, Object>)metaData.get("tab-name")).get("key-1"));
        assertEquals("value-2", ((Map<String, Object>)metaData.get("tab-name")).get("key-2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleTabs() {
        MetaData metaData = new MetaData();
        metaData.addToTab("tab-name-1", "key-1", "value-1");
        metaData.addToTab("tab-name-2", "key-1", "value-1");

        assertEquals(2, metaData.size());
        assertEquals(1, ((Map<String, Object>)metaData.get("tab-name-1")).size());
        assertEquals(1, ((Map<String, Object>)metaData.get("tab-name-2")).size());
        assertEquals("value-1", ((Map<String, Object>)metaData.get("tab-name-1")).get("key-1"));
        assertEquals("value-1", ((Map<String, Object>)metaData.get("tab-name-1")).get("key-1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClearTab() {
        MetaData metaData = new MetaData();
        metaData.addToTab("tab-name-1", "key-1", "value-1");
        metaData.addToTab("tab-name-2", "key-1", "value-1");

        assertEquals(2, metaData.size());

        metaData.clearTab("tab-name-1");

        assertEquals(1, metaData.size());

        assertNull(((Map<String, Object>)metaData.get("tab-name-1")));
        assertEquals(1, ((Map<String, Object>)metaData.get("tab-name-2")).size());
    }
}

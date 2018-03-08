package com.bugsnag.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilteredMapTest {

    private static final String KEY_UNFILTERED = "unfiltered";
    private static final String KEY_FILTERED = "auth";
    private static final String KEY_NESTED = "nested";
    private static final String VAL_UNFILTERED = "Foo";
    private static final String VAL_FILTERED = "Bar";
    private static final String PLACEHOLDER_FILTERED = "[FILTERED]";

    private Map<String, Object> filteredMap;

    @Before
    public void setUp() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UNFILTERED, VAL_UNFILTERED);
        map.put(KEY_FILTERED, VAL_FILTERED);

        HashMap<String, Object> nestedMap = new HashMap<String, Object>();
        nestedMap.put(KEY_UNFILTERED, VAL_UNFILTERED);
        nestedMap.put(KEY_FILTERED, VAL_FILTERED);
        map.put(KEY_NESTED, nestedMap);

        this.filteredMap = new FilteredMap(map, Collections.singleton(KEY_FILTERED));
    }

    @Test
    public void testSize() {
        assertEquals(3, filteredMap.size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertFalse(filteredMap.isEmpty());
        Map<String, Object> map = Collections.emptyMap();
        FilteredMap emptyMap = new FilteredMap(map, Collections.<String>emptyList());
        assertTrue(emptyMap.isEmpty());
    }

    @Test
    public void testClear() throws Exception {
        assertEquals(3, filteredMap.size());
        filteredMap.clear();
        assertTrue(filteredMap.isEmpty());
    }

    @Test
    public void testContainsKey() throws Exception {
        assertTrue(filteredMap.containsKey(KEY_FILTERED));
        assertTrue(filteredMap.containsKey(KEY_UNFILTERED));
        assertTrue(filteredMap.containsKey(KEY_NESTED));
        assertFalse(filteredMap.containsKey("fake"));
    }

    @Test
    public void testRemove() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UNFILTERED, VAL_UNFILTERED);
        map.put(KEY_FILTERED, VAL_FILTERED);

        HashMap<String, Object> emptyMap = new HashMap<String, Object>();
        Map<String, Object> removeMap = new FilteredMap(emptyMap, Collections.singleton(KEY_FILTERED));
        removeMap.putAll(map);

        assertEquals(2, removeMap.size());
        removeMap.remove(KEY_FILTERED);
        assertEquals(1, removeMap.size());
        removeMap.remove(KEY_UNFILTERED);
        assertEquals(0, removeMap.size());
    }

    @Test
    public void testGet() throws Exception {
        assertEquals(PLACEHOLDER_FILTERED, filteredMap.get(KEY_FILTERED));
        assertEquals(VAL_UNFILTERED, filteredMap.get(KEY_UNFILTERED));

        Object actual = filteredMap.get(KEY_NESTED);
        assertTrue(actual instanceof FilteredMap);

        Map<String, Object> nestedMap = (Map<String, Object>) actual;
        assertEquals(VAL_UNFILTERED, nestedMap.get(KEY_UNFILTERED));
        assertEquals(PLACEHOLDER_FILTERED, nestedMap.get(KEY_FILTERED));
    }

    @Test
    public void testKeySet() throws Exception {
        Set<String> keySet = filteredMap.keySet();
        assertEquals(3, keySet.size());
        assertTrue(keySet.contains(KEY_FILTERED));
        assertTrue(keySet.contains(KEY_UNFILTERED));
        assertTrue(keySet.contains(KEY_NESTED));
    }

    @Test
    public void testValues() throws Exception {
        Collection<Object> values = filteredMap.values();
        assertEquals(3, values.size());
        assertTrue(values.contains(VAL_UNFILTERED));
        assertTrue(values.contains(PLACEHOLDER_FILTERED));

        values.remove(PLACEHOLDER_FILTERED);
        values.remove(VAL_UNFILTERED);

        Object nestedObj = values.toArray(new Object[1])[0];
        assertTrue(nestedObj instanceof FilteredMap);
        Map<String, Object> nestedMap = (Map<String, Object>) nestedObj;
        values = nestedMap.values();

        assertEquals(2, values.size());
        assertTrue(values.contains(VAL_UNFILTERED));
        assertTrue(values.contains(PLACEHOLDER_FILTERED));
    }

    @Test
    public void testEntrySet() throws Exception {
        Set<Map.Entry<String, Object>> entries = filteredMap.entrySet();
        assertEquals(3, entries.size());

        int expectedCount = 0;

        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();

            if (key.equals(KEY_FILTERED)) {
                expectedCount++;
                assertEquals(PLACEHOLDER_FILTERED, entry.getValue());

            } else if (key.equals(KEY_UNFILTERED)) {
                expectedCount++;
                assertEquals(VAL_UNFILTERED, entry.getValue());

            } else if (key.equals(KEY_NESTED)) {
                expectedCount++;
                Object value = entry.getValue();
                assertTrue(value instanceof FilteredMap);
            }
        }
        assertEquals(3, expectedCount);
    }

}
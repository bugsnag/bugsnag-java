package com.bugsnag.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilteredMapTest {

    private static final String KEY_UNFILTERED = "unfiltered";
    private static final String KEY_FILTERED = "auth";
    private static final String KEY_NESTED = "nested";
    private static final String KEY_UNMODIFIABLE = "unmodifiable";
    private static final String VAL_UNFILTERED = "Foo";
    private static final String VAL_FILTERED = "Bar";
    private static final String PLACEHOLDER_FILTERED = "[FILTERED]";

    private Map<String, Object> filteredMap;

    /**
     * Creates a map with filtered, unfiltered, and nested values
     * @throws Exception an exception
     */
    @Before
    public void setUp() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UNFILTERED, VAL_UNFILTERED);
        map.put(KEY_FILTERED, VAL_FILTERED);

        HashMap<String, Object> nestedMap = new HashMap<String, Object>();
        nestedMap.put(KEY_UNFILTERED, VAL_UNFILTERED);
        nestedMap.put(KEY_FILTERED, VAL_FILTERED);
        map.put(KEY_NESTED, nestedMap);

        map.put(KEY_UNMODIFIABLE, Collections.unmodifiableMap(nestedMap));

        this.filteredMap = new FilteredMap(map, Collections.singleton(KEY_FILTERED));
    }

    @Test
    public void testSize() {
        assertEquals(4, filteredMap.size());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(filteredMap.isEmpty());
        Map<String, Object> map = Collections.emptyMap();
        FilteredMap emptyMap = new FilteredMap(map, Collections.<String>emptyList());
        assertTrue(emptyMap.isEmpty());
    }

    @Test
    public void testClear() {
        assertEquals(4, filteredMap.size());
        filteredMap.clear();
        assertTrue(filteredMap.isEmpty());
    }

    @Test
    public void testContainsKey() {
        assertTrue(filteredMap.containsKey(KEY_FILTERED));
        assertTrue(filteredMap.containsKey(KEY_UNFILTERED));
        assertTrue(filteredMap.containsKey(KEY_NESTED));
        assertFalse(filteredMap.containsKey("fake"));
    }

    @Test
    public void testRemove() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UNFILTERED, VAL_UNFILTERED);
        map.put(KEY_FILTERED, VAL_FILTERED);

        HashMap<String, Object> emptyMap = new HashMap<String, Object>();
        Set<String> filters = Collections.singleton(KEY_FILTERED);
        Map<String, Object> removeMap = new FilteredMap(emptyMap, filters);
        removeMap.putAll(map);

        assertEquals(2, removeMap.size());
        removeMap.remove(KEY_FILTERED);
        assertEquals(1, removeMap.size());
        removeMap.remove(KEY_UNFILTERED);
        assertEquals(0, removeMap.size());
    }

    @Test
    public void testGet() {
        assertEquals(PLACEHOLDER_FILTERED, filteredMap.get(KEY_FILTERED));
        assertEquals(VAL_UNFILTERED, filteredMap.get(KEY_UNFILTERED));

        Object actual = filteredMap.get(KEY_NESTED);
        assertTrue(actual instanceof FilteredMap);

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) actual;
        assertEquals(VAL_UNFILTERED, nestedMap.get(KEY_UNFILTERED));
        assertEquals(PLACEHOLDER_FILTERED, nestedMap.get(KEY_FILTERED));
    }

    @Test
    public void testKeySet() {
        Set<String> keySet = filteredMap.keySet();
        assertEquals(4, keySet.size());
        assertTrue(keySet.contains(KEY_FILTERED));
        assertTrue(keySet.contains(KEY_UNFILTERED));
        assertTrue(keySet.contains(KEY_NESTED));
    }

    @Test
    public void testValues() {
        Collection<Object> values = filteredMap.values();
        assertEquals(4, values.size());
        assertTrue(values.contains(VAL_UNFILTERED));
        assertTrue(values.contains(PLACEHOLDER_FILTERED));

        values.remove(PLACEHOLDER_FILTERED);
        values.remove(VAL_UNFILTERED);

        Object nestedObj = values.toArray(new Object[1])[0];
        assertTrue(nestedObj instanceof FilteredMap);

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) nestedObj;
        values = nestedMap.values();

        assertEquals(2, values.size());
        assertTrue(values.contains(VAL_UNFILTERED));
        assertTrue(values.contains(PLACEHOLDER_FILTERED));
    }

    @Test
    public void testEntrySet() {
        Set<Map.Entry<String, Object>> entries = filteredMap.entrySet();
        assertEquals(4, entries.size());

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
            } else if (key.equals(KEY_UNMODIFIABLE)) {
                expectedCount++;

                @SuppressWarnings("unchecked")
                Map<String, Object> nested = (Map<String, Object>) entry.getValue();
                assertEquals(2, nested.entrySet().size());
            }
        }
        assertEquals(4, expectedCount);
    }

}

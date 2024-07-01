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

public class RedactedKeysMapTest {

    private static final String KEY_UNREDACTED = "unredacted";
    private static final String KEY_REDACTED = "auth";
    private static final String KEY_NESTED = "nested";
    private static final String KEY_UNMODIFIABLE = "unmodifiable";
    private static final String VAL_UNREDACTED = "Foo";
    private static final String VAL_REDACTED = "Bar";
    private static final String PLACEHOLDER_REDACTED = "[REDACTED]";

    private Map<String, Object> redactedKeysMap;

    /**
     * Creates a map with filtered, unfiltered, and nested values
     * @throws Exception an exception
     */
    @Before
    public void setUp() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UNREDACTED, VAL_UNREDACTED);
        map.put(KEY_REDACTED, VAL_REDACTED);

        HashMap<String, Object> nestedMap = new HashMap<String, Object>();
        nestedMap.put(KEY_UNREDACTED, VAL_UNREDACTED);
        nestedMap.put(KEY_REDACTED, VAL_REDACTED);
        map.put(KEY_NESTED, nestedMap);

        map.put(KEY_UNMODIFIABLE, Collections.unmodifiableMap(nestedMap));

        this.redactedKeysMap = new RedactedKeysMap(map, Collections.singleton(KEY_REDACTED));
    }

    @Test
    public void testSize() {
        assertEquals(4, redactedKeysMap.size());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(redactedKeysMap.isEmpty());
        Map<String, Object> map = Collections.emptyMap();
        FilteredMap emptyMap = new FilteredMap(map, Collections.<String>emptyList());
        assertTrue(emptyMap.isEmpty());
    }

    @Test
    public void testClear() {
        assertEquals(4, redactedKeysMap.size());
        redactedKeysMap.clear();
        assertTrue(redactedKeysMap.isEmpty());
    }

    @Test
    public void testContainsKey() {
        assertTrue(redactedKeysMap.containsKey(KEY_REDACTED));
        assertTrue(redactedKeysMap.containsKey(KEY_UNREDACTED));
        assertTrue(redactedKeysMap.containsKey(KEY_NESTED));
        assertFalse(redactedKeysMap.containsKey("fake"));
    }

    @Test
    public void testRemove() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UNREDACTED, VAL_UNREDACTED);
        map.put(KEY_REDACTED, VAL_REDACTED);

        HashMap<String, Object> emptyMap = new HashMap<String, Object>();
        Set<String> redactedKeys = Collections.singleton(KEY_REDACTED);
        Map<String, Object> removeMap = new RedactedKeysMap(emptyMap, redactedKeys);
        removeMap.putAll(map);

        assertEquals(2, removeMap.size());
        removeMap.remove(KEY_REDACTED);
        assertEquals(1, removeMap.size());
        removeMap.remove(KEY_UNREDACTED);
        assertEquals(0, removeMap.size());
    }

    @Test
    public void testGet() {
        assertEquals(PLACEHOLDER_REDACTED, redactedKeysMap.get(KEY_REDACTED));
        assertEquals(VAL_UNREDACTED, redactedKeysMap.get(KEY_UNREDACTED));

        Object actual = redactedKeysMap.get(KEY_NESTED);
        assertTrue(actual instanceof RedactedKeysMap);

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) actual;
        assertEquals(VAL_UNREDACTED, nestedMap.get(KEY_UNREDACTED));
        assertEquals(PLACEHOLDER_REDACTED, nestedMap.get(KEY_REDACTED));
    }

    @Test
    public void testKeySet() {
        Set<String> keySet = redactedKeysMap.keySet();
        assertEquals(4, keySet.size());
        assertTrue(keySet.contains(KEY_REDACTED));
        assertTrue(keySet.contains(KEY_UNREDACTED));
        assertTrue(keySet.contains(KEY_NESTED));
    }

    @Test
    public void testValues() {
        Collection<Object> values = redactedKeysMap.values();
        assertEquals(4, values.size());
        assertTrue(values.contains(VAL_UNREDACTED));
        assertTrue(values.contains(PLACEHOLDER_REDACTED));

        values.remove(PLACEHOLDER_REDACTED);
        values.remove(VAL_UNREDACTED);

        Object nestedObj = values.toArray(new Object[1])[0];
        assertTrue(nestedObj instanceof RedactedKeysMap);

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) nestedObj;
        values = nestedMap.values();

        assertEquals(2, values.size());
        assertTrue(values.contains(VAL_UNREDACTED));
        assertTrue(values.contains(PLACEHOLDER_REDACTED));
    }

    @Test
    public void testEntrySet() {
        Set<Map.Entry<String, Object>> entries = redactedKeysMap.entrySet();
        assertEquals(4, entries.size());

        int expectedCount = 0;

        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();

            if (key.equals(KEY_REDACTED)) {
                expectedCount++;
                assertEquals(PLACEHOLDER_REDACTED, entry.getValue());

            } else if (key.equals(KEY_UNREDACTED)) {
                expectedCount++;
                assertEquals(VAL_UNREDACTED, entry.getValue());

            } else if (key.equals(KEY_NESTED)) {
                expectedCount++;
                Object value = entry.getValue();
                assertTrue(value instanceof RedactedKeysMap);
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

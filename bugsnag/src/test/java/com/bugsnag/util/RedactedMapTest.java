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

public class RedactedMapTest {

    private static final String KEY_UNREDACTED = "unredacted";
    private static final String KEY_REDACTED = "auth";
    private static final String KEY_NESTED = "nested";
    private static final String KEY_UNMODIFIABLE = "unmodifiable";
    private static final String VAL_UNREDACTED = "Foo";
    private static final String VAL_REDACTED = "Bar";
    private static final String PLACEHOLDER_REDACTED = "[REDACTED]";

    private Map<String, Object> redactedMap;

    /**
     * Creates a map with redacted, unredacted, and nested values
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

        this.redactedMap = new RedactedMap(map, Collections.singleton(KEY_REDACTED));
    }

    @Test
    public void testSize() {
        assertEquals(4, redactedMap.size());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(redactedMap.isEmpty());
        Map<String, Object> map = Collections.emptyMap();
        RedactedMap emptyMap = new RedactedMap(map, Collections.<String>emptyList());
        assertTrue(emptyMap.isEmpty());
    }

    @Test
    public void testClear() {
        assertEquals(4, redactedMap.size());
        redactedMap.clear();
        assertTrue(redactedMap.isEmpty());
    }

    @Test
    public void testContainsKey() {
        assertTrue(redactedMap.containsKey(KEY_REDACTED));
        assertTrue(redactedMap.containsKey(KEY_UNREDACTED));
        assertTrue(redactedMap.containsKey(KEY_NESTED));
        assertFalse(redactedMap.containsKey("fake"));
    }

    @Test
    public void testRemove() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UNREDACTED, VAL_UNREDACTED);
        map.put(KEY_REDACTED, VAL_REDACTED);

        HashMap<String, Object> emptyMap = new HashMap<String, Object>();
        Set<String> redactedKeys = Collections.singleton(KEY_REDACTED);
        Map<String, Object> removeMap = new RedactedMap(emptyMap, redactedKeys);
        removeMap.putAll(map);

        assertEquals(2, removeMap.size());
        removeMap.remove(KEY_REDACTED);
        assertEquals(1, removeMap.size());
        removeMap.remove(KEY_UNREDACTED);
        assertEquals(0, removeMap.size());
    }

    @Test
    public void testGet() {
        assertEquals(PLACEHOLDER_REDACTED, redactedMap.get(KEY_REDACTED));
        assertEquals(VAL_UNREDACTED, redactedMap.get(KEY_UNREDACTED));

        Object actual = redactedMap.get(KEY_NESTED);
        assertTrue(actual instanceof RedactedMap);

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) actual;
        assertEquals(VAL_UNREDACTED, nestedMap.get(KEY_UNREDACTED));
        assertEquals(PLACEHOLDER_REDACTED, nestedMap.get(KEY_REDACTED));
    }

    @Test
    public void testKeySet() {
        Set<String> keySet = redactedMap.keySet();
        assertEquals(4, keySet.size());
        assertTrue(keySet.contains(KEY_REDACTED));
        assertTrue(keySet.contains(KEY_UNREDACTED));
        assertTrue(keySet.contains(KEY_NESTED));
    }

    @Test
    public void testValues() {
        Collection<Object> values = redactedMap.values();
        assertEquals(4, values.size());
        assertTrue(values.contains(VAL_UNREDACTED));
        assertTrue(values.contains(PLACEHOLDER_REDACTED));

        values.remove(PLACEHOLDER_REDACTED);
        values.remove(VAL_UNREDACTED);

        Object nestedObj = values.toArray(new Object[1])[0];
        assertTrue(nestedObj instanceof RedactedMap);

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) nestedObj;
        values = nestedMap.values();

        assertEquals(2, values.size());
        assertTrue(values.contains(VAL_UNREDACTED));
        assertTrue(values.contains(PLACEHOLDER_REDACTED));
    }

    @Test
    public void testEntrySet() {
        Set<Map.Entry<String, Object>> entries = redactedMap.entrySet();
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
                assertTrue(value instanceof RedactedMap);
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

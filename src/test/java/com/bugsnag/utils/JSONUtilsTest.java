package com.bugsnag.utils;

import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.*;

public class JSONUtilsTest {
	@Test
	public void testFilterRemovesRequiredKeys() {
		JSONObject jsonObject = new JSONObject();
		JSONUtils.safePut(jsonObject, "key", "value");
		JSONUtils.safePut(jsonObject, "key1", "value1");
		JSONUtils.safePut(jsonObject, "key2", 2);

		JSONUtils.filter(jsonObject, new String[] {"key1"});

		assertThat(jsonObject.has("key"), is(true));
		assertThat(jsonObject.has("key1"), is(true));
		assertThat(jsonObject.has("key2"), is(true));

		assertThat(jsonObject.optString("key"), is("value"));
		assertThat(jsonObject.optString("key1"), is("[FILTERED]"));
		assertThat(jsonObject.optInt("key2"), is(2));
	}

	@Test
	public void testFilterRemovesRequiredKeysRecursively() {
		JSONObject jsonObject = new JSONObject();
		JSONUtils.safePut(jsonObject, "key", "value");
		JSONUtils.safePut(jsonObject, "key1", "value1");
		JSONUtils.safePut(jsonObject, "key2", 2);

		JSONObject embeddedJSONObject = new JSONObject();
		JSONUtils.safePut(embeddedJSONObject, "key", "value");
		JSONUtils.safePut(embeddedJSONObject, "key1", "value1");
		JSONUtils.safePut(embeddedJSONObject, "key2", 2);

		JSONUtils.safePut(jsonObject, "key3", embeddedJSONObject);

		JSONUtils.filter(jsonObject, new String[] {"key1"});

		assertThat(jsonObject.has("key"), is(true));
		assertThat(jsonObject.has("key1"), is(true));
		assertThat(jsonObject.has("key2"), is(true));
		assertThat(jsonObject.has("key3"), is(true));

		assertThat(jsonObject.optString("key"), is("value"));
		assertThat(jsonObject.optString("key1"), is("[FILTERED]"));
		assertThat(jsonObject.optInt("key2"), is(2));

		assertThat(jsonObject.optJSONObject("key3"), is(instanceOf(JSONObject.class)));

		assertThat(jsonObject.optJSONObject("key3").has("key"), is(true));
		assertThat(jsonObject.optJSONObject("key3").has("key1"), is(true));
		assertThat(jsonObject.optJSONObject("key3").has("key2"), is(true));

		assertThat(jsonObject.optJSONObject("key3").optString("key"), is("value"));
		assertThat(jsonObject.optJSONObject("key3").optString("key1"), is("[FILTERED]"));
		assertThat(jsonObject.optJSONObject("key3").optInt("key2"), is(2));
	}

	@Test
	public void testJSONObjectReturnedFromMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("key", "value");
		map.put("key1", "value1");
		map.put("key2", 2);
		JSONObject returnValue = (JSONObject)JSONUtils.objectForJSON(map);

		assertThat(returnValue, is(instanceOf(JSONObject.class)));

		assertThat(returnValue.has("key"), is(true));
		assertThat(returnValue.has("key1"), is(true));
		assertThat(returnValue.has("key2"), is(true));

		assertThat(returnValue.optString("key"), is("value"));
		assertThat(returnValue.optString("key1"), is("value1"));
		assertThat(returnValue.optInt("key2"), is(2));
	}

	@Test
	public void testJSONArrayReturnedFromArray() {
		String[] array = new String[] { "first", "second" };
		JSONArray returnValue = (JSONArray)JSONUtils.objectForJSON(array);

		assertThat(returnValue, is(instanceOf(JSONArray.class)));

		assertThat(returnValue.optString(0), is("first"));
		assertThat(returnValue.optString(1), is("second"));
	}

	@Test
	public void testJSONArrayReturnedFromList() {
		ArrayList<String> array = new ArrayList<String>();
		array.add("first");
		array.add("second");
		JSONArray returnValue = (JSONArray)JSONUtils.objectForJSON(array);

		assertThat(returnValue, is(instanceOf(JSONArray.class)));

		assertThat(returnValue.optString(0), is("first"));
		assertThat(returnValue.optString(1), is("second"));
	}

	@Test
	public void testOriginalReturnedFromString() {
		String returnValue = (String)JSONUtils.objectForJSON("string");

		assertThat(returnValue, is(instanceOf(String.class)));

		assertThat(returnValue, is("string"));
	}

	@Test
	public void testRecursionOfObjectForJSON() {
		HashMap<String, Object> embeddedMap = new HashMap<String, Object>();
		embeddedMap.put("mapKey", "mapValue");

		ArrayList<String> embeddedArrayList = new ArrayList<String>();
		embeddedArrayList.add("arrayList");

		String[] embeddedArray = new String[] { "array" };

		HashMap<String, Object> rootMap = new HashMap<String, Object>();
		rootMap.put("map", embeddedMap);
		rootMap.put("arrayList", embeddedArrayList);
		rootMap.put("array", embeddedArray);
		JSONObject returnValue = (JSONObject)JSONUtils.objectForJSON(rootMap);

		assertThat(returnValue, is(instanceOf(JSONObject.class)));

		assertThat(returnValue.has("map"), is(true));
		assertThat(returnValue.has("arrayList"), is(true));
		assertThat(returnValue.has("array"), is(true));

		assertThat(returnValue.optJSONObject("map"), is(instanceOf(JSONObject.class)));
		assertThat(returnValue.optJSONObject("map").has("mapKey"), is(true));
		assertThat(returnValue.optJSONObject("map").optString("mapKey"), is("mapValue"));

		assertThat(returnValue.optJSONArray("arrayList"), is(instanceOf(JSONArray.class)));
		assertThat(returnValue.optJSONArray("arrayList").optString(0), is("arrayList"));

		assertThat(returnValue.optJSONArray("array"), is(instanceOf(JSONArray.class)));
		assertThat(returnValue.optJSONArray("array").optString(0), is("array"));
	}

	@Test
	public void testValuesTruncated() {
		String returnValue = (String)JSONUtils.objectForJSON(generateTestString(10000));

        String truncatedSuffix = "[TRUNCATED]";
        assertEquals(4096 + truncatedSuffix.length(), returnValue.length());
        assertThat(returnValue, endsWith(truncatedSuffix));
	}

    @Test
    public void testMultipleValuesTruncated() {
        HashMap<String, Object> embeddedMap = new HashMap<String, Object>();
        embeddedMap.put("mapKey", generateTestString(10000));

        ArrayList<String> embeddedArrayList = new ArrayList<String>();
        embeddedArrayList.add(generateTestString(10000));

        String[] embeddedArray = new String[] { generateTestString(10000) };

        HashMap<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put("map", embeddedMap);
        rootMap.put("arrayList", embeddedArrayList);
        rootMap.put("array", embeddedArray);
        rootMap.put("string", generateTestString(10000));

        JSONObject returnValue = (JSONObject)JSONUtils.objectForJSON(rootMap);

        // Contains four large strings truncated to 4096 characters
        assertEquals(16491, returnValue.toString().length());
    }

    private String generateTestString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++){
            sb.append("A");
        }
        return sb.toString();
    }
}

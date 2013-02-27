package com.bugsnag;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

class Util {
    static byte[] stringToByteArray(String str) {
        byte[] bytes = null;

        try {
            bytes = str.getBytes("UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace(System.err);
        }

        return bytes;
    }

    static void addToJSONObject(JSONObject obj, String key, Object val) {
        try {
            obj.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace(System.err);
        }
    }

    static void addToJSONArray(JSONArray arr, Object val) {
        arr.put(val);
    }

    static JSONArray listToJSONArray(List<Object> source, String[] filters) {
        if(source == null) return null;

        JSONArray returnValue = new JSONArray();
        for (Object value : source) {
            if(value instanceof Map) {
                returnValue.put(mapToJSONObject((Map<String,Object>)value, filters));
            } else if(value instanceof List) {
                returnValue.put(listToJSONArray((List<Object>)value, filters));
            } else {
                returnValue.put(value);
            }
        }
        return returnValue;
    }

    static JSONObject mapToJSONObject(Map<String,Object> source, String[] filters) {
        if(source == null) return null;

        JSONObject returnValue = new JSONObject();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(matchesFilter(key, filters)) {
                addToJSONObject(returnValue, key, "[FILTERED]");
            } else {
                if(value instanceof Map) {
                    addToJSONObject(returnValue, key, mapToJSONObject((Map<String,Object>)value, filters));
                } else if(value instanceof List) {
                    addToJSONObject(returnValue, key, listToJSONArray((List<Object>)value, filters));
                } else {
                    addToJSONObject(returnValue, key, value);
                }
            }
        }
        return returnValue;
    }

    static JSONObject mergeJSONObjects(JSONObject dest, JSONObject source) {
        if(source == null) return dest;
        if(dest == null) return source;

        for (String key: JSONObject.getNames(source)) {
            try {
                Object value = source.get(key);
                if (!dest.has(key)) {
                    // new value for "key":
                    dest.put(key, value);
                } else {
                    // existing value for "key" - recursively deep merge:
                    if (value instanceof JSONObject) {
                        JSONObject valueJson = (JSONObject)value;
                        mergeJSONObjects(valueJson, dest.getJSONObject(key));
                    } else {
                        dest.put(key, value);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace(System.err);
            }
        }
        return dest;
    }

    private static boolean matchesFilter(String key, String[] filters) {
        if(filters == null || key == null) {
            return false;
        }

        for(String filter : filters) {
            if(key.contains(filter)) {
                return true;
            }
        }
        
        return false;
    }
}
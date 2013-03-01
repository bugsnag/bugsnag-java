package com.bugsnag.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class JSONUtils {
    public static void safePut(JSONObject obj, String key, Object val) {
        try {
            obj.put(key, objectForJSON(val));
        } catch (JSONException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void filter(JSONObject object, String[] filters) {
        if(object == null) return;

        Iterator keys = object.keys();

        while(keys.hasNext()) {
            String key = (String)keys.next();
            if( matchesFilter(key, filters) ){
                safePut(object, key, "[FILTERED]");
            } else {
                Object value = object.opt(key);
                if(value != null && value instanceof JSONObject) {
                    filter((JSONObject)value, filters);
                }
            }
        }
    }

    public static JSONObject deepClone(JSONObject source) {
        if(source == null) return null;

        JSONObject dest = new JSONObject();

        Iterator keys = source.keys();

        while(keys.hasNext()) {
            String key = (String)keys.next();
            Object value = source.opt(key);
            if(value != null) {
                if( value instanceof JSONObject ){
                    value = deepClone((JSONObject)value);
                }
                safePut((JSONObject)dest, key, value);
            }
        }

        return dest;
    }

    public static void merge(JSONObject dest, JSONObject source) {
        if(dest == null || source == null) return;

        Iterator keys = source.keys();

        while(keys.hasNext()) {
            String key = (String)keys.next();
            Object sourceValue = source.opt(key);
            if(sourceValue != null) {
                Object destValue = dest.opt(key);
                if( destValue != null && sourceValue instanceof JSONObject && destValue instanceof JSONObject ){
                    merge((JSONObject)destValue, (JSONObject)sourceValue);
                } else {
                    safePut(dest, key, sourceValue);
                }
            }
        }
    }

    static boolean matchesFilter(String key, String[] filters) {
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

    static Object objectForJSON(Object value) {
        if(value == null) return null;

        if(value instanceof Map) {
            JSONObject dest = new JSONObject();

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map)value;

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                safePut(dest, entry.getKey(), objectForJSON(entry.getValue()));
            }
            return dest;
        } else if(value instanceof Object[]) {
            JSONArray dest = new JSONArray();

            Object[] array = (Object[])value;
            for(Object val : array) {
                dest.put(objectForJSON(val));
            }
            return dest;
        } else if(value instanceof List) {
            JSONArray dest = new JSONArray();

            List list = (List)value;
            for(Object val : list) {
                dest.put(objectForJSON(val));
            }
            return dest;
        } else {
            return value;
        }
    }
}
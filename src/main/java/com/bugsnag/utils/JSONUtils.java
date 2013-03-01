package com.bugsnag.utils;

import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONException;

public class JSONUtils {
    public static void safePut(JSONObject obj, String key, Object val) {
        try {
            obj.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void filter(JSONObject object, String[] filters) {
        if(object == null) return;

        Iterator<String> keys = object.keys();

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

        Iterator<String> keys = source.keys();

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

        Iterator<String> keys = source.keys();

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
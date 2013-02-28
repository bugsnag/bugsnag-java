package com.bugsnag;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONException;

public class Util {
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

    static JSONObject mergeJSONObjects(JSONObject dest, JSONObject source) {
        if(source == null) return dest;
        if(dest == null) return source;

        Iterator<String> keys = source.keys();
        while(keys.hasNext()) {
            String key = (String)keys.next();
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
}
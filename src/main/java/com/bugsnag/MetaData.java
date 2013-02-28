package com.bugsnag;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MetaData extends HashMap<String, Object> {
    public void addToTab(String tabName, String key, Object value) {
        Object tab = get(tabName);
        if(tab == null || !(tab instanceof Map)) {
            tab = new HashMap<String, Object>();
            put(tabName, tab);
        }

        if(value != null) {
            ((Map)tab).put(key, value);
        } else {
            ((Map)tab).remove(key);
        }
    }

    public void clearTab(String tabName){
        remove(tabName);
    }

    public JSONObject toJSON(String[] filters) {
        return mapToJSONObject(this, filters);
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

    private static JSONArray listToJSONArray(List<Object> source, String[] filters) {
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

    private static  JSONObject mapToJSONObject(Map<String,Object> source, String[] filters) {
        if(source == null) return null;

        JSONObject returnValue = new JSONObject();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(matchesFilter(key, filters)) {
                Util.addToJSONObject(returnValue, key, "[FILTERED]");
            } else {
                if(value instanceof Map) {
                    Util.addToJSONObject(returnValue, key, mapToJSONObject((Map<String,Object>)value, filters));
                } else if(value instanceof List) {
                    Util.addToJSONObject(returnValue, key, listToJSONArray((List<Object>)value, filters));
                } else {
                    Util.addToJSONObject(returnValue, key, value);
                }
            }
        }
        return returnValue;
    }
}
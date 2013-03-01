package com.bugsnag;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MetaData extends JSONObject {
    public MetaData() {
        super();
    }

    public MetaData(MetaData source) throws JSONException {
        super(source, getNames(source));
        String[] names = getNames(source);

        for (int i = 0; i < names.length; i++) {
            String key = names[i];
            Object value = get(key);
            if( value instanceof JSONObject ){
                JSONObject newValue = deepCloneJSONObject((JSONObject)value);
                put(key, newValue);
            }
        }
    }

    public void addToTab(String tabName, String key, Object value) {
        if(value != null) {
            Util.addToJSONObject(getTab(tabName), key, value);
        } else {
            getTab(tabName).remove(key);
        }
    }

    public void addToTab(String tabName, Object value) {
        if(value instanceof Map) {
            JSONObject tab = getTab(tabName);
            for (Map.Entry<String, Object> entry : ((Map<String, Object>)value).entrySet()) {
                Util.addToJSONObject(tab, entry.getKey(), entry.getValue());
            }
        } else {
            addToTab("Custom Data", tabName, value);
        }
    }

    public void clearTab(String tabName){
        remove(tabName);
    }

    public MetaData duplicate() {
        try {
            return new MetaData(this);
        } catch (JSONException e) {
            e.printStackTrace(System.err);
            return new MetaData();
        }
    }

    public MetaData filter(String[] filters) {
        filter(this, filters);
        return this;
    }

    public MetaData merge(JSONObject source) {
        mergeJSONObjects(this, source);
        return this;
    }

    private JSONObject getTab(String tabName) {
        Object tab = opt(tabName);

        if(tab == null || !(tab instanceof JSONObject)) {
            tab = new JSONObject();
            Util.addToJSONObject(this, tabName, tab);
        }
        return (JSONObject)tab;
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

    private static void filter(JSONObject object, String[] filters) {
        String[] names = getNames(object);

        for (int i = 0; i < names.length; i++) {
            String key = names[i];
            if( matchesFilter(key, filters) ){
                Util.addToJSONObject(object, key, "[FILTERED]");
            } else {
                Object value = object.opt(key);
                if(value != null && value instanceof JSONObject) {
                    filter((JSONObject)value, filters);
                }
            }
        }
    }

    private static JSONObject deepCloneJSONObject(JSONObject source) {
        String[] names = getNames(source);
        JSONObject dest;

        try {
            dest = new JSONObject(source, names);
        } catch (JSONException e) {
            e.printStackTrace(System.err);
            return new JSONObject();
        }

        for (int i = 0; i < names.length; i++) {
            String key = names[i];
            Object value = source.opt(key);
            if( value != null && value instanceof JSONObject ){
                JSONObject newValue = deepCloneJSONObject((JSONObject)value);
                Util.addToJSONObject((JSONObject)dest, key, newValue);
            }
        }

        return dest;
    }

    private static void mergeJSONObjects(JSONObject dest, JSONObject source) {
        String[] names = getNames(source);

        for (int i = 0; i < names.length; i++) {
            String key = names[i];
            Object sourceValue = source.opt(key);
            if(sourceValue != null) {
                Object destValue = dest.opt(key);
                if( destValue != null && sourceValue instanceof JSONObject && destValue instanceof JSONObject ){
                    mergeJSONObjects((JSONObject)destValue, (JSONObject)sourceValue);
                } else {
                    Util.addToJSONObject(dest, key, sourceValue);
                }
            }
        }
    }
}
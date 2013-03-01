package com.bugsnag;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.bugsnag.utils.JSONUtils;

public class MetaData extends JSONObject {
    public MetaData() {
        super();
    }

    public MetaData(MetaData source) {
        super();
        Iterator keys = source.keys();

        while(keys.hasNext()) {
            String key = (String)keys.next();
            Object value = source.opt(key);
            if(value != null) {
                if( value instanceof JSONObject ){
                    value = JSONUtils.deepClone((JSONObject)value);
                }
                JSONUtils.safePut(this, key, value);
            }
        }
    }

    public void addToTab(String tabName, String key, Object value) {
        if(value != null) {
            JSONUtils.safePut(getTab(tabName), key, value);
        } else {
            getTab(tabName).remove(key);
        }
    }

    public void addToTab(String tabName, Object value) {
        if(value instanceof Map) {
            JSONObject tab = getTab(tabName);
            @SuppressWarnings("unchecked")
            Map<String, Object> mapValue = (Map<String, Object>)value;
            for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
                JSONUtils.safePut(tab, entry.getKey(), entry.getValue());
            }
        } else {
            addToTab("Custom Data", tabName, value);
        }
    }

    public void clearTab(String tabName){
        remove(tabName);
    }

    public MetaData duplicate() {
        return new MetaData(this);
    }

    public MetaData filter(String[] filters) {
        JSONUtils.filter(this, filters);
        return this;
    }

    public MetaData merge(JSONObject source) {
        JSONUtils.merge(this, source);
        return this;
    }

    private JSONObject getTab(String tabName) {
        Object tab = opt(tabName);

        if(tab == null || !(tab instanceof JSONObject)) {
            tab = new JSONObject();
            JSONUtils.safePut(this, tabName, tab);
        }
        return (JSONObject)tab;
    }
}
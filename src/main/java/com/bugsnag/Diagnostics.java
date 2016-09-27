package com.bugsnag;

import java.util.HashMap;
import java.util.Map;

class Diagnostics {
    String context;
    Map<String, Object> app = new HashMap<String, Object>();
    Map<String, Object> device = new HashMap<String, Object>();
    Map<String, String> user = new HashMap<String, String>();
    MetaData metaData = new MetaData();
}

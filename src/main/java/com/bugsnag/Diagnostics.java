package com.bugsnag;

import java.util.HashMap;
import java.util.Map;

class Diagnostics {
    String context;
    Map app = new HashMap();
    Map device = new HashMap();
    Map user = new HashMap();
    MetaData metaData = new MetaData();
}

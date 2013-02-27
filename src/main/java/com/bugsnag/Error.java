package com.bugsnag;

import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;

class Error {
    private Throwable exception;
    private Configuration config;
    private Map<String, Object> metaData;

    public Error(Throwable exception, Map<String, Object> metaData, Configuration config) {
        this.exception = exception;
        this.config = config;
        this.metaData = metaData;
    }

    public JSONObject toJSON() {
        JSONObject error = new JSONObject();

        // Add basic information
        Util.addToJSONObject(error, "userId", config.getUserId());
        Util.addToJSONObject(error, "appVersion", config.getAppVersion());
        Util.addToJSONObject(error, "osVersion", config.getOsVersion());
        Util.addToJSONObject(error, "releaseStage", config.getReleaseStage());
        Util.addToJSONObject(error, "context", config.getContext());

        // Unwrap exceptions
        JSONArray exceptions = new JSONArray();
        Throwable currentEx = this.exception;
        while(currentEx != null) {
            JSONObject exception = new JSONObject();
            Util.addToJSONObject(exception, "errorClass", currentEx.getClass().getName());
            Util.addToJSONObject(exception, "message", currentEx.getLocalizedMessage());

            // Stacktrace
            JSONArray stacktrace = new JSONArray();
            StackTraceElement[] stackTrace = currentEx.getStackTrace();
            for(StackTraceElement el : stackTrace) {
                try {
                    JSONObject line = new JSONObject();
                    Util.addToJSONObject(line, "method", el.getClassName() + "." + el.getMethodName());
                    Util.addToJSONObject(line, "file", el.getFileName() == null ? "Unknown" : el.getFileName());
                    Util.addToJSONObject(line, "lineNumber", el.getLineNumber());

                    // TODO: Set inProject packages, use config.getProjectPackages();
                    // if(el.getClassName().startsWith(packageName)) {
                    //     line.put("inProject", true);
                    // }

                    Util.addToJSONArray(stacktrace, line);
                } catch(Exception lineEx) {
                    lineEx.printStackTrace(System.err);
                }
            }
            Util.addToJSONObject(exception, "stacktrace", stacktrace);

            currentEx = currentEx.getCause();
            Util.addToJSONArray(exceptions, exception);
        }
        Util.addToJSONObject(error, "exceptions", exceptions);

        // Merge config.metaData with this.metaData and add to this error
        JSONObject globalMetaData = Util.mapToJSONObject(config.getMetaData());
        JSONObject localMetaData = Util.mapToJSONObject(this.metaData);
        Util.addToJSONObject(error, "metaData", Util.mergeJSONObjects(globalMetaData, localMetaData));
        // TODO: Apply filters config.getFilters()

        return error;
    }

    public String toString() {
        return toJSON().toString();
    }
}
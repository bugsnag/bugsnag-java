package com.bugsnag;

import org.json.JSONObject;
import org.json.JSONArray;
import com.bugsnag.utils.JSONUtils;

public class Error extends JSONObject {
    public Error(Throwable exception, MetaData metaData, Configuration config) {
        // Add basic information
        JSONUtils.safePut(this, "userId", config.userId);
        JSONUtils.safePut(this, "appVersion", config.appVersion);
        JSONUtils.safePut(this, "osVersion", config.osVersion);
        JSONUtils.safePut(this, "releaseStage", config.releaseStage);
        JSONUtils.safePut(this, "context", config.context);

        // Unwrap exceptions
        JSONArray exceptions = new JSONArray();
        Throwable currentEx = exception;
        while(currentEx != null) {
            JSONObject exceptionObject = new JSONObject();
            JSONUtils.safePut(exceptionObject, "errorClass", currentEx.getClass().getName());
            JSONUtils.safePut(exceptionObject, "message", currentEx.getLocalizedMessage());

            // Stacktrace
            JSONArray stacktrace = new JSONArray();
            StackTraceElement[] stackTrace = currentEx.getStackTrace();
            for(StackTraceElement el : stackTrace) {
                try {
                    JSONObject line = new JSONObject();
                    JSONUtils.safePut(line, "method", el.getClassName() + "." + el.getMethodName());
                    JSONUtils.safePut(line, "file", el.getFileName() == null ? "Unknown" : el.getFileName());
                    JSONUtils.safePut(line, "lineNumber", el.getLineNumber());

                    // Check if line is inProject
                    if(config.projectPackages != null) {
                        for(String packageName : config.projectPackages) {
                            if(packageName != null && el.getClassName().startsWith(packageName)) {
                                line.put("inProject", true);
                                break;
                            }
                        }
                    }

                    stacktrace.put(line);
                } catch(Exception lineEx) {
                    lineEx.printStackTrace(System.err);
                }
            }
            JSONUtils.safePut(exceptionObject, "stacktrace", stacktrace);

            currentEx = currentEx.getCause();
            exceptions.put(exceptionObject);
        }
        JSONUtils.safePut(this, "exceptions", exceptions);

        // Merge global metaData with local metaData, apply filters, and add to this error
        MetaData errorMetaData = config.getMetaData().merge(metaData).filter(config.filters);
        JSONUtils.safePut(this, "metaData", errorMetaData);
    }

    public void setContext(String context) {
        JSONUtils.safePut(this, "context", context);
    }
}
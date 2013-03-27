package com.bugsnag;

import java.io.FileWriter;

import org.json.JSONObject;
import org.json.JSONArray;
import com.bugsnag.utils.JSONUtils;

public class Error {
    private Throwable exception;
    private Configuration config;
    private MetaData metaData;
    private String context;

    public Error(Throwable exception, MetaData metaData, Configuration config) {
        this.exception = exception;
        this.config = config;
        this.metaData = metaData;

        if(this.metaData == null) {
            this.metaData = new MetaData();
        }
    }

    public JSONObject toJSON() {
        JSONObject error = new JSONObject();

        // Add basic information
        JSONUtils.safePut(error, "userId", config.userId);
        JSONUtils.safePut(error, "appVersion", config.appVersion);
        JSONUtils.safePut(error, "osVersion", config.osVersion);
        JSONUtils.safePut(error, "releaseStage", config.releaseStage);
        JSONUtils.safePut(error, "context", getContext());

        // Unwrap exceptions
        JSONArray exceptions = new JSONArray();
        Throwable currentEx = this.exception;
        while(currentEx != null) {
            JSONObject exception = new JSONObject();
            JSONUtils.safePut(exception, "errorClass", currentEx.getClass().getName());
            JSONUtils.safePut(exception, "message", currentEx.getLocalizedMessage());

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
            JSONUtils.safePut(exception, "stacktrace", stacktrace);

            currentEx = currentEx.getCause();
            exceptions.put(exception);
        }
        JSONUtils.safePut(error, "exceptions", exceptions);

        // Merge global metaData with local metaData, apply filters, and add to this error
        MetaData errorMetaData = config.getMetaData().merge(metaData).filter(config.filters);
        JSONUtils.safePut(error, "metaData", errorMetaData);

        return error;
    }

    public String toString() {
        return toJSON().toString();
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void addToTab(String tabName, String key, Object value) {
        metaData.addToTab(tabName, key, value);
    }

    public boolean shouldIgnore() {
        return config.shouldIgnore(exception.getClass().getName());
    }

    public void writeToFile(String filename) throws java.io.IOException {
        String errorString = toString();
        if(errorString.length() > 0) {
            // Write the error to disk
            FileWriter writer = null;
            try {
                writer = new FileWriter(filename);
                writer.write(errorString);
                writer.flush();
                config.logger.debug(String.format("Saved unsent error to disk (%s) ", filename));
            } finally {
                if(writer != null) {
                    writer.close();
                }
            }
        }
    }

    private String getContext() {
        if(context != null) {
            return context;
        } else {
            return config.context;
        }
    }
}
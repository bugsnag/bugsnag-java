package com.bugsnag;

import java.io.FileWriter;
import java.util.List;
import java.util.Arrays;

import org.json.JSONObject;
import org.json.JSONArray;
import com.bugsnag.utils.JSONUtils;

public class Error {
    private static final List<String> ALLOWED_SEVERITIES = Arrays.asList("fatal", "error", "warning", "info");

    private Throwable exception;
    private Configuration config;
    private MetaData metaData;
    private Diagnostics diagnostics;
    private String severity;

    public Error(Throwable exception, String severity, MetaData metaData, Configuration config, Diagnostics diagnostics) {
        this.exception = exception;
        this.config = config;
        this.metaData = metaData;
        this.diagnostics = diagnostics;
        this.setSeverity(severity);

        if(this.metaData == null) {
            this.metaData = new MetaData();
        }
    }

    public JSONObject toJSON() {
        JSONObject error = new JSONObject();

        // Add basic information
        JSONUtils.safePut(error, "user", diagnostics.getUser());

        JSONUtils.safePutOpt(error, "app", diagnostics.getAppData());
        JSONUtils.safePutOpt(error, "appState", diagnostics.getAppState());

        JSONUtils.safePutOpt(error, "device", diagnostics.getDeviceData());
        JSONUtils.safePutOpt(error, "deviceState", diagnostics.getDeviceState());
        
        JSONUtils.safePut(error, "context", diagnostics.getContext());
        JSONUtils.safePut(error, "severity", severity);

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

    private void setSeverity(String severity) {
        if(severity == null || !ALLOWED_SEVERITIES.contains(severity)) {
            this.severity = "error";
        } else {
            this.severity = severity;
        }
    }
}
package com.bugsnag;

import java.io.FileWriter;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Comparator;

import org.json.JSONObject;
import org.json.JSONArray;
import com.bugsnag.utils.JSONUtils;

public class Error {
    private static final List<String> ALLOWED_SEVERITIES = Arrays.asList("error", "warning", "info");

    private Throwable exception;
    private Configuration config;
    private MetaData metaData;
    private Diagnostics diagnostics;
    private String severity;
    private String payloadVersion = "2";
    private String groupingHash;

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

        JSONUtils.safePut(error, "payloadVersion", payloadVersion);

        if(groupingHash != null) {
            JSONUtils.safePut(error, "groupingHash", groupingHash);
        }

        // Unwrap exceptions
        JSONArray exceptions = new JSONArray();
        Throwable currentEx = this.exception;
        while(currentEx != null) {
            JSONObject exception = new JSONObject();
            JSONUtils.safePut(exception, "errorClass", currentEx.getClass().getName());
            JSONUtils.safePut(exception, "message", currentEx.getLocalizedMessage());

            // Stacktrace
            JSONArray stacktraceJSON = stacktraceToJSON(currentEx.getStackTrace());
            JSONUtils.safePut(exception, "stacktrace", stacktraceJSON);

            currentEx = currentEx.getCause();
            exceptions.put(exception);
        }
        JSONUtils.safePut(error, "exceptions", exceptions);

        // Merge global metaData with local metaData, apply filters, and add to this error
        MetaData errorMetaData = config.getMetaData().merge(metaData).filter(config.filters);
        JSONUtils.safePut(error, "metaData", errorMetaData);

        // Add thread status to payload
        JSONUtils.safePut(error, "threads", getThreadStatus());

        return error;
    }

    public String toString() {
        return toJSON().toString();
    }

    public void addToTab(String tabName, String key, Object value) {
        metaData.addToTab(tabName, key, value);
    }

    public boolean shouldIgnore() {
        return config.shouldIgnore(getExceptionName());
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

    private JSONArray getThreadStatus() {
        JSONArray threads = new JSONArray();

        long currentId = Thread.currentThread().getId();

        Map<Thread,StackTraceElement[]> liveThreads = Thread.getAllStackTraces();

        Object[] keys = liveThreads.keySet().toArray();
        Arrays.sort(keys, new Comparator<Object>(){
            public int compare(Object a, Object b) {
                return Long.compare(((Thread)a).getId(), ((Thread)b).getId());
            }
        });

        for(int i = 0; i < keys.length; i++) {
            JSONObject threadJSON = new JSONObject();
            Thread thread = (Thread)keys[i];

            // Don't show the current stacktrace here. It'll point at this method
            // rather than at the point they crashed.
            if (thread.getId() != currentId) {
                StackTraceElement[] stacktrace = liveThreads.get(thread);
                JSONArray stacktraceJSON = stacktraceToJSON(stacktrace);

                JSONUtils.safePut(threadJSON, "id", thread.getId());
                JSONUtils.safePut(threadJSON, "name", thread.getName());
                JSONUtils.safePut(threadJSON, "stacktrace", stacktraceJSON);

                threads.put(threadJSON);
            }
        }

        return threads;
    }

    private JSONArray stacktraceToJSON(StackTraceElement[] stacktrace) {
        JSONArray stacktraceJson = new JSONArray();

        for(StackTraceElement el : stacktrace) {
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

                stacktraceJson.put(line);
            } catch(Exception lineEx) {
                lineEx.printStackTrace(System.err);
            }
        }

        return stacktraceJson;
    }

    public void setSeverity(String severity) {
        if(severity == null || !ALLOWED_SEVERITIES.contains(severity)) {
            this.severity = "warning";
        } else {
            this.severity = severity;
        }
    }

    public void setGroupingHash(String groupingHash) {
        this.groupingHash = groupingHash;
    }

    public Throwable getException() {
        return exception;
    }

    public String getExceptionName() {
        return exception.getClass().getName();
    }

    public StackTraceElement[] getStackTrace() {
        return exception.getStackTrace();
    }

    public MetaData getMetaData() {
        return metaData;
    }
}

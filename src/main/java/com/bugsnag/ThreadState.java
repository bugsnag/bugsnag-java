package com.bugsnag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

class ThreadState {

    private final Configuration config;
    private final Thread thread;
    private final StackTraceElement[] stackTraceElements;
    private Boolean errorReportingThread;

    ThreadState(Configuration config, Thread thread, StackTraceElement[] stackTraceElements) {
        this.config = config;
        this.thread = thread;
        this.stackTraceElements = stackTraceElements;
    }

    static List<ThreadState> getLiveThreads(Configuration config,
                                            Thread currentThread,
                                            Map<Thread, StackTraceElement[]> liveThreads,
                                            Throwable exc) {
        // Get current thread id (the crashing thread) and stacktraces for all live threads
        long crashingThreadId = currentThread.getId();

        // if thread is not present for any reason, add the current stacktrace to the map
        // so that the errorReportingThread will always be reported
        if (!liveThreads.containsKey(currentThread)) {
            liveThreads.put(currentThread, currentThread.getStackTrace());
        }
        if (exc != null) { // unhandled errors use the exception trace
            liveThreads.put(currentThread, exc.getStackTrace());
        }

        // Sort threads by thread-id
        Object[] keys = liveThreads.keySet().toArray();
        Arrays.sort(keys, new Comparator<Object>() {
            public int compare(Object first, Object second) {
                return Long.valueOf(((Thread) first).getId()).compareTo(((Thread) second).getId());
            }
        });

        List<ThreadState> threads = new ArrayList<ThreadState>();

        for (Object key : keys) {
            Thread thread = (Thread) key;
            ThreadState threadState = new ThreadState(config, thread, liveThreads.get(thread));
            threads.add(threadState);

            if (threadState.getId() == crashingThreadId) {
                threadState.setErrorReportingThread(true);
            }
        }
        return threads;
    }

    @JsonProperty("id")
    public long getId() {
        return thread.getId();
    }

    @JsonProperty("name")
    public String getName() {
        return thread.getName();
    }

    @JsonProperty("stacktrace")
    public List<Stackframe> getStacktrace() {
        return Stackframe.getStacktrace(config, stackTraceElements);
    }

    @JsonProperty("errorReportingThread")
    public Boolean isErrorReportingThread() {
        return errorReportingThread;
    }

    public void setErrorReportingThread(Boolean errorReportingThread) {
        this.errorReportingThread = errorReportingThread;
    }
}

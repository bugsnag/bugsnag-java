package com.bugsnag;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

public class BugsnagLogger implements Logger {
    protected static final String LOGGER_NAME = "Bugsnag";

    protected Client client;

    public BugsnagLogger(Client client) {
        this.client = client;
    }

    public String getName() {
        return LOGGER_NAME;
    }

    public boolean isTraceEnabled() {
        return false;
    }

    public void trace(String msg) {
        // Trace not supported
    }

    public void trace(String format, Object arg) {
        // Trace not supported
    }

    public void trace(String format, Object arg1, Object arg2) {
        // Trace not supported
    }

    public void trace(String format, Object... arguments) {
        // Trace not supported
    }

    public void trace(String msg, Throwable t) {
        // Trace not supported
    }

    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    public void trace(Marker marker, String msg) {
        // Markers not supported
    }

    public void trace(Marker marker, String format, Object arg) {
        // Markers not supported
    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        // Markers not supported
    }

    public void trace(Marker marker, String format, Object... argArray) {
        // Markers not supported
    }

    public void trace(Marker marker, String msg, Throwable t) {
        // Markers not supported
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void debug(String msg) {
        // Debug not supported
    }

    public void debug(String format, Object arg) {
        // Debug not supported
    }

    public void debug(String format, Object arg1, Object arg2) {
        // Debug not supported
    }

    public void debug(String format, Object... arguments) {
        // Debug not supported
    }

    public void debug(String msg, Throwable t) {
        // Debug not supported
    }

    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    public void debug(Marker marker, String msg) {
        // Markers not supported
    }

    public void debug(Marker marker, String format, Object arg) {
        // Markers not supported
    }

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        // Markers not supported
    }

    public void debug(Marker marker, String format, Object... arguments) {
        // Markers not supported
    }

    public void debug(Marker marker, String msg, Throwable t) {
        // Markers not supported
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String msg) {
        // client.notify(msg, "info");
    }

    public void info(String format, Object arg) {
        info(MessageFormatter.format(format, arg).getMessage());
    }

    public void info(String format, Object arg1, Object arg2) {
        info(MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    public void info(String format, Object... arguments) {
        info(MessageFormatter.format(format, arguments).getMessage());
    }

    public void info(String msg, Throwable t) {
        client.notify(t, "info");
    }

    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    public void info(Marker marker, String msg) {
        // Markers not supported
    }

    public void info(Marker marker, String format, Object arg) {
        // Markers not supported
    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        // Markers not supported
    }

    public void info(Marker marker, String format, Object... arguments) {
        // Markers not supported
    }

    public void info(Marker marker, String msg, Throwable t) {
        // Markers not supported
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(String msg) {
        // client.notify(msg, "warning");
    }

    public void warn(String format, Object arg) {
        warn(MessageFormatter.format(format, arg).getMessage());
    }

    public void warn(String format, Object arg1, Object arg2) {
        warn(MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    public void warn(String format, Object... arguments) {
        warn(MessageFormatter.format(format, arguments).getMessage());
    }

    public void warn(String msg, Throwable t) {
        client.notify(t, "warning");
    }

    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    public void warn(Marker marker, String msg) {
        // Markers not supported
    }

    public void warn(Marker marker, String format, Object arg) {
        // Markers not supported
    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        // Markers not supported
    }

    public void warn(Marker marker, String format, Object... arguments) {
        // Markers not supported
    }

    public void warn(Marker marker, String msg, Throwable t) {
        // Markers not supported
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public void error(String msg) {
        // client.notify(msg, "error");
    }

    public void error(String format, Object arg) {
        error(MessageFormatter.format(format, arg).getMessage());
    }

    public void error(String format, Object arg1, Object arg2) {
        error(MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    public void error(String format, Object... arguments) {
        error(MessageFormatter.format(format, arguments).getMessage());
    }

    public void error(String msg, Throwable t) {
        client.notify(t, "error");
    }

    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    public void error(Marker marker, String msg) {
        // Markers not supported
    }

    public void error(Marker marker, String format, Object arg) {
        // Markers not supported
    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        // Markers not supported
    }

    public void error(Marker marker, String format, Object... arguments) {
        // Markers not supported
    }

    public void error(Marker marker, String msg, Throwable t) {
        // Markers not supported
    }

    public Client getClient() {
        return client;
    }
}
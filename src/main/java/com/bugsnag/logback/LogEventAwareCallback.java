package com.bugsnag.logback;

import com.bugsnag.Report;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface LogEventAwareCallback {
	void beforeNotify(Report report, ILoggingEvent event);
}

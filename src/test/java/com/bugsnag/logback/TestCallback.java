package com.bugsnag.logback;

import com.bugsnag.Report;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Map;

public class TestCallback extends MdcCallback {

    @Override
    public void beforeNotify(Report report, ILoggingEvent event) {
        super.beforeNotify(report, event);
        assertValuePresent("id", BugsnagAppenderTest.USER_ID_VALUE, report.getUser());
        assertValuePresent("name", BugsnagAppenderTest.USER_NAME_VALUE, report.getUser());
        assertValuePresent("email", BugsnagAppenderTest.USER_EMAIL_VALUE, report.getUser());

        Map requestData = (Map) report.getMetaData().get("request");
        assertValuePresent(
                BugsnagAppenderTest.REQUEST_PROPERTY,
                BugsnagAppenderTest.REQUEST_VALUE,
                requestData);
        report.cancel();
    }

    private void assertValuePresent(String name, String expectedValue, Map map) {
        Object actualValue = map.get(name);
        if (!expectedValue.equals(actualValue)) {
            throw new IllegalStateException(
                    "Assertion failure for property: "
                            + name
                            + ", expected: "
                            + expectedValue
                            + ", but got: "
                            + actualValue);
        }
    }
}

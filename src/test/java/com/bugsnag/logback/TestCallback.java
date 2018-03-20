package com.bugsnag.logback;

import java.util.Map;

import com.bugsnag.Report;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class TestCallback extends DefaultLogEventAwareCallback {

    @Override
    public void beforeNotify(Report report, ILoggingEvent event) {
        super.beforeNotify(report, event);
        checkValue("id", BugsnagAppenderTest.USER_ID_VALUE, report.getUser());
        checkValue("name", BugsnagAppenderTest.USER_NAME_VALUE, report.getUser());
        checkValue("email", BugsnagAppenderTest.USER_EMAIL_VALUE, report.getUser());

        Map requestData = (Map) report.getMetaData().get("request");
        checkValue(
                BugsnagAppenderTest.REQUEST_PROPERTY,
                BugsnagAppenderTest.REQUEST_VALUE,
                requestData);
        report.cancel();
    }

    private void checkValue(String name, String expectedValue, Map map) {
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

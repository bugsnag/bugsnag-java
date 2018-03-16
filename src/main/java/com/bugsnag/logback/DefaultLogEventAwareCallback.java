package com.bugsnag.logback;

import java.util.HashMap;
import java.util.Map;

import com.bugsnag.Report;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class DefaultLogEventAwareCallback extends AbstractLogEventAwareCallback {

    private String userIdProperty;
    private String userNameProperty;
    private String userEmailProperty;
    private Map<String, String> tabProperties = new HashMap<String, String>();

    @Override
    public void beforeNotify(Report report, ILoggingEvent event) {
        super.beforeNotify(report, event);

        String userId = getMdcProperty(userIdProperty, event);
        if (userId != null) {
            report.setUserId(userId);
        }

        String userName = getMdcProperty(userNameProperty, event);
        if (userName != null) {
            report.setUserName(userName);
        }

        String userEmail = getMdcProperty(userEmailProperty, event);
        if (userEmail != null) {
            report.setUserEmail(userEmail);
        }

        for (Map.Entry<String, String> entry : tabProperties.entrySet()) {
            String value = getMdcProperty(entry.getKey(), event);
            if (value != null) {
                report.addToTab(entry.getValue(), entry.getKey(), value);
            }
        }
    }

    private String getMdcProperty(String name, ILoggingEvent event) {
        return name == null ? null : event.getMDCPropertyMap().get(name);
    }

    public void setUserIdProperty(String userIdProperty) {
        this.userIdProperty = userIdProperty;
    }

    public void setUserNameProperty(String userNameProperty) {
        this.userNameProperty = userNameProperty;
    }

    public void setUserEmailProperty(String userEmailProperty) {
        this.userEmailProperty = userEmailProperty;
    }

    public void setTabProperties(Map<String, String> tabProperties) {
        this.tabProperties = tabProperties;
    }
}

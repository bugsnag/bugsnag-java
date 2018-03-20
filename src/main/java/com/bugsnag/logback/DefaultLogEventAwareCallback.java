package com.bugsnag.logback;

import java.util.ArrayList;
import java.util.List;

import com.bugsnag.Report;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class DefaultLogEventAwareCallback extends AbstractLogEventAwareCallback {

    private String userIdProperty;
    private String userNameProperty;
    private String userEmailProperty;
    private List<TabConfiguration> tabConfigurations = new ArrayList<TabConfiguration>();

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

        for (TabConfiguration tabConfiguration : tabConfigurations) {
            for (String property : tabConfiguration.getProperties()) {
                String value = getMdcProperty(property, event);
                if (value != null) {
                    report.addToTab(tabConfiguration.getTab(), property, value);
                }
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

    public void addTabConfiguration(TabConfiguration tabConfiguration) {
        this.tabConfigurations.add(tabConfiguration);
    }
}

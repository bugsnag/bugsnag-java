package com.bugsnag.logback;

import com.bugsnag.Report;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;

/** Obtains data about the current user and additional tabs from MDC. */
public class MdcCallback extends AbstractLogEventAwareCallback {
    /** Property that contains the current user ID. */
    private String userIdProperty;
    /** Property that contains the current user name. */
    private String userNameProperty;
    /** Property that contains the current user email. */
    private String userEmailProperty;
    /** Configurations to obtain data for additional tabs. */
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

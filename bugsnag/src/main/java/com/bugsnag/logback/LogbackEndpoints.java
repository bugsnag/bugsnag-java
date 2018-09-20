package com.bugsnag.logback;

/**
 * Used to set endpoints in the Logback.xml file
 */
public class LogbackEndpoints {

    private String notifyEndpoint;

    private String sessionEndpoint;

    /**
     * @return Bugsnag error server endpoint
     */
    public String getNotifyEndpoint() {
        return notifyEndpoint;
    }

    /**
     * @param notifyEndpoint Bugsnag error server endpoint
     */
    public void setNotifyEndpoint(String notifyEndpoint) {
        this.notifyEndpoint = notifyEndpoint;
    }

    /**
     * @return Bugsnag session server endpoint
     */
    public String getSessionEndpoint() {
        return sessionEndpoint;
    }

    /**
     * @param sessionEndpoint Bugsnag session server endpoint
     */
    public void setSessionEndpoint(String sessionEndpoint) {
        this.sessionEndpoint = sessionEndpoint;
    }
}

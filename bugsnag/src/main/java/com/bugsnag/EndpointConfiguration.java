package com.bugsnag;

public class EndpointConfiguration {

    String DEFAULT_NOTIFY_ENDPOINT = "https://notify.bugsnag.com";
    String DEFAULT_SESSION_ENDPOINT = "https://sessions.bugsnag.com";
    String HUB_NOTIFY_ENDPOINT = "https://notify.insighthub.smartbear.com";
    String HUB_SESSION_ENDPOINT = "https://sessions.insighthub.smartbear.com";
    String HUB_KEY_PREFIX = "00000";
    public String notifyEndpoint = "";
    public String sessionEndpoint = "";
    public EndpointConfiguration() {}
    public EndpointConfiguration(String notify, String sessions) throws IllegalArgumentException {
        if (notify == null || sessions == null) {
            throw new IllegalArgumentException("Endpoints cannot be null");
        }
        this.notifyEndpoint = notify;
        this.sessionEndpoint = sessions;
    }

    public void configureDefaultEndpoints(String apiKey) {
        if(!notifyEndpoint.isEmpty() && !sessionEndpoint.isEmpty()) {
            return;
        }
        if (apiKey != null && apiKey.startsWith(HUB_KEY_PREFIX)) {
            notifyEndpoint = HUB_NOTIFY_ENDPOINT;
            sessionEndpoint = HUB_SESSION_ENDPOINT;
        } else {
            notifyEndpoint = DEFAULT_NOTIFY_ENDPOINT;
            sessionEndpoint = DEFAULT_SESSION_ENDPOINT;
        }
    }
}

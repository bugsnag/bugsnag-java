package com.bugsnag;

public class EndpointConfiguration {

    private static final String DEFAULT_NOTIFY_ENDPOINT = "https://notify.bugsnag.com";
    private static final String DEFAULT_SESSION_ENDPOINT = "https://sessions.bugsnag.com";
    private static final String HUB_NOTIFY_ENDPOINT = "https://notify.insighthub.smartbear.com";
    private static final String HUB_SESSION_ENDPOINT = "https://sessions.insighthub.smartbear.com";
    private static final String HUB_KEY_PREFIX = "00000";

    private final String notifyEndpoint;
    private final String sessionEndpoint;

    /**
     * Constructs an EndpointConfiguration with the specified notify and session endpoints.
     *
     * @param notify   the notify endpoint URL
     * @param sessions the session endpoint URL
     * @throws IllegalArgumentException if either endpoint is null
     */
    public EndpointConfiguration(String notify, String sessions) {
        if (notify == null || sessions == null) {
            throw new IllegalArgumentException("Endpoints cannot be null");
        }
        this.notifyEndpoint = notify;
        this.sessionEndpoint = sessions;
    }

    /**
     * Constructs an EndpointConfiguration with default notify and session endpoints.
     */
    public static EndpointConfiguration fromApiKey(String apiKey) {
        if (apiKey != null && apiKey.startsWith(HUB_KEY_PREFIX)) {
            return new EndpointConfiguration(HUB_NOTIFY_ENDPOINT, HUB_SESSION_ENDPOINT);
        } else {
            return new EndpointConfiguration(DEFAULT_NOTIFY_ENDPOINT, DEFAULT_SESSION_ENDPOINT);
        }
    }

    public String getNotifyEndpoint() {
        return notifyEndpoint;
    }

    public String getSessionEndpoint() {
        return sessionEndpoint;
    }
}

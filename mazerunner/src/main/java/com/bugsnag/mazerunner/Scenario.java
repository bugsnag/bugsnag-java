package com.bugsnag.mazerunner;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;


public abstract class Scenario {

    private static final Logger LOGGER = Logger.getLogger(Scenario.class);

    protected Bugsnag bugsnag;

    public Scenario() {

        String apiKey = "YOUR-API-KEY";
        if (!StringUtils.isEmpty(System.getProperty("BUGSNAG_API_KEY"))) {
            apiKey = System.getProperty("BUGSNAG_API_KEY");
        }

        String path = "http://localhost:9339";
        if (!StringUtils.isEmpty(System.getProperty("MOCK_API_PATH"))) {
            path = System.getProperty("MOCK_API_PATH");
        }

        LOGGER.info("using " + path + " to send Bugsnags");

        bugsnag = new Bugsnag(apiKey);
        bugsnag.setEndpoint(path);
    }

    public abstract void run();

    /**
     * Returns a throwable with the message as the current classname
     */
    protected Throwable generateException(){
        return new RuntimeException(getClass().getSimpleName());
    }
}

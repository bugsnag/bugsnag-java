package com.bugsnag;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.logback.ProxyConfiguration;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Test for the Bugsnag Appender
 * NOTE: Not called BugsnagAppenderTest because that throws away errors to prevent cycles
 */
public class AppenderTest {

    private static final Logger LOGGER = Logger.getLogger(AppenderTest.class);

    private static final org.slf4j.Logger SLF_LOGGER = LoggerFactory.getLogger(AppenderTest.class);

    private StubNotificationDelivery delivery;
    private StubSessionDelivery sessionDelivery;
    private Delivery originalDelivery;
    private Delivery originalSessionDelivery;
    private BugsnagAppender appender;

    /**
     * Create a new test delivery and assign it to the Bugsnag client
     */
    @Before
    public void swapDelivery() {

        ch.qos.logback.classic.Logger rootLogger =
                (ch.qos.logback.classic.Logger) LoggerFactory
                        .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        appender = (BugsnagAppender) rootLogger.getAppender("BUGSNAG");

        Bugsnag bugsnag = appender.getClient();
        originalDelivery = bugsnag.getDelivery();
        delivery = new StubNotificationDelivery();
        bugsnag.setDelivery(delivery);

        originalSessionDelivery = bugsnag.getSessionDelivery();
        sessionDelivery = new StubSessionDelivery();
        bugsnag.setSessionDelivery(sessionDelivery);
    }

    /**
     * Restore the previous delivery objects after the test
     */
    @After
    public void revertDelivery() {
        Bugsnag bugsnag = appender.getClient();
        bugsnag.setDelivery(originalDelivery);
        bugsnag.setSessionDelivery(originalSessionDelivery);
    }

    @Test
    public void testSimpleException() {

        // Send a test log
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Send a log with no exception
        LOGGER.warn("Test log with no exception");

        // Check that one report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());

        // Check the correct event was created
        Notification notification = delivery.getNotifications().get(0);
        assertEquals("test", notification.getEvents().get(0).getExceptionMessage());
        assertEquals(Severity.WARNING.getValue(), notification.getEvents().get(0).getSeverity());
        assertEquals("Test exception",
                getMetaDataMap(notification, "Log event data").get("Message"));
    }

    @Test
    public void testFormattedMessage() {
        int value = 1234;

        // Send a test log
        SLF_LOGGER.warn("Test exception, errorCode: {}", value, new RuntimeException("test"));

        String message = "no exception";
        // Send a log with no exception
        SLF_LOGGER.warn("Test log with {}", message);

        // Check that one report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());

        // Check the correct event was created
        Notification notification = delivery.getNotifications().get(0);
        assertEquals("test", notification.getEvents().get(0).getExceptionMessage());
        assertEquals(Severity.WARNING.getValue(), notification.getEvents().get(0).getSeverity());
        assertEquals("Test exception, errorCode: " + value,
                getMetaDataMap(notification, "Log event data").get("Message"));
    }

    @Test
    public void testExceptionSeverities() {

        // Send a test log
        LOGGER.info("Test exception", new RuntimeException("test"));
        LOGGER.warn("Test exception", new RuntimeException("test"));
        LOGGER.error("Test exception", new RuntimeException("test"));

        // Check that three reports were sent to Bugsnag
        assertEquals(3, delivery.getNotifications().size());

        // Check the correct event was created
        Notification notification = delivery.getNotifications().get(0);
        assertEquals(Severity.INFO.getValue(), notification.getEvents().get(0).getSeverity());

        notification = delivery.getNotifications().get(1);
        assertEquals(Severity.WARNING.getValue(), notification.getEvents().get(0).getSeverity());

        notification = delivery.getNotifications().get(2);
        assertEquals(Severity.ERROR.getValue(), notification.getEvents().get(0).getSeverity());
    }

    @Test
    public void testBugsnagConfig() {

        // Get the Bugsnag instance
        Configuration config = getConfig(appender.getClient());
        assertEquals("test", config.releaseStage);
        assertEquals("1.0.1", config.appVersion);
        assertEquals("gradleTask", config.appType);
        assertFalse(config.shouldAutoCaptureSessions());

        assertEquals(8, config.redactedKeys.length);
        List<Pattern> redactedKeys = new ArrayList<Pattern>(Arrays.asList(config.redactedKeys));
        assertTrue(containsPattern(redactedKeys, "password"));
        assertTrue(containsPattern(redactedKeys, "credit_card_number"));

        assertEquals(4, config.filters.length);
        List<String> filters = new ArrayList<String>(Arrays.asList(config.filters));
        assertTrue(filters.contains("password"));
        assertTrue(filters.contains("secret"));

        assertEquals(2, config.ignoreClasses.length);
        ArrayList<String> ignoreClasses
                = new ArrayList<String>(Arrays.asList(config.ignoreClasses));
        assertTrue(ignoreClasses.contains("com.example.Custom"));
        assertTrue(ignoreClasses.contains("java.io.IOException"));

        assertEquals(2, config.notifyReleaseStages.length);
        ArrayList<String> notifyReleaseStages
                = new ArrayList<String>(Arrays.asList(config.notifyReleaseStages));
        assertTrue(notifyReleaseStages.contains("development"));
        assertTrue(notifyReleaseStages.contains("test"));

        assertEquals(2, config.projectPackages.length);
        ArrayList<String> projectPackages
                = new ArrayList<String>(Arrays.asList(config.projectPackages));
        assertTrue(projectPackages.contains("com.company.package2"));
        assertTrue(projectPackages.contains("com.company.package1"));

        assertTrue(config.sendThreads);
    }

    @Test
    public void testExcludedClasses() {
        RuntimeException exception = new RuntimeException("test");
        StackTraceElement[] trace = exception.getStackTrace();

        // Send logs with stack traces containing excluded classes
        trace[0] = changeClassName(trace[0], "com.bugsnag.Bugsnag");
        exception.setStackTrace(trace);
        LOGGER.warn("Test exception", exception);

        trace[0] = changeClassName(trace[0], "com.bugsnag.delivery.OutputStreamDelivery");
        exception.setStackTrace(trace);
        LOGGER.warn("Test exception", exception);

        trace[0] = changeClassName(trace[0], "com.bugsnag.delivery.SyncHttpDelivery");
        exception.setStackTrace(trace);
        LOGGER.warn("Test exception", exception);

        // Check that no reports were sent to Bugsnag
        assertEquals(0, delivery.getNotifications().size());
    }

    @Test
    public void testIgnoreClasses() {
        // Send an ignored exception class
        LOGGER.warn("Exception class ignored", new java.io.IOException());

        // Check that no reports were sent to Bugsnag
        assertEquals(0, delivery.getNotifications().size());

        // Send a test log
        LOGGER.warn("Release stage ignored", new RuntimeException("test"));

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());
    }

    @Test
    public void testNotifyReleaseStages() {
        // Send a log with the release stage set to an excluded one
        appender.setReleaseStage("ignoredReleaseStage");
        LOGGER.warn("Release stage ignored", new RuntimeException("test"));

        // Check that no reports were sent to Bugsnag
        assertEquals(0, delivery.getNotifications().size());

        // Reset the release stage and send another log
        appender.setReleaseStage("test");
        LOGGER.warn("Release stage notified", new RuntimeException("test"));

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());
    }

    @Test
    public void testProjectPackages() {
        // Create an exception including classes within the project packages list
        RuntimeException exception = new RuntimeException("test");
        StackTraceElement[] trace = exception.getStackTrace();
        trace[0] = changeClassName(trace[0], "com.company.package1.Class1");
        trace[1] = changeClassName(trace[1], "com.company.package2.Class2");
        exception.setStackTrace(trace);

        // Log with project packages set
        LOGGER.warn("Exception with project packages", exception);

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);

        List<Stackframe> frames = report.getExceptions().get(0).getStacktrace();
        assertTrue(frames.get(0).isInProject());
        assertTrue(frames.get(1).isInProject());
        for (int i = 2; i < frames.size(); i++) {
            assertFalse(frames.get(i).isInProject());
        }
    }

    @Test
    public void testAppVersion() {
        // Send a log message
        LOGGER.warn("Exception with project packages", new RuntimeException("test"));

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());
        Notification notification = delivery.getNotifications().get(0);

        assertEquals("1.0.1", notification.getEvents().get(0).getApp().get("version"));
    }

    @Test
    public void testAppType() {
        // Send a log message
        LOGGER.warn("Exception with project packages", new RuntimeException("test"));

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());
        Notification notification = delivery.getNotifications().get(0);

        assertEquals("gradleTask", notification.getEvents().get(0).getApp().get("type"));
    }

    @Test
    public void testFilters() {

        // Add some meta data which should be redacted by key name
        Bugsnag.addThreadMetaData("myTab", "password", "password value");
        Bugsnag.addThreadMetaData("myTab", "credit_card_number", "card number");
        Bugsnag.addThreadMetaData("myTab", "mysecret", "not redacted");

        // Send a log message
        LOGGER.warn("Exception with redacted meta data", new RuntimeException("test"));

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());

        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("myTab"));
        Map<String, Object> myTab = getMetaDataMap(notification, "myTab");

        assertEquals("[REDACTED]", myTab.get("password"));
        assertEquals("[REDACTED]", myTab.get("credit_card_number"));
        assertEquals("[REDACTED]", myTab.get("mysecret"));
    }

    @Test
    public void testCallback() {

        // Setup a callback to set the user
        appender.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setUserName("User Name");
                report.setUserEmail("user@example.com");
                report.setUserId("12345");

                report.setContext("the context");

                report.setGroupingHash("the grouping hash");

                report.setApiKey("newapikey");
            }
        });

        // Send a log message
        LOGGER.warn("Exception with redacted meta data", new RuntimeException("test"));

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());

        Notification notification = delivery.getNotifications().get(0);
        assertEquals("User Name", notification.getEvents().get(0).getUser().get("name"));
        assertEquals("user@example.com", notification.getEvents().get(0).getUser().get("email"));
        assertEquals("12345", notification.getEvents().get(0).getUser().get("id"));

        assertEquals("the context", notification.getEvents().get(0).getContext());
        assertEquals("the grouping hash", notification.getEvents().get(0).getGroupingHash());
        assertEquals("newapikey", notification.getEvents().get(0).getApiKey());
    }

    @Test
    public void testEndpoints() {
        appender.setEndpoint("https://notify.example.com");

        assertEquals("https://notify.example.com", delivery.getEndpoint());
    }

    @Test
    public void testProxy() {
        ProxyConfiguration proxy = new ProxyConfiguration();
        proxy.setType(Proxy.Type.HTTP);
        proxy.setHostname("127.0.0.1");
        proxy.setPort(8080);
        appender.setProxy(proxy);

        assertEquals("/127.0.0.1:8080", delivery.getProxy().address().toString());
        assertEquals("/127.0.0.1:8080", sessionDelivery.getProxy().address().toString());
    }

    @Test
    public void testSendThreads() {
        // Send a log message
        LOGGER.warn("Exception with threads", new RuntimeException("test"));

        // Check that a report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());
        Notification notification = delivery.getNotifications().get(0);

        assertTrue(notification.getEvents().get(0).getThreads().size() > 0);
    }

    @Test
    public void testSplit() {
        assertTrue(appender.split(null).isEmpty());
        assertArrayEquals(new String[]{""}, appender.split("").toArray());

        String[] expected = {"one", "two", "three"};
        assertArrayEquals(expected, appender.split("one,two,three").toArray());
    }

    @Test
    public void testCreateFromExistingClient() {
        Bugsnag client = new Bugsnag("testApiKey");
        BugsnagAppender appender = new BugsnagAppender(client);
        // Make sure configuration changes are not passed through to the provided client.
        appender.setApiKey("newApiKey");
        // Make sure a new client is not created when starting the appender.
        appender.start();

        assertEquals(client, appender.getClient());
        assertEquals("testApiKey", appender.getClient().getConfig().apiKey);
    }

    private StackTraceElement changeClassName(StackTraceElement element, String className) {
        return new StackTraceElement(className,
                element.getFileName(),
                element.getMethodName(),
                element.getLineNumber());
    }

    private Configuration getConfig(Bugsnag bugsnag) {
        try {
            Field field = bugsnag.getClass().getDeclaredField("config");
            field.setAccessible(true);
            return (Configuration) field.get(bugsnag);
        } catch (java.lang.Exception ex) {
            return null;
        }
    }

    private SessionTracker getSessionTracker(Bugsnag bugsnag) {
        try {
            Field field = bugsnag.getClass().getDeclaredField("sessionTracker");
            field.setAccessible(true);
            return (SessionTracker) field.get(bugsnag);
        } catch (java.lang.Exception ex) {
            return null;
        }
    }

    /**
     * Gets a hashmap key from the meta data in a notification
     *
     * @param notification The notification
     * @param key The key to get
     * @return The hash map
     */
    @SuppressWarnings (value = "unchecked")
    private Map<String, Object> getMetaDataMap(Notification notification, String key) {
        return ((Map<String, Object>) notification.getEvents().get(0).getMetaData().get(key));
    }

    /**
    * Checks if the given list of objects contains a pattern with the specified regex and flags.
    *
    * @param patterns the list of objects to search through
    * @param regex the regular expression string to match
    * @param flags the match flags, a bit mask that may include #CASE_INSENSITIVE, #LITERAL}, etc.
    * @return {@code true} if a pattern with the specified regex and flags is found in the list, {@code false} otherwise
    */
    private static boolean containsPattern(List<Pattern> patterns, String regex) {
        for (Pattern pattern : patterns) {
            if (pattern.pattern().equals(regex)) {
                return true;
            }
        }
        return false;
    }
}

package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.delivery.OutputStreamDelivery;
import com.bugsnag.serialization.Serializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import java.net.InetSocketAddress;
import java.net.Proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class BugsnagTest {

    private Bugsnag bugsnag;

    /**
     * Create a new test Bugsnag client
     */
    @Before
    public void initBugsnag() {
        bugsnag = new Bugsnag("apikey");
    }

    /**
     * Close test Bugsnag
     */
    @After
    public void closeBugsnag() {
        bugsnag.close();
    }

    @Test
    public void testNoDeliveryFails() {
        bugsnag.setDelivery(null);

        boolean result = bugsnag.notify(new RuntimeException());
        assertFalse(result);
    }

    @Test
    public void testIgnoreClasses() {
        bugsnag.setDelivery(BugsnagTestUtils.generateDelivery());

        // Ignore neither
        bugsnag.setDiscardClasses();
        assertTrue(bugsnag.notify(new RuntimeException()));
        assertTrue(bugsnag.notify(new TestException()));

        // Ignore just RuntimeException (compile pattern for exact match)
        bugsnag.setDiscardClasses(Pattern.compile(Pattern.quote(RuntimeException.class.getName())));
        assertFalse(bugsnag.notify(new RuntimeException()));
        assertTrue(bugsnag.notify(new TestException()));

        // Ignore both (compile patterns for exact matches)
        bugsnag.setDiscardClasses(
            Pattern.compile(Pattern.quote(RuntimeException.class.getName())),
            Pattern.compile(Pattern.quote(TestException.class.getName()))
        );
        assertFalse(bugsnag.notify(new RuntimeException()));
        assertFalse(bugsnag.notify(new TestException()));
    }

    @Test
    public void testEnabledReleaseStages() {
        bugsnag.setDelivery(BugsnagTestUtils.generateDelivery());

        bugsnag.setReleaseStage("production");

        // Never send
        bugsnag.setEnabledReleaseStages();
        assertFalse(bugsnag.notify(new Throwable()));

        // Ignore 'production'
        bugsnag.setEnabledReleaseStages("staging");
        assertFalse(bugsnag.notify(new Throwable()));

        // Allow 'production'
        bugsnag.setEnabledReleaseStages("production");
        assertTrue(bugsnag.notify(new Throwable()));

        // Allow 'production' and others
        bugsnag.setEnabledReleaseStages("production");
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testProjectPackages() {
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertTrue(event.getErrors().get(0).getStacktrace().get(0).isInProject());
            }

            @Override
            public void close() {
            }
        });
        bugsnag.setProjectPackages("com.bugsnag");

        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testAppVersion() {
        bugsnag.setAppVersion("1.2.3");
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("1.2.3", event.getApp().get("version"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testAppType() {
        bugsnag.setAppType("testtype");
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("testtype", event.getApp().get("type"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testSeverity() {
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals(Severity.INFO.getValue(), event.getSeverity());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable(), Severity.INFO));
    }

    @Test
    public void testRedactedKeys() {
        bugsnag.setRedactedKeys("testredact1", "testredact2");
        bugsnag.setDelivery(new Delivery() {
            @SuppressWarnings("unchecked")
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                Map<String, Object> firstTab =
                        (Map<String, Object>) event.getMetadata().get("firsttab");
                final Map<String, Object> secondTab =
                        (Map<String, Object>) event.getMetadata().get("secondtab");
                assertEquals("[REDACTED]", firstTab.get("testredact1"));
                assertEquals("[REDACTED]", firstTab.get("testredact2"));
                assertEquals("secretpassword", firstTab.get("testredact3"));
                assertEquals("[REDACTED]", secondTab.get("testredact1"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable(), report -> {
            report.addMetadata("firsttab", "testredact1", "secretpassword");
            report.addMetadata("firsttab", "testredact2", "secretpassword");
            report.addMetadata("firsttab", "testredact3", "secretpassword");
            report.addMetadata("secondtab", "testredact1", "secretpassword");
            return true;
        }));
    }

    @Test
    public void testRedactHeaders() {
        bugsnag.setDelivery(new Delivery() {
            @SuppressWarnings("unchecked")
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                Map<String, Object> requestTab =
                        (Map<String, Object>) event.getMetadata().get("request");

                Map<String, Object> headersMap =
                        (Map<String, Object>) requestTab.get("headers");

                assertEquals("[REDACTED]", headersMap.get("Authorization"));
                assertEquals("User:Password", headersMap.get("authorization"));
                assertEquals("[REDACTED]", headersMap.get("Cookie"));
                assertEquals("123456ABCDEF", headersMap.get("cookie"));
            }

            @Override
            public void close() {
            }
        });

        assertTrue(bugsnag.notify(new Throwable(), report -> {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "User:Password");
            headers.put("authorization", "User:Password");
            headers.put("Cookie", "123456ABCDEF");
            headers.put("cookie", "123456ABCDEF");

            report.addMetadata("request", "headers", headers);
            return true;
        }));
    }

    @Test
    public void testUser() {
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("123", event.getUser().get("id"));
                assertEquals("test@example.com", event.getUser().get("email"));
                assertEquals("test name", event.getUser().get("name"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable(), report -> {
            report.setUser("123", "test@example.com", "test name");
            return true;
        }));
    }

    @Test
    public void testContext() {
        bugsnag.addCallback(report -> {
            report.setContext("the context");
            return true;
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("the context", event.getContext());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testGroupingHash() {
        bugsnag.addCallback(report -> {
            report.setGroupingHash("the grouping hash");
            return true;
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("the grouping hash", event.getGroupingHash());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testSingleCallback() {
        bugsnag.addCallback(report -> {
            report.setApiKey("newapikey");
            return true;
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("newapikey", event.getApiKey());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testSingleCallbackInNotify() {
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("newapikey", event.getApiKey());
            }

            @Override
            public void close() {
            }
        });

        assertTrue(bugsnag.notify(new Throwable(), report -> {
            report.setApiKey("newapikey");
            return true;
        }));
    }

    @Test
    public void testCallbackOrder() {
        bugsnag.addCallback(report -> {
            report.setApiKey("newapikey");
            return true;
        });
        bugsnag.addCallback(report -> {
            report.setApiKey("secondnewapikey");
            return true;
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertEquals("secondnewapikey", event.getApiKey());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testCallbackCancel() {
        bugsnag.setDelivery(BugsnagTestUtils.generateDelivery());
        bugsnag.addCallback(report -> {
            report.cancel();
            return true; // cancellation flag respected
        });
        // Test the report is not sent
        assertFalse(bugsnag.notify(new Throwable()));
    }

    @SuppressWarnings("deprecation") // ensures deprecated setEndpoint method still works correctly
    @Test
    public void testEndpoint() {
        bugsnag.setDelivery(new HttpDelivery() {
            private String endpoint;

            @Override
            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }

            @Override
            public void setTimeout(int timeout) {
            }

            @Override
            public void setProxy(Proxy proxy) {
            }

            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                assertEquals("https://www.example.com", endpoint);
            }

            @Override
            public void close() {
            }
        });
        bugsnag.setEndpoints(new EndpointConfiguration("https://www.example.com", ""));

        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testProxy() {
        bugsnag.setDelivery(new HttpDelivery() {
            private Proxy proxy;

            @Override
            public void setEndpoint(String endpoint) {
            }

            @Override
            public void setTimeout(int timeout) {
            }

            @Override
            public void setProxy(Proxy proxy) {
                this.proxy = proxy;
            }

            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                assertEquals("/127.0.0.1:8080", proxy.address().toString());
            }

            @Override
            public void close() {
            }
        });
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080));
        bugsnag.setProxy(proxy);

        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testSendThreads() {
        bugsnag.setSendThreads(true);
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                // There is information about at least one thread
                assertTrue(event.getThreads().size() > 0);
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testHandledIncrementNoSession() {
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);
                assertNull(event.getSession());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testHandledIncrementWithSession() {
        bugsnag.startSession();
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Event event = ((Notification) object).getEvents().get(0);

                Map<String, Object> session = event.getSession();
                assertNotNull(session);

                @SuppressWarnings("unchecked")
                Map<String, Object> handledCounts = (Map<String, Object>) session.get("events");
                assertEquals(1, handledCounts.get("handled"));
                assertEquals(0, handledCounts.get("unhandled"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testMultipleHandledIncrementWithSession() {
        bugsnag.startSession();
        StubNotificationDelivery testDelivery = new StubNotificationDelivery();
        bugsnag.setDelivery(testDelivery);

        assertTrue(bugsnag.notify(new Throwable()));
        assertTrue(bugsnag.notify(new Throwable()));
        assertTrue(bugsnag.notify(new Throwable()));

        for (int i = 0; i < testDelivery.getNotifications().size(); i++) {
            Event event = testDelivery.getNotifications().get(i).getEvents().get(0);

            Map<String, Object> session = event.getSession();
            assertNotNull(session);

            @SuppressWarnings("unchecked")
            Map<String, Object> handledCounts = (Map<String, Object>) session.get("events");
            assertEquals(i + 1, handledCounts.get("handled"));
            assertEquals(0, handledCounts.get("unhandled"));
        }
    }

    @Test
    public void testSerialization() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        bugsnag.setDelivery(new OutputStreamDelivery(byteStream));
        bugsnag.notify(new RuntimeException());

        // Exact content will vary with stacktrace so just check for some content
        assertTrue(new String(byteStream.toByteArray()).length() > 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUncaughtHandlerModification() {
        Set<Bugsnag> bugsnags = Bugsnag.uncaughtExceptionClients();
        bugsnags.clear();
    }

    // Test exception class
    private class TestException extends RuntimeException {
        private static final long serialVersionUID = -458298914160798211L;
    }
}

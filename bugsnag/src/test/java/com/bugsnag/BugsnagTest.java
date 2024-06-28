package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.bugsnag.callbacks.Callback;
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
        bugsnag.setIgnoreClasses();
        assertTrue(bugsnag.notify(new RuntimeException()));
        assertTrue(bugsnag.notify(new TestException()));

        // Ignore just RuntimeException
        bugsnag.setIgnoreClasses(RuntimeException.class.getName());
        assertFalse(bugsnag.notify(new RuntimeException()));
        assertTrue(bugsnag.notify(new TestException()));

        // Ignore both
        bugsnag.setIgnoreClasses(RuntimeException.class.getName(), TestException.class.getName());
        assertFalse(bugsnag.notify(new RuntimeException()));
        assertFalse(bugsnag.notify(new TestException()));
    }

    @Test
    public void testNotifyReleaseStages() {
        bugsnag.setDelivery(BugsnagTestUtils.generateDelivery());

        bugsnag.setReleaseStage("production");

        // Never send
        bugsnag.setNotifyReleaseStages();
        assertFalse(bugsnag.notify(new Throwable()));

        // Ignore 'production'
        bugsnag.setNotifyReleaseStages("staging", "development");
        assertFalse(bugsnag.notify(new Throwable()));

        // Allow 'production'
        bugsnag.setNotifyReleaseStages("production");
        assertTrue(bugsnag.notify(new Throwable()));

        // Allow 'production' and others
        bugsnag.setNotifyReleaseStages("production", "staging", "development");
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testProjectPackages() {
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                assertTrue(report.getExceptions().get(0).getStacktrace().get(0).isInProject());
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
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("1.2.3", report.getApp().get("version"));
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
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("testtype", report.getApp().get("type"));
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
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals(Severity.INFO.getValue(), report.getSeverity());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable(), Severity.INFO));
    }

    @Test
    public void testRedactedKeys() {
        bugsnag.setRedactedKeys("testredacted1", "testredacted2");
        bugsnag.setDelivery(new Delivery() {
            @SuppressWarnings("unchecked")
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                Map<String, Object> firstTab =
                        (Map<String, Object>) report.getRedactedMetaData().get("firsttab");
                final Map<String, Object> secondTab =
                        (Map<String, Object>) report.getRedactedMetaData().get("secondtab");
                assertEquals("[REDACTED]", firstTab.get("testredacted1"));
                assertEquals("[REDACTED]", firstTab.get("testredacted2"));
                assertEquals("secretpassword", firstTab.get("testredacted3"));
                assertEquals("[REDACTED]", secondTab.get("testredacted1"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("firsttab", "testredacted1", "secretpassword");
                report.addToTab("firsttab", "testredacted2", "secretpassword");
                report.addToTab("firsttab", "testredacted3", "secretpassword");
                report.addToTab("secondtab", "testredacted1", "secretpassword");
            }
        }));
    }

    @Test
    public void testRedactedHeaders() {
        bugsnag.setDelivery(new Delivery() {
            @SuppressWarnings("unchecked")
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                Map<String, Object> requestTab =
                        (Map<String, Object>) report.getRedactedMetaData().get("request");

                Map<String, Object> headersMap =
                        (Map<String, Object>) requestTab.get("headers");

                assertEquals("[REDACTED]", headersMap.get("Authorization"));
                assertEquals("[REDACTED]", headersMap.get("authorization"));
                assertEquals("[REDACTED]", headersMap.get("Authentication"));
                assertEquals("[REDACTED]", headersMap.get("authentication"));
            }

            @Override
            public void close() {
            }
        });

        assertTrue(bugsnag.notify(new Throwable(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "User:Password");
                headers.put("authorization", "User:Password");
                headers.put("Cookie", "123456ABCDEF");
                headers.put("cookie", "123456ABCDEF");

                report.addToTab("request", "headers", headers);
            }
        }));
    }

    @Test
    public void testFilters() {
        bugsnag.setFilters("testredacted1", "testredacted2");
        bugsnag.setDelivery(new Delivery() {
            @SuppressWarnings("unchecked")
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                Map<String, Object> firstTab =
                        (Map<String, Object>) report.getFilteredMetaData().get("firsttab");
                final Map<String, Object> secondTab =
                        (Map<String, Object>) report.getFilteredMetaData().get("secondtab");
                assertEquals("[FILTERED]", firstTab.get("testfilter1"));
                assertEquals("[FILTERED]", firstTab.get("testfilter2"));
                assertEquals("secretpassword", firstTab.get("testfilter3"));
                assertEquals("[FILTERED]", secondTab.get("testfilter1"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("firsttab", "testfilter1", "secretpassword");
                report.addToTab("firsttab", "testfilter2", "secretpassword");
                report.addToTab("firsttab", "testfilter3", "secretpassword");
                report.addToTab("secondtab", "testfilter1", "secretpassword");
            }
        }));
    }

    @Test
    public void testFilterHeaders() {
        bugsnag.setDelivery(new Delivery() {
            @SuppressWarnings("unchecked")
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                Map<String, Object> requestTab =
                        (Map<String, Object>) report.getFilteredMetaData().get("request");

                Map<String, Object> headersMap =
                        (Map<String, Object>) requestTab.get("headers");

                assertEquals("[FILTERED]", headersMap.get("Authorization"));
                assertEquals("user:Password", headersMap.get("authorization"));
                assertEquals("[FILTERED]", headersMap.get("Cookie"));
                assertEquals("123456ABCDEF", headersMap.get("cookie"));
            }

            @Override
            public void close() {
            }
        });

        assertTrue(bugsnag.notify(new Throwable(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "User:Password");
                headers.put("authorization", "User:Password");
                headers.put("Cookie", "123456ABCDEF");
                headers.put("cookie", "123456ABCDEF");

                report.addToTab("request", "headers", headers);
            }
        }));
    }

    @Test
    public void testUser() {
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("123", report.getUser().get("id"));
                assertEquals("test@example.com", report.getUser().get("email"));
                assertEquals("test name", report.getUser().get("name"));
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setUser("123", "test@example.com", "test name");
            }
        }));
    }

    @Test
    public void testContext() {
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setContext("the context");
            }
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("the context", report.getContext());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testGroupingHash() {
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setGroupingHash("the grouping hash");
            }
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("the grouping hash", report.getGroupingHash());
            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testSingleCallback() {
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setApiKey("newapikey");
            }
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("newapikey", report.getApiKey());
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
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("newapikey", report.getApiKey());
            }

            @Override
            public void close() {
            }
        });

        assertTrue(bugsnag.notify(new Throwable(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setApiKey("newapikey");
            }
        }));
    }

    @Test
    public void testCallbackOrder() {
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setApiKey("newapikey");
            }
        });
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setApiKey("secondnewapikey");
            }
        });
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                assertEquals("secondnewapikey", report.getApiKey());
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
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.cancel();
            }
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
        bugsnag.setEndpoints("https://www.example.com", null);

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
                Report report = ((Notification) object).getEvents().get(0);
                // There is information about at least one thread
                assertTrue(report.getThreads().size() > 0);
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
                Report report = ((Notification) object).getEvents().get(0);
                assertNull(report.getSession());
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
                Report report = ((Notification) object).getEvents().get(0);

                Map<String, Object> session = report.getSession();
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
            Report report = testDelivery.getNotifications().get(i).getEvents().get(0);

            Map<String, Object> session = report.getSession();
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

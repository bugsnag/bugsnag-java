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

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;


public class BugsnagTest {

    @Test
    public void testNoDeliveryFails() {
        Bugsnag bugsnag = new Bugsnag("apikey");
        bugsnag.setDelivery(null);

        boolean result = bugsnag.notify(new RuntimeException());
        assertFalse(result);
    }

    @Test
    public void testIgnoreClasses() {
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
    public void testFilters() {
        Bugsnag bugsnag = new Bugsnag("apikey");
        bugsnag.setFilters("testfilter1", "testfilter2");
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);
                Map firstTab = (Map) report.getMetaData().get("firsttab");
                final Map secondTab = (Map) report.getMetaData().get("secondtab");
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
    public void testUser() {
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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

    @Test
    public void testEndpoint() {
        Bugsnag bugsnag = new Bugsnag("apikey");
        bugsnag.setDelivery(new HttpDelivery() {
            String endpoint;

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
        bugsnag.setEndpoint("https://www.example.com");

        assertTrue(bugsnag.notify(new Throwable()));
    }

    @Test
    public void testProxy() {
        Bugsnag bugsnag = new Bugsnag("apikey");
        bugsnag.setDelivery(new HttpDelivery() {
            Proxy proxy;

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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
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
        Bugsnag bugsnag = new Bugsnag("apikey");
        bugsnag.startSession();
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                Report report = ((Notification) object).getEvents().get(0);

                Map<String, Object> session = report.getSession();
                assertNotNull(session);

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
    public void testSerialization() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        Bugsnag bugsnag = new Bugsnag("apikey");
        bugsnag.setDelivery(new OutputStreamDelivery(byteStream));
        bugsnag.notify(new RuntimeException());

        // Exact content will vary with stacktrace so just check for some content
        assertTrue(new String(byteStream.toByteArray()).length() > 0);
    }

    // Test exception class
    private class TestException extends RuntimeException {
    }
}

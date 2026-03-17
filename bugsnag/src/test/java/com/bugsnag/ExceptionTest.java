package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ExceptionTest {

    private Error error;
    private RuntimeException ogThrowable;

    /**
     * Initialises a config
     *
     * @throws Throwable if config couldn't be initialised
     */
    @Before
    public void setUp() {
        Configuration config = new Configuration("api-key");
        ogThrowable = new RuntimeException("Test");
        error = new Error(config, ogThrowable);
    }

    @Test
    public void testDefaults() {
        assertEquals("java.lang.RuntimeException", error.getErrorClass());
        assertEquals("Test", error.getMessage());
        assertEquals(ogThrowable, error.getThrowable());
        assertFalse(error.getStacktrace().isEmpty());
    }

    @Test
    public void testClassOverride() {
        error.setErrorClass("Hello");
        assertEquals("Hello", error.getErrorClass());
        assertEquals("Test", error.getMessage());
    }

    @Test
    public void testReportCallback() {
        Bugsnag bugsnag = new Bugsnag("apikey");
        bugsnag.setDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {

            }

            @Override
            public void close() {
            }
        });
        assertTrue(bugsnag.notify(ogThrowable, report -> {
            try {
                assertEquals(ogThrowable, report.getException());
                assertEquals("Test", report.getExceptionMessage());
                assertEquals("java.lang.RuntimeException", report.getExceptionName());

                report.setExceptionName("Foo");
                assertEquals("Foo", report.getExceptionName());

                List<Error> errors = report.getErrors();
                assertEquals(1, errors.size());

                Error error = errors.get(0);
                assertNotNull(error);
                assertEquals("Foo", error.getErrorClass());
                assertEquals("Test", error.getMessage());
            } catch (Throwable throwable) {
                report.cancel();
            }
            return !report.getShouldCancel();
        }));

        bugsnag.close();
    }

}

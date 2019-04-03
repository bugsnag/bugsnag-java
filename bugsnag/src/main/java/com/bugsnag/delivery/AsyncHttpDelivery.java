package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncHttpDelivery implements HttpDelivery {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHttpDelivery.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;

    private HttpDelivery baseDelivery;

    private final ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setName("bugsnag-async-delivery-" + thread.getId());
            return thread;
        }
    };
    // Create an exector service which keeps idle threads alive for a maximum of SHUTDOWN_TIMEOUT.
    // This should avoid blocking an application that doesn't call shutdown from exiting.
    private ExecutorService executorService =
            new ThreadPoolExecutor(0, 1,
                    SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    threadFactory);

    private boolean shuttingDown = false;

    /**
     * Creates a new instance, which defaults to the https://notify.bugsnag.com endpoint
     */
    public AsyncHttpDelivery() {
        this(SyncHttpDelivery.DEFAULT_NOTIFY_ENDPOINT);
    }

    /**
     * Creates a new instance, which uses a custom endpoint
     */
    public AsyncHttpDelivery(String endpoint) {
        baseDelivery = new SyncHttpDelivery(endpoint);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                AsyncHttpDelivery.this.shutdown();
            }
        });
    }


    public void setEndpoint(String endpoint) {
        baseDelivery.setEndpoint(endpoint);
    }

    public void setProxy(Proxy proxy) {
        baseDelivery.setProxy(proxy);
    }

    public void setTimeout(int timeout) {
        baseDelivery.setTimeout(timeout);
    }

    public void setBaseDelivery(HttpDelivery baseDelivery) {
        this.baseDelivery = baseDelivery;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void deliver(final Serializer serializer,
                        final Object object,
                        final Map<String, String> headers) {
        if (shuttingDown) {
            LOGGER.warn("Not notifying - 'sending' threads are already shutting down");
            return;
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                baseDelivery.deliver(serializer, object, headers);
            }
        });
    }

    @Override
    public void close() {
        shutdown();
    }

    private void shutdown() {
        shuttingDown = true;
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS)) {
                LOGGER.warn("Shutdown of 'sending' threads took too long - forcing a shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            LOGGER.warn("Shutdown of 'sending' threads was interrupted - forcing a shutdown");
            executorService.shutdownNow();
        }
    }
}

package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncHttpDelivery implements HttpDelivery {
    private static final Logger logger = LoggerFactory.getLogger(AsyncHttpDelivery.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;

    private HttpDelivery baseDelivery = new SyncHttpDelivery();

    // Create an exector service which keeps idle threads alive for a maximum of SHUTDOWN_TIMEOUT.
    // This should avoid blocking an application that doesn't call shutdown from exiting.
    private ExecutorService executorService =
            new ThreadPoolExecutor(0, 1,
                    SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());

    private boolean shuttingDown = false;

    /**
     * Constructor.
     */
    public AsyncHttpDelivery() {
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
    public void deliver(final Serializer serializer, final Object object, final Map<String, String> headers) {
        if (shuttingDown) {
            logger.warn("Not notifying - 'sending' threads are already shutting down");
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
                logger.warn("Shutdown of 'sending' threads took too long - forcing a shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            logger.warn("Shutdown of 'sending' threads was interrupted - forcing a shutdown");
            executorService.shutdownNow();
        }
    }
}

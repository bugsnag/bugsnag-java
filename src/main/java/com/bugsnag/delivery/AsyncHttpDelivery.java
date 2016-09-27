package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.net.Proxy;

public class AsyncHttpDelivery implements HttpDelivery {
    private static final Logger logger = LoggerFactory.getLogger(AsyncHttpDelivery.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;

    protected HttpDelivery baseDelivery = new SyncHttpDelivery();
    protected ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean shuttingDown = false;

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

    public void deliver(final Serializer serializer, final Object object) {
        if (shuttingDown) {
            logger.warn("Not notifying - 'sending' threads are already shutting down");
            return;
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                baseDelivery.deliver(serializer, object);
            }
        });
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

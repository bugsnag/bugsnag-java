package com.bugsnag;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import org.aopalliance.intercept.MethodInterceptor;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.scheduling.TaskScheduler;

import java.util.Map;

class TestUtils {

    /**
     * Verify that a report was received, then capture and return that report
     */
    static Report verifyAndGetReport(Delivery delivery) {
        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);
        verify(delivery, timeout(100).times(1)).deliver(
                ArgumentMatchers.any(Serializer.class),
                notificationCaptor.capture(),
                anyMapOf(String.class, String.class));
        return notificationCaptor.getValue().getEvents().get(0);
    }

    static <K, V> Map<K, V> anyMapOf(Class<K> keyClazz, Class<V> valueClazz) {
        return ArgumentMatchers.anyMap();
    }

    static TaskScheduler createProxy(TaskScheduler target) {
        ProxyFactory factory = new ProxyFactory(target);
        factory.addAdvice((MethodInterceptor) invocation -> invocation.proceed());
        return (TaskScheduler) factory.getProxy();
    }
}

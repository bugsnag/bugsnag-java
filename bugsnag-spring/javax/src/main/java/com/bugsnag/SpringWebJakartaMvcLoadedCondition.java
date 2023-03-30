package com.bugsnag;

import com.bugsnag.callbacks.JakartaServletCallback;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Check whether spring-webmvc is available to the application.
 */
class SpringWebJakartaMvcLoadedCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context,
                           AnnotatedTypeMetadata metadata) {
        return context.getClassLoader() != null
                && JakartaServletCallback.isAvailable()
                && context.getClassLoader().getResource("org/springframework/web/servlet") != null;
    }
}

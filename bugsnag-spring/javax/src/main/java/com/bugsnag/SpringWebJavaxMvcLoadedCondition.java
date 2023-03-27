package com.bugsnag;

import com.bugsnag.callbacks.JavaxServletCallback;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Check whether spring-webmvc is available to the application.
 */
class SpringWebJavaxMvcLoadedCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context,
                           AnnotatedTypeMetadata metadata) {
        return context.getClassLoader() != null
                && JavaxServletCallback.isAvailable()
                && context.getClassLoader().getResource("org/springframework/web/servlet") != null;
    }
}

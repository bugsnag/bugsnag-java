package com.bugsnag;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Check whether spring-boot is available to the application.
 */
class SpringBootLoadedCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context,
                           AnnotatedTypeMetadata metadata) {

        return context.getClassLoader() != null
                && context.getClassLoader().getResource("org/springframework/boot") != null;
    }
}

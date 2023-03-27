package com.bugsnag;

import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Check whether spring-boot is available to the application.
 */
class SpringBootV3LoadedCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context,
                           AnnotatedTypeMetadata metadata) {

        return context.getClassLoader() != null
                && context.getClassLoader().getResource("org/springframework/boot") != null
                && isSpringBootV3();
    }

    private boolean isSpringBootV3() {
        String bootVersion = SpringBootVersion.getVersion();
        return bootVersion != null & bootVersion.matches("3\\..+");
    }
}

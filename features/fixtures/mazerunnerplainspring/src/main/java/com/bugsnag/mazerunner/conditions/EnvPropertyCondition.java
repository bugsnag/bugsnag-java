package com.bugsnag.mazerunner.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class EnvPropertyCondition implements Condition {

    protected abstract String getPropertyName();

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String propertyName = getPropertyName();
        String value = environment.getProperty(propertyName);
        return "true".equals(value);
    }
}

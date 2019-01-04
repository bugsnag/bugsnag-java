package com.bugsnag.mazerunner.conditions;

public class ScheduledExecutorServiceCondition extends EnvPropertyCondition {
    @Override
    protected String getPropertyName() {
        return "scheduled_executor_service_bean";
    }
}

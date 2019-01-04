package com.bugsnag.mazerunner.conditions;

public class OtherScheduledExecutorServiceCondition extends EnvPropertyCondition {
    @Override
    protected String getPropertyName() {
        return "other_scheduled_executor_service_bean";
    }
}

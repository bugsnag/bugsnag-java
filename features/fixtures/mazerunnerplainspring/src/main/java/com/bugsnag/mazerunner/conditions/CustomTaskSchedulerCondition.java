package com.bugsnag.mazerunner.conditions;

public class CustomTaskSchedulerCondition extends EnvPropertyCondition {
    @Override
    protected String getPropertyName() {
        return "custom_task_scheduler_bean";
    }
}

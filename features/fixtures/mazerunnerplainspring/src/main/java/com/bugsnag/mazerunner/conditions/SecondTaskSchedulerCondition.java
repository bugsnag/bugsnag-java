package com.bugsnag.mazerunner.conditions;

public class SecondTaskSchedulerCondition extends EnvPropertyCondition {
    @Override
    protected String getPropertyName() {
        return "second_task_scheduler_bean";
    }
}

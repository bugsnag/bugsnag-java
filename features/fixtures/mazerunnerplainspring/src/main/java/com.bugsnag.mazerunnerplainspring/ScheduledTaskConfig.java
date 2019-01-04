package com.bugsnag.mazerunnerplainspring;

import com.bugsnag.mazerunner.conditions.ScheduledExecutorServiceCondition;
import com.bugsnag.mazerunner.conditions.OtherScheduledExecutorServiceCondition;
import com.bugsnag.mazerunner.conditions.CustomTaskSchedulerCondition;
import com.bugsnag.mazerunner.conditions.SecondTaskSchedulerCondition;
import com.bugsnag.Bugsnag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class ScheduledTaskConfig {

    @Conditional(ScheduledExecutorServiceCondition.class)
    @Bean
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(4);
    }

    @Conditional(OtherScheduledExecutorServiceCondition.class)
    @Bean
    public Executor otherTaskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    @Conditional(CustomTaskSchedulerCondition.class)
    @Bean
    public TaskScheduler customTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        return scheduler;
    }

    @Conditional(SecondTaskSchedulerCondition.class)
    @Bean(name = "taskScheduler")
    public TaskScheduler secondTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        return scheduler;
    }
}

package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class ScheduledTaskConfig {

    @ConditionalOnProperty(name = "scheduled_executor_service_bean", havingValue = "true")
    @Bean
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(4);
    }

    @ConditionalOnProperty(name = "other_scheduled_executor_service_bean", havingValue = "true")
    @Bean
    public Executor otherTaskScheduler() {
        return Executors.newScheduledThreadPool(4);
    }

    @ConditionalOnProperty(name = "custom_task_scheduler_bean", havingValue = "true")
    @Bean
    public TaskScheduler customTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        return scheduler;
    }
}

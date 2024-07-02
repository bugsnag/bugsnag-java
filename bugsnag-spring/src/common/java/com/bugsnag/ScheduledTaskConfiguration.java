package com.bugsnag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import org.springframework.util.ErrorHandler;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Add configuration for reporting unhandled exceptions for scheduled tasks.
 */
@Configuration
class ScheduledTaskConfiguration implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskConfiguration.class);

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private ScheduledTaskBeanLocator beanLocator;

    /**
     * Add bugsnag error handling to a task scheduler
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        BugsnagScheduledTaskExceptionHandler bugsnagErrorHandler =
                new BugsnagScheduledTaskExceptionHandler(bugsnag);

        // Decision process for finding a TaskScheduler, in order of preference:
        //
        // 1. use the scheduler from the task registrar
        // 2. search for a TaskScheduler bean, by type, then by name
        // 3. search for a ScheduledExecutorService bean by type, then by name,
        //    and wrap it in a TaskScheduler
        // 4. create our own TaskScheduler

        TaskScheduler registrarScheduler = taskRegistrar.getScheduler();
        TaskScheduler taskScheduler = registrarScheduler != null
                ? registrarScheduler : beanLocator.resolveTaskScheduler();

        if (taskScheduler != null) {
            //check if taskSchedular is a proxy
            if (AopUtils.isAopProxy(taskScheduler)) {
                //if it's a proxy then get the target class and cast as necessary
                Class<?> targetClass = AopProxyUtils.ultimateTargetClass(taskScheduler);
                if (TaskScheduler.class.isAssignableFrom(targetClass)) {
                    taskScheduler = (TaskScheduler) AopProxyUtils.getSingletonTarget(taskScheduler);
                }
            }
            configureExistingTaskScheduler(taskScheduler, bugsnagErrorHandler);
        } else {
            ScheduledExecutorService executorService = beanLocator.resolveScheduledExecutorService();
            taskScheduler = createNewTaskScheduler(executorService, bugsnagErrorHandler);
            taskRegistrar.setScheduler(taskScheduler);
        }
    }

    private TaskScheduler createNewTaskScheduler(
            ScheduledExecutorService executorService,
            BugsnagScheduledTaskExceptionHandler errorHandler) {
        if (executorService != null) {
            // create a task scheduler which delegates to the existing Executor
            ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(executorService);
            scheduler.setErrorHandler(errorHandler);
            return scheduler;
        } else {
            // If no task scheduler has been defined by the application, create one
            // and add the bugsnag error handler.
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setErrorHandler(errorHandler);
            scheduler.initialize();
            return scheduler;
        }
    }

    /**
     * If a task scheduler has been defined by the application, get it so that
     * bugsnag error handling can be added.
     * <p>
     * Reflection is the simplest way to get and set an error handler
     * because the error handler setter is only defined in the concrete classes,
     * not the TaskScheduler interface.
     *
     * @param taskScheduler the task scheduler
     */
    private void configureExistingTaskScheduler(TaskScheduler taskScheduler,
                                                BugsnagScheduledTaskExceptionHandler errorHandler) {
        try {
            Field errorHandlerField =
                    taskScheduler.getClass().getDeclaredField("errorHandler");
            errorHandlerField.setAccessible(true);
            Object existingErrorHandler = errorHandlerField.get(taskScheduler);

            // If an error handler has already been defined then make the Bugsnag handler
            // call this afterwards
            if (existingErrorHandler instanceof ErrorHandler) {
                errorHandler.setExistingErrorHandler((ErrorHandler) existingErrorHandler);
            }

            // Add the bugsnag error handler to the scheduler.
            errorHandlerField.set(taskScheduler, errorHandler);
        } catch (Throwable ex) {
            LOGGER.warn("Bugsnag scheduled task exception handler could not be configured");
        }
    }
}

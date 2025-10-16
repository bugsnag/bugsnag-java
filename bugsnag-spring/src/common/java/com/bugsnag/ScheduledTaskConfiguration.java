package com.bugsnag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
     * Optional: if the app defines a dedicated ErrorHandler bean for scheduled tasks
     * (e.g. your @MockBean(name = "scheduledTaskErrorHandler") in tests), we can
     * still chain it when we replace or wrap the scheduler.
     */
    @Autowired(required = false)
    @Qualifier("scheduledTaskErrorHandler")
    private ErrorHandler scheduledTaskErrorHandlerBean;

    /**
     * Add Bugsnag error handling to the task scheduler being used by Spring.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        BugsnagScheduledTaskExceptionHandler bugsnagErrorHandler =
                new BugsnagScheduledTaskExceptionHandler(bugsnag);

        // Decision process for finding a TaskScheduler, in order of preference:
        // 1. use the scheduler from the task registrar
        // 2. search for a TaskScheduler bean, by type, then by name
        // 3. search for a ScheduledExecutorService bean by type, then by name, and wrap it
        // 4. create our own TaskScheduler
        TaskScheduler registrarScheduler = taskRegistrar.getScheduler();
        TaskScheduler taskScheduler = registrarScheduler != null
                ? registrarScheduler
                : beanLocator.resolveTaskScheduler();

        if (taskScheduler != null) {
            // Spring Boot 3 creates a TaskSchedulerRouter which cannot be configured.
            // In this case, create our own scheduler instead (but preserve any bean-level handler).
            String schedulerClassName = taskScheduler.getClass().getName();
            if (schedulerClassName.contains("TaskSchedulerRouter")) {
                LOGGER.info("Detected TaskSchedulerRouter, creating Bugsnag-wrapped scheduler");
                ScheduledExecutorService executorService = beanLocator.resolveScheduledExecutorService();
                chainExistingBeanHandlerIfPresent(bugsnagErrorHandler);
                taskScheduler = createNewTaskScheduler(executorService, bugsnagErrorHandler);
                taskRegistrar.setScheduler(taskScheduler);
                return;
            }

            // If it's a proxy, unwrap to the target to allow reflection / method calls.
            if (AopUtils.isAopProxy(taskScheduler)) {
                Class<?> targetClass = AopProxyUtils.ultimateTargetClass(taskScheduler);
                if (TaskScheduler.class.isAssignableFrom(targetClass)) {
                    TaskScheduler target = (TaskScheduler) AopProxyUtils.getSingletonTarget(taskScheduler);
                    if (target != null) {
                        taskScheduler = target;
                    }
                }
            }

            configureExistingTaskScheduler(taskScheduler, bugsnagErrorHandler);
        } else {
            // No scheduler has been defined by the application, create one and add the Bugsnag error handler.
            ScheduledExecutorService executorService = beanLocator.resolveScheduledExecutorService();
            chainExistingBeanHandlerIfPresent(bugsnagErrorHandler);
            taskScheduler = createNewTaskScheduler(executorService, bugsnagErrorHandler);
            taskRegistrar.setScheduler(taskScheduler);
        }
    }

    private TaskScheduler createNewTaskScheduler(
            ScheduledExecutorService executorService,
            BugsnagScheduledTaskExceptionHandler errorHandler
    ) {
        if (executorService != null) {
            // Create a task scheduler which delegates to the existing Executor
            ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(executorService);
            scheduler.setErrorHandler(errorHandler);
            return scheduler;
        } else {
            // If no task scheduler has been defined by the application, create one and add the Bugsnag error handler.
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setErrorHandler(errorHandler);
            scheduler.initialize();
            return scheduler;
        }
    }

    /**
     * If a task scheduler has been defined by the application, configure it so that Bugsnag error handling is added.
     * We first capture any existing ErrorHandler (e.g. your mock bean), chain it into the Bugsnag handler,
     * then set the Bugsnag handler via setter if available (Boot 3+) or via field reflection.
     */
    private void configureExistingTaskScheduler(
            TaskScheduler taskScheduler,
            BugsnagScheduledTaskExceptionHandler errorHandler
    ) {
        // (1) Capture whatever handler is already configured on the scheduler
        ErrorHandler existing = extractExistingErrorHandler(taskScheduler);
        if (existing != null) {
            errorHandler.setExistingErrorHandler(existing);
        } else if (scheduledTaskErrorHandlerBean != null) {
            // Fallback: chain the bean-level handler if the scheduler didn't have one yet
            errorHandler.setExistingErrorHandler(scheduledTaskErrorHandlerBean);
        }

        // (2) Install the Bugsnag handler via public setter if available, else via private field
        if (trySetErrorHandlerViaMethod(taskScheduler, errorHandler)) {
            LOGGER.info("Bugsnag scheduled task exception handler configured via setter on {}",
                    taskScheduler.getClass().getName());
            return;
        }
        if (trySetErrorHandlerViaField(taskScheduler, errorHandler)) {
            LOGGER.info("Bugsnag scheduled task exception handler configured via field on {}",
                    taskScheduler.getClass().getName());
            return;
        }

        LOGGER.warn("Bugsnag scheduled task exception handler could not be configured for scheduler type: {}",
                taskScheduler.getClass().getName());
    }

    /**
     * Chain the dedicated bean-level handler if present (useful when we replace the scheduler).
     */
    private void chainExistingBeanHandlerIfPresent(BugsnagScheduledTaskExceptionHandler errorHandler) {
        if (scheduledTaskErrorHandlerBean != null) {
            errorHandler.setExistingErrorHandler(scheduledTaskErrorHandlerBean);
        }
    }

    /**
     * Prefer a public getter if present; otherwise fall back to private field.
     */
    private ErrorHandler extractExistingErrorHandler(TaskScheduler taskScheduler) {
        // Try public getter (present on ThreadPoolTaskScheduler et al.)
        try {
            Method getter = taskScheduler.getClass().getMethod("getErrorHandler");
            Object val = getter.invoke(taskScheduler);
            if (val instanceof ErrorHandler) {
                return (ErrorHandler) val;
            }
        } catch (Throwable ignore) {
            // no-op
        }

        // Fall back to private field access
        try {
            Field fld = taskScheduler.getClass().getDeclaredField("errorHandler");
            fld.setAccessible(true);
            Object val = fld.get(taskScheduler);
            if (val instanceof ErrorHandler) {
                return (ErrorHandler) val;
            }
        } catch (Throwable ignore) {
            // no-op
        }

        return null;
    }

    private boolean trySetErrorHandlerViaMethod(TaskScheduler taskScheduler, ErrorHandler handler) {
        try {
            Method setter = taskScheduler.getClass().getMethod("setErrorHandler", ErrorHandler.class);
            setter.invoke(taskScheduler, handler);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private boolean trySetErrorHandlerViaField(TaskScheduler taskScheduler, ErrorHandler handler) {
        try {
            Field fld = taskScheduler.getClass().getDeclaredField("errorHandler");
            fld.setAccessible(true);
            fld.set(taskScheduler, handler);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}

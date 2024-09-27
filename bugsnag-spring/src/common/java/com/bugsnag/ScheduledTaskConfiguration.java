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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

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
     * Add bugsnag error handling to a task scheduler.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        BugsnagScheduledTaskExceptionHandler bugsnagErrorHandler =
                new BugsnagScheduledTaskExceptionHandler(bugsnag);

        TaskScheduler registrarScheduler = taskRegistrar.getScheduler();
        TaskScheduler taskScheduler = registrarScheduler != null
                ? registrarScheduler : beanLocator.resolveTaskScheduler();

        if (taskScheduler != null) {
            if (AopUtils.isAopProxy(taskScheduler)) {
                // If it's a proxy, get the target class and cast as necessary
                Class<?> targetClass = AopProxyUtils.ultimateTargetClass(taskScheduler);
                if (TaskScheduler.class.isAssignableFrom(targetClass)) {
                    taskScheduler = (TaskScheduler) AopProxyUtils.getSingletonTarget(taskScheduler);
                }
            }
            // Apply the error handler to the task scheduler
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
            // Create a task scheduler which delegates to the existing Executor
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
     * Configure the TaskScheduler with Bugsnag error handling.
     * Handles cases where the TaskScheduler is proxied.
     *
     * @param taskScheduler the task scheduler
     */
    private void configureExistingTaskScheduler(TaskScheduler taskScheduler,
                                                BugsnagScheduledTaskExceptionHandler errorHandler) {
        try {
            // Log the actual class of the TaskScheduler for debugging
            LOGGER.debug("TaskScheduler class: {}", taskScheduler.getClass().getName());
            Class<?> schedulerClass = taskScheduler.getClass();
            // Check if the class is one of the expected types using reflection
            if (ThreadPoolTaskScheduler.class.isAssignableFrom(schedulerClass)
                || ConcurrentTaskScheduler.class.isAssignableFrom(schedulerClass)) {
                configureErrorHandlerOnConcreteScheduler(taskScheduler, errorHandler);
            } else {
                // Try using reflection to check if the scheduler is a TaskSchedulerRouter
                TaskScheduler unwrappedScheduler = unwrapRouter(taskScheduler);
                if (unwrappedScheduler != null) {
                    configureErrorHandlerOnConcreteScheduler(unwrappedScheduler, errorHandler);
                } else {
                    LOGGER.warn(
                        "TaskScheduler of type {} does not support errorHandler configuration",
                         schedulerClass.getName());
                }
            }
        } catch (Throwable ex) {
            LOGGER.warn(
                "Bugsnag scheduled task exception handler could not be configured for TaskScheduler of type {}",
                taskScheduler.getClass().getName(), ex);
        }
    }

    private TaskScheduler unwrapRouter(TaskScheduler maybeRouter) {
        try {
            Class<?> taskSchedulerRouterClass = Class.forName(
                "org.springframework.scheduling.config.TaskSchedulerRouter");
            if (taskSchedulerRouterClass.isAssignableFrom(maybeRouter.getClass())) {
                Field defaultSchedulerField = taskSchedulerRouterClass.getDeclaredField("defaultScheduler");
                defaultSchedulerField.setAccessible(true);
                Supplier<TaskScheduler> defaultSchedulerSupplier =
                    (Supplier<TaskScheduler>) defaultSchedulerField.get(maybeRouter);
                return defaultSchedulerSupplier.get(); // Call get() on the Supplier
            }
        } catch (java.lang.Exception ex) {
            LOGGER.warn("Unable to unwrap TaskSchedulerRouter", ex);
        }
        return null;
    }

    private List<TaskScheduler> getDelegateSchedulers(TaskScheduler taskScheduler) {
        List<TaskScheduler> delegateSchedulers = new ArrayList<>();
        try {
            // Inspect the task scheduler for fields that might hold delegated schedulers
            Field[] fields = taskScheduler.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(taskScheduler);
                if (fieldValue instanceof List<?>) {
                    for (Object item : (List<?>) fieldValue) {
                        if (item instanceof TaskScheduler) {
                            delegateSchedulers.add((TaskScheduler) item);
                        }
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            LOGGER.warn(
                "Unable to retrieve delegate schedulers from TaskScheduler of type {}",
                    taskScheduler.getClass().getName(), ex);
        }
        return delegateSchedulers;
    }

    /**
     * Configure the error handler for concrete TaskScheduler implementations.
     */
    private void configureErrorHandlerOnConcreteScheduler(TaskScheduler scheduler,
                                                          BugsnagScheduledTaskExceptionHandler errorHandler)
            throws NoSuchFieldException, IllegalAccessException {
        Field errorHandlerField = scheduler.getClass().getDeclaredField("errorHandler");
        errorHandlerField.setAccessible(true);
        Object existingErrorHandler = errorHandlerField.get(scheduler);

        if (existingErrorHandler instanceof ErrorHandler) {
            errorHandler.setExistingErrorHandler((ErrorHandler) existingErrorHandler);
        }

        errorHandlerField.set(scheduler, errorHandler);
    }
}

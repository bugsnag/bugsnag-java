package com.bugsnag;

import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.util.concurrent.ScheduledExecutorService;

@Configuration
class ScheduledTaskBeanLocator implements ApplicationContextAware {

    private ApplicationContext beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext;
    }

    public TaskScheduler resolveTaskScheduler() {
        return resolveSchedulerBean(TaskScheduler.class);
    }

    public ScheduledExecutorService resolveScheduledExecutorService() {
        return resolveSchedulerBean(ScheduledExecutorService.class);
    }

    /**
     * Resolves a bean, first by searching for any bean in the
     * {@link org.springframework.beans.factory.BeanFactory}
     * which matches the type, and then by returning any bean which matches the
     * given bean name. If no bean can be found which satisifies either of these
     * conditions, null will be returned.
     * <p>
     * This broadly follows the approach used in {@link ScheduledAnnotationBeanPostProcessor}.
     *
     * @param schedulerType the scheduler clas
     * @param <T>           the bean type
     * @return the resolved bean, or null if it could not be found
     */
    private <T> T resolveSchedulerBean(Class<T> schedulerType) {
        try {
            return resolveSchedulerBean(schedulerType, false);
        } catch (NoUniqueBeanDefinitionException ex) { // multiple beans available, use name
            return resolveSchedulerBean(schedulerType, true);
        } catch (NoSuchBeanDefinitionException ex2) {
            return null;
        }
    }

    private <T> T resolveSchedulerBean(Class<T> schedulerType, boolean byName) {
        if (byName) {
            return this.beanFactory.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, schedulerType);
        } else if (this.beanFactory instanceof AutowireCapableBeanFactory) {
            AutowireCapableBeanFactory factory = (AutowireCapableBeanFactory) this.beanFactory;
            NamedBeanHolder<T> holder = factory.resolveNamedBean(schedulerType);
            return holder.getBeanInstance();
        } else {
            return this.beanFactory.getBean(schedulerType);
        }
    }
}

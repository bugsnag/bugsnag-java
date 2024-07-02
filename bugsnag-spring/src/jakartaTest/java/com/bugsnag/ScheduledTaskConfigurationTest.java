package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.bugsnag.testapp.springboot.TestSpringBootApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestSpringBootApplication.class)
public class ScheduledTaskConfigurationTest {

    @Autowired
    private ScheduledTaskConfiguration configuration;

    @Mock
    private ScheduledTaskRegistrar registrar;

    @Autowired
    private ScheduledTaskBeanLocator beanLocator;

    @MockBean
    private ApplicationContext context;

    @Before
    public void setUp() {
        registrar = new ScheduledTaskRegistrar();
        beanLocator.setApplicationContext(context);
    }

    @Test
    public void existingSchedulerUsed() {
        ThreadPoolTaskScheduler expected = new ThreadPoolTaskScheduler();
        registrar.setScheduler(expected);
        configuration.configureTasks(registrar);
        assertEquals(expected, registrar.getScheduler());
    }

    @Test
    public void noSchedulersAvailable() {
        configuration.configureTasks(registrar);
        assertTrue(registrar.getScheduler() instanceof ThreadPoolTaskScheduler);
    }

    @Test
    public void findSchedulerByType() throws NoSuchFieldException, IllegalAccessException {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        when(context.getBean(TaskScheduler.class)).thenReturn(scheduler);

        configuration.configureTasks(registrar);
        assertNull(registrar.getScheduler());
        Object errorHandler = accessField(scheduler, "errorHandler");
        assertTrue(errorHandler instanceof BugsnagScheduledTaskExceptionHandler);
    }

    @Test
    public void findSchedulerByName() throws NoSuchFieldException, IllegalAccessException {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        Throwable exc = new NoUniqueBeanDefinitionException(TaskScheduler.class);
        when(context.getBean(TaskScheduler.class)).thenThrow(exc);
        when(context.getBean("taskScheduler", TaskScheduler.class)).thenReturn(scheduler);

        configuration.configureTasks(registrar);
        assertNull(registrar.getScheduler());
        Object errorHandler = accessField(scheduler, "errorHandler");
        assertTrue(errorHandler instanceof BugsnagScheduledTaskExceptionHandler);
    }

    @Test
    public void findExecutorByType() throws NoSuchFieldException, IllegalAccessException {
        ScheduledExecutorService expected = Executors.newScheduledThreadPool(1);
        when(context.getBean(ScheduledExecutorService.class)).thenReturn(expected);

        configuration.configureTasks(registrar);
        TaskScheduler scheduler = registrar.getScheduler();
        assertTrue(scheduler instanceof ConcurrentTaskScheduler);
        assertEquals(expected, accessField(scheduler, "scheduledExecutor"));
    }

    @Test
    public void findExecutorByName() throws NoSuchFieldException, IllegalAccessException {
        ScheduledExecutorService expected = Executors.newScheduledThreadPool(4);
        Throwable exc = new NoUniqueBeanDefinitionException(ScheduledExecutorService.class);
        when(context.getBean(ScheduledExecutorService.class)).thenThrow(exc);
        when(context.getBean("taskScheduler", ScheduledExecutorService.class))
                .thenReturn(expected);

        configuration.configureTasks(registrar);
        TaskScheduler scheduler = registrar.getScheduler();
        assertTrue(scheduler instanceof ConcurrentTaskScheduler);
        assertEquals(expected, accessField(scheduler, "scheduledExecutor"));
    }

    @Test
    public void testSchedulerIsProxy(){

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(scheduler);
        proxyFactoryBean.setProxyTargetClass(true);
        TaskScheduler proxyScheduler = (TaskScheduler) proxyFactoryBean.getObject();

        when(context.getBean(TaskScheduler.class)).thenReturn(proxyScheduler);

        configuration.configureTasks(registrar);

        TaskScheduler resultScheduler = registrar.getScheduler();
        assertTrue("Expected scheduler to be a proxy", AopUtils.isAopProxy(resultScheduler));
    }

    @Test
    public void testSchedulerUnwrapped() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(scheduler);
        proxyFactoryBean.setProxyTargetClass(true);
        TaskScheduler proxyScheduler = (TaskScheduler) proxyFactoryBean.getObject();

        when(context.getBean(TaskScheduler.class)).thenReturn(proxyScheduler);

        configuration.configureTasks(registrar);

        TaskScheduler resultScheduler = registrar.getScheduler();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(resultScheduler);
        assertEquals("Expected scheduler to be unwrapped", scheduler.getClass(), targetClass);
    }



    private Object accessField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}

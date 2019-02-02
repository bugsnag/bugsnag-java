package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.bugsnag.testapp.springboot.TestSpringBootApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestSpringBootApplication.class)
public class ScheduledTaskBeanLocatorTest {

    @Autowired
    private ScheduledTaskBeanLocator beanLocator;

    @MockBean
    private ApplicationContext context;

    @Before
    public void setUp() {
        beanLocator.setApplicationContext(context);
    }

    @Test
    public void findSchedulerByType() {
        ThreadPoolTaskScheduler expected = new ThreadPoolTaskScheduler();
        when(context.getBean(TaskScheduler.class)).thenReturn(expected);
        assertEquals(expected, beanLocator.resolveTaskScheduler());
    }

    @Test
    public void findSchedulerByName() {
        ThreadPoolTaskScheduler expected = new ThreadPoolTaskScheduler();
        Throwable exc = new NoUniqueBeanDefinitionException(TaskScheduler.class);
        when(context.getBean(TaskScheduler.class)).thenThrow(exc);
        when(context.getBean("taskScheduler", TaskScheduler.class)).thenReturn(expected);
        assertEquals(expected, beanLocator.resolveTaskScheduler());
    }

    @Test
    public void noTaskSchedulerAvailable() {
        assertNull(beanLocator.resolveTaskScheduler());
    }

    @Test
    public void findExecutorByType() {
        ScheduledExecutorService expected = Executors.newScheduledThreadPool(1);
        when(context.getBean(ScheduledExecutorService.class)).thenReturn(expected);
        assertEquals(expected, beanLocator.resolveScheduledExecutorService());
    }

    @Test
    public void findExecutorByName() {
        ScheduledExecutorService expected = Executors.newScheduledThreadPool(4);
        Throwable exc = new NoUniqueBeanDefinitionException(ScheduledExecutorService.class);
        when(context.getBean(ScheduledExecutorService.class)).thenThrow(exc);
        when(context.getBean("taskScheduler", ScheduledExecutorService.class))
                .thenReturn(expected);
        assertEquals(expected, beanLocator.resolveScheduledExecutorService());
    }

    @Test
    public void noScheduledExecutorAvailable() {
        assertNull(beanLocator.resolveScheduledExecutorService());
    }
}

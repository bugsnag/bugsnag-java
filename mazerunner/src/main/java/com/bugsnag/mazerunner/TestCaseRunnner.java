package com.bugsnag.mazerunner;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Component
public class TestCaseRunnner implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(TestCaseRunnner.class);


    @Override
    public void run(String... args) throws Exception {

        LOGGER.info("args were:" + String.join(",", args));

    }

    public Scenario testCaseForName(String eventType)
            throws ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        Class clz = Class.forName("com.bugsnag.mazerunner.scenarios.$eventType");
        Constructor constructor = clz.getConstructors()[0];
        return (Scenario) constructor.newInstance();
    }
}

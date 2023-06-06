package com.bugsnag.servlet.jakarta;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import java.util.Set;

public class BugsnagServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> cls, ServletContext context) throws ServletException {
        context.addListener(BugsnagServletRequestListener.class);
    }
}

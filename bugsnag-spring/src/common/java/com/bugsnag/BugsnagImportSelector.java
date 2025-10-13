package com.bugsnag;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class BugsnagImportSelector implements ImportSelector {

    private static final String[] SPRING_JAKARTA_CLASSES = {
        "com.bugsnag.SpringBootJakartaConfiguration",
        "com.bugsnag.JakartaMvcConfiguration",
        "com.bugsnag.ScheduledTaskConfiguration"
    };

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return SPRING_JAKARTA_CLASSES;
    }
}

package com.bugsnag;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.SpringVersion;
import org.springframework.core.type.AnnotationMetadata;

public class BugsnagImportSelector implements ImportSelector {

    private static final String[] SPRING_JAKARTA_CLASSES = {
        "com.bugsnag.SpringBootJakartaConfiguration",
        "com.bugsnag.JakartaMvcConfiguration",
        "com.bugsnag.ScheduledTaskConfiguration"
    };

    private static final String[] SPRING_JAVAX_CLASSES = {
        "com.bugsnag.SpringBootJavaxConfiguration",
        "com.bugsnag.JavaxMvcConfiguration",
        "com.bugsnag.ScheduledTaskConfiguration"
    };

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        if (isSpringJakartaCompatible() && isJava17Compatible()) {
            return SPRING_JAKARTA_CLASSES;
        }

        return SPRING_JAVAX_CLASSES;
    }

    private static boolean isSpringJakartaCompatible() {
        return getMajorVersion(SpringVersion.getVersion()) >= 6;
    }

    private static boolean isJava17Compatible() {
        return getMajorVersion(System.getProperty("java.version")) >= 17;
    }

    private static int getMajorVersion(String version) {
        if (version == null) {
            return 0;
        }
        int firstDot = version.indexOf(".");
        String majorVersion;

        if (firstDot == -1) {
            majorVersion = version;
        }
        else{
            majorVersion = version.substring(0, firstDot);
        }

        try {
            return Integer.parseInt(majorVersion);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}

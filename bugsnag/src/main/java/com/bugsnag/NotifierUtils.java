package com.bugsnag;

class NotifierUtils {

    private static final boolean IS_SPRING_NOTIFIER = hasBugsnagSpringClz();
    private static final String BUGSNAG_SPRING_CLZ = "com.bugsnag.BugsnagSpringConfiguration";

    private static boolean hasBugsnagSpringClz() {
        try {
            Class.forName(BUGSNAG_SPRING_CLZ, false, NotifierUtils.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    static Notifier getNotifier() {
        Notifier notifier = new Notifier();

        if (IS_SPRING_NOTIFIER) {
            notifier.setNotifierName("Bugsnag Spring");
        }
        return notifier;
    }

}

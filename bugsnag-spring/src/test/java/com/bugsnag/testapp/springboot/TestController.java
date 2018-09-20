package com.bugsnag.testapp.springboot;

import com.bugsnag.Bugsnag;

import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Throw a runtime exception
     */
    @RequestMapping("/throw-runtime-exception")
    public void throwRuntimeException() {
        throw new RuntimeException("Test");
    }

    /**
     * Throw an exception where the severity reason is exceptionClass
     */
    @RequestMapping("/throw-type-mismatch-exception")
    public void throwTypeMismatchException() {
        throw new TypeMismatchException("Test", String.class);
    }

    /**
     * Report a handled exception where the severity reason is exceptionClass
     */
    @RequestMapping("/handled-type-mismatch-exception")
    public void handledTypeMismatchException() {
        try {
            throw new TypeMismatchException("Test", String.class);
        } catch (TypeMismatchException ex) {
            bugsnag.notify(ex);
        }
    }

    /**
     * Report a handled exception where the severity is set in the notify call
     */
    @RequestMapping("/handled-type-mismatch-exception-user-severity")
    public void handledTypeMismatchExceptionUserSeverity() {
        try {
            throw new TypeMismatchException("Test", String.class);
        } catch (TypeMismatchException ex) {
            bugsnag.notify(ex, Severity.WARNING);
        }
    }

    /**
     * Report a handled exception where the severity reason is set in a callback
     */
    @RequestMapping("/handled-type-mismatch-exception-callback-severity")
    public void handledTypeMismatchExceptionCallbackSeverity() {
        try {
            throw new TypeMismatchException("Test", String.class);
        } catch (TypeMismatchException ex) {
            bugsnag.notify(ex, new Callback() {
                @Override
                public void beforeNotify(Report report) {
                    report.setSeverity(Severity.WARNING);
                }
            });
        }
    }
}

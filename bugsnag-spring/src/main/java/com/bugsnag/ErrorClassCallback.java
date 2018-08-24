package com.bugsnag;

import com.bugsnag.HandledState.SeverityReasonType;
import com.bugsnag.callbacks.Callback;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A callback to assign a specified severity in a report for particular Spring exceptions.
 * This severity can be overridden if the severity is set manually in an application.
 */
class ErrorClassCallback implements Callback {

    private static final Map<Class<? extends java.lang.Exception>, Severity> exceptionToSeverity;

    static {
        exceptionToSeverity = new HashMap<Class<? extends java.lang.Exception>, Severity>();
        exceptionToSeverity.put(HttpRequestMethodNotSupportedException.class, Severity.INFO);
        exceptionToSeverity.put(HttpMediaTypeNotSupportedException.class, Severity.INFO);
        exceptionToSeverity.put(HttpMediaTypeNotAcceptableException.class, Severity.WARNING);
        exceptionToSeverity.put(MissingPathVariableException.class, Severity.ERROR);
        exceptionToSeverity.put(MissingServletRequestParameterException.class, Severity.ERROR);
        exceptionToSeverity.put(ServletRequestBindingException.class, Severity.INFO);
        exceptionToSeverity.put(ConversionNotSupportedException.class, Severity.ERROR);
        exceptionToSeverity.put(TypeMismatchException.class, Severity.INFO);
        exceptionToSeverity.put(HttpMessageNotReadableException.class, Severity.WARNING);
        exceptionToSeverity.put(HttpMessageNotWritableException.class, Severity.ERROR);
        exceptionToSeverity.put(MethodArgumentNotValidException.class, Severity.INFO);
        exceptionToSeverity.put(MissingServletRequestPartException.class, Severity.INFO);
        exceptionToSeverity.put(BindException.class, Severity.INFO);
        exceptionToSeverity.put(NoHandlerFoundException.class, Severity.INFO);
    }

    @Override
    public void beforeNotify(Report report) {

        HandledState handledState = report.getHandledState();

        // A manually-set severity takes precedence
        SeverityReasonType severityReasonType = handledState.calculateSeverityReasonType();
        if (severityReasonType == SeverityReasonType.REASON_USER_SPECIFIED
                || severityReasonType == SeverityReasonType.REASON_CALLBACK_SPECIFIED) {
            return;
        }

        Class errorClass = report.getException().getClass();

        if (exceptionToSeverity.containsKey(errorClass)) {
            Severity severity = exceptionToSeverity.get(errorClass);
            report.setSeverity(severity);
            report.setHandledState(HandledState.newInstance(
                    SeverityReasonType.REASON_ERROR_CLASS,
                    Collections.singletonMap("errorClass", errorClass.getSimpleName()),
                    severity,
                    handledState.isUnhandled()));
        }
    }
}
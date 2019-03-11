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
 *
 * These exceptions are automatically resolved to HTTP responses by the built-in Spring
 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}.
 *
 * Exceptions that are automatically resolved to 4XX (client error) responses map to
 * severity INFO because the client is at fault, not the server.
 *
 * Exceptions that automatically resolve to 500 (internal server error) responses
 * map to severity to ERROR.
 */
class ExceptionClassCallback implements Callback {

    private static final Map<Class<? extends java.lang.Exception>, Severity> EXCEPTION_TO_SEVERITY;

    static {
        EXCEPTION_TO_SEVERITY = new HashMap<Class<? extends java.lang.Exception>, Severity>();

        // Thrown when a required header, required cookie, etc is missing from the request.
        // Resolves to HTTP 400, bad request.
        EXCEPTION_TO_SEVERITY.put(ServletRequestBindingException.class, Severity.INFO);

        // Thrown if an HTTP method is not supported, e.g. a POST request was sent instead
        // of a GET.
        // Resolves to HTTP 405, method not allowed.
        EXCEPTION_TO_SEVERITY.put(HttpRequestMethodNotSupportedException.class, Severity.INFO);

        // Thrown when a client POSTs, PUTs, or PATCHes content of a type
        // not supported by request handler.
        // Resolves to HTTP 415, unsupported media type.
        EXCEPTION_TO_SEVERITY.put(HttpMediaTypeNotSupportedException.class, Severity.INFO);

        // Thrown when the request handler cannot generate a response that is acceptable
        // by the client, expressed by the Accept header.
        // Resolves to HTTP 406, not acceptable.
        EXCEPTION_TO_SEVERITY.put(HttpMediaTypeNotAcceptableException.class, Severity.INFO);

        // Thrown when a method argument annotated with @Valid fails validation.
        // Resolves to HTTP 400, bad request.
        EXCEPTION_TO_SEVERITY.put(MethodArgumentNotValidException.class, Severity.INFO);

        // Thrown when part of a multipart request is required but is missing.
        // Resolves to HTTP 400, bad request.
        EXCEPTION_TO_SEVERITY.put(MissingServletRequestPartException.class, Severity.INFO);

        // Thrown when a @ModelAttribute method argument has binding or validation errors
        // and an error response for the client is not defined with a BindingResult.
        // Resolves to HTTP 400, bad request.
        EXCEPTION_TO_SEVERITY.put(BindException.class, Severity.INFO);

        // Thrown if a handler cannot be found for the request (404).
        // Note: By default, the DispatcherServlet does not send exceptions for 404s.  However
        // if its property "throwExceptionIfNoHandlerFound" is set to true this exception is raised.
        // Resolves to HTTP 404, not found.
        EXCEPTION_TO_SEVERITY.put(NoHandlerFoundException.class, Severity.INFO);

        // Thrown when an org.springframework.web.bind.WebDataBinder conversion cannot occur because
        // a suitable converter could not be found.
        // Resolves to HTTP 500, internal server error.
        EXCEPTION_TO_SEVERITY.put(ConversionNotSupportedException.class, Severity.ERROR);

        // Thrown when an org.springframework.web.bind.WebDataBinder conversion causes an error.
        // Resolves to HTTP 400, bad request.
        EXCEPTION_TO_SEVERITY.put(TypeMismatchException.class, Severity.INFO);

        // Thrown when an HTTP message cannot be read.
        // Resolves to HTTP 400, bad request.
        EXCEPTION_TO_SEVERITY.put(HttpMessageNotReadableException.class, Severity.INFO);

        // Thrown when an HTTP message cannot be written.
        // Resolves to HTTP 500, internal server error.
        EXCEPTION_TO_SEVERITY.put(HttpMessageNotWritableException.class, Severity.ERROR);

        // Thrown if a required method parameter is missing from the request.
        // Resolves to HTTP 400, bad request.
        EXCEPTION_TO_SEVERITY.put(MissingServletRequestParameterException.class, Severity.INFO);

        // Thrown if a controller method's path variable does not match the URI template, e.g.
        //
        // @RequestMapping("/example/{variable}")
        // public String doAThing(@PathVariable String wrongVariable)
        //
        // Resolves to HTTP 500, internal server error.
        EXCEPTION_TO_SEVERITY.put(MissingPathVariableException.class, Severity.ERROR);
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

        Class exceptionClass = report.getException().getClass();

        if (EXCEPTION_TO_SEVERITY.containsKey(exceptionClass)) {
            Severity severity = EXCEPTION_TO_SEVERITY.get(exceptionClass);
            report.setSeverity(severity);
            report.setHandledState(HandledState.newInstance(
                    SeverityReasonType.REASON_EXCEPTION_CLASS,
                    Collections.singletonMap("exceptionClass", exceptionClass.getSimpleName()),
                    severity,
                    handledState.isUnhandled()));
        }
    }
}

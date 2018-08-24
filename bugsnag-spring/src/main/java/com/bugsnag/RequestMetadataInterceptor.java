package com.bugsnag;

        import com.bugsnag.callbacks.Callback;

        import com.bugsnag.servlet.BugsnagServletRequestListener;
        import com.bugsnag.util.RequestUtils;

        import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

        import java.util.Map;

        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;

/**
 * Automatically adds metadata for HTTP requests which will be included in handled
 * and unhandled reports generated during the processing of the request.
 */
class RequestMetadataInterceptor
        extends HandlerInterceptorAdapter implements Callback {

    private static final ThreadLocal<Map<String, Object>> REQUEST_METADATA =
            new ThreadLocal<Map<String, Object>>();

    private static final ThreadLocal<String> CONTEXT =
            new ThreadLocal<String>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // If the request is available through the listener then the request data has
        // already been added by the ServletCallback. This is only the case for plain
        // Spring apps, ServletRequestListeners do not work for Spring Boot apps.
        if (BugsnagServletRequestListener.getServletRequest() == null) {
            REQUEST_METADATA.set(RequestUtils.getRequestMetadata(request));
            CONTEXT.set(RequestUtils.generateContext(request));
        }
        return true;
    }

    @Override
    public void beforeNotify(Report report) {
        Map<String, Object> map = REQUEST_METADATA.get();
        if (map != null) {
            report.addToTab("request", map);

            // Set default context
            if (report.getContext() == null) {
                report.setContext(CONTEXT.get());
            }
        }
    }
}

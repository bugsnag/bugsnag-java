package com.bugsnag.example.servlet;

import com.bugsnag.Bugsnag;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandler extends HttpServlet {

    private static final long serialVersionUID = 4926619146717832212L;

    private Bugsnag bugsnag;

    /**
     * Error handler to report the error to Bugsnag
     */
    public ErrorHandler() {
        bugsnag = new Bugsnag("YOUR-API-KEY");
        bugsnag.setProjectPackages("com.bugsnag.example");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Notify Bugsnag of the exception
        Throwable throwable = (Throwable) req.getAttribute("jakarta.servlet.error.exception");
        bugsnag.notify(throwable);
    }
}

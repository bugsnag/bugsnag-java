package com.bugsnag.example.servlet;

import com.bugsnag.Bugsnag;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;

import java.io.IOException;

@WebFilter(urlPatterns = {"/*"}, asyncSupported=true)
public class ErrorFilter extends HttpFilter {

    private Bugsnag bugsnag;

    public ErrorFilter() {
        bugsnag = new Bugsnag("YOUR-API-KEY");
        bugsnag.setProjectPackages("com.bugsnag.example");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(req, res, chain);
        } catch (ServletException servletException) {
            bugsnag.notify(servletException);
            throw servletException;
        }
    }
}
package com.bugsnag.testapp.springboot;

import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(I_AM_A_TEAPOT)
public class TestResponseStatusException extends RuntimeException {
}

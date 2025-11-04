package com.bugsnag.serialization;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for JSON serialization.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@JacksonAnnotationsInside
@JsonProperty
public @interface Expose {
    /**
     * The name to use for the JSON property. If not specified, the property name
     * will be derived from the method name.
     *
     * @return the JSON property name
     */
    String value() default "";
}


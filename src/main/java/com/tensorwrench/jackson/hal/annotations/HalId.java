package com.tensorwrench.jackson.hal.annotations;

import java.lang.annotation.*;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
@JacksonAnnotation 
public @interface HalId {
}

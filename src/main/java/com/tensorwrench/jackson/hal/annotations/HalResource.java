package com.tensorwrench.jackson.hal.annotations;

import java.lang.annotation.*;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@JacksonAnnotationsInside
public @interface HalResource {
	String format() default "{classname.lcase}/{id}";
	String idParser() default ".*/(.*)\\??"; // after the last slash, but before any query params
}

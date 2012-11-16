package com.tensorwrench.jackson.hal.annotations;

import java.lang.annotation.*;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@JacksonAnnotationsInside
public @interface HalResource {
	public static final String DEFAULT_URL_FORMAT="{classname.lcase}/{id}";
	public static final String DEFAULT_ID_REGEX=".*/([^?#]+).*";
	
	String urlFormat() default DEFAULT_URL_FORMAT;
	String idRegex() default DEFAULT_ID_REGEX; // after the last slash, but before any query params
}

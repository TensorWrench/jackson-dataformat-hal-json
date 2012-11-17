package com.tensorwrench.jackson.hal;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface HalLinkResolver {
	Object makeObject(String href, DeserializationContext ctxt, JavaType javaType) throws JsonMappingException;
	String makeIdFromHref(Class<?> javaType, DeserializationContext ctxt, String href) throws JsonMappingException;
}

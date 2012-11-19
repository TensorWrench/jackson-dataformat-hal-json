package com.tensorwrench.jackson.hal;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface HalLinkResolver {
	public Object makeObject(String href, JavaType javaType,DeserializationContext ctxt) throws JsonMappingException;
	public String extractIdFromHref(String href, Class<?> resultClass,DeserializationContext ctxt) throws JsonMappingException;
}

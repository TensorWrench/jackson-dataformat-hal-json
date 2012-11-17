package com.tensorwrench.jackson.hal.resolvers;

import java.net.URI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class UriInfoFilter implements BeanPropertyFilter {
	public static final Object KEY = UriInfoFilter.class; // prevent collisions
	URI uri;
	
	public URI getUri() {
		return uri;
	}

	public UriInfoFilter(URI uri) {
		super();
		this.uri = uri;
	}

	@Override
	public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov, BeanPropertyWriter writer) throws Exception {
		throw new UnsupportedOperationException("UriInfoFilter is never supposed to be actually triggered, it's just a data holder");
	}
	
}

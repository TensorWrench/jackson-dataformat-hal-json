package com.tensorwrench.jackson.hal;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;

public interface HalLinkCreator {

	String makeLink(Object val, SerializerProvider prov) throws JsonMappingException;
	
}

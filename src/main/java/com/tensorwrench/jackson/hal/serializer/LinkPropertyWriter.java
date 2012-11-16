package com.tensorwrench.jackson.hal.serializer;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.tensorwrench.jackson.hal.Hal;
import com.tensorwrench.jackson.hal.HalLinkCreator;

public class LinkPropertyWriter extends BaseHalPropertyWriter {
	HalLinkCreator linkCreator;
	public LinkPropertyWriter(HalLinkCreator linkCreator) {
		super(Hal.LINKS);
		this.linkCreator=linkCreator;
	}

	@Override
	public void serializeAsField(Object obj, JsonGenerator jgen, SerializerProvider prov) throws Exception {
		jgen.writeObjectFieldStart(getName());{  // "_links": {
			jgen.writeFieldName("self");
			writeOne(obj,jgen,prov);
			
			for(BeanPropertyWriter p: properties) {
				jgen.writeFieldName(p.getName());
				Object val=p.get(obj);
				if(val instanceof Collection) {
					jgen.writeStartArray();
					for(Object o: (Collection<?>) val) {
						writeOne(o,jgen,prov);
					}
					jgen.writeEndArray();
				} else {
					writeOne(val, jgen,prov);
				}
			}		
			}; jgen.writeEndObject();
	}
	

	protected void writeOne(Object val, JsonGenerator jgen, SerializerProvider prov) throws IOException, JsonGenerationException {
		jgen.writeStartObject();
		if(val ==null) {
			jgen.writeNullField("href");
		} else {
			jgen.writeStringField("href",linkCreator.makeLink(val,prov));
		}
		jgen.writeEndObject();
	}

	
}

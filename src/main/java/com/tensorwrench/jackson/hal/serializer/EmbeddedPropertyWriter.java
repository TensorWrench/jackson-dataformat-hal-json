package com.tensorwrench.jackson.hal.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class EmbeddedPropertyWriter extends BaseHalPropertyWriter {
	public EmbeddedPropertyWriter() {
		super("_embedded");
	}
	@Override
	public void serializeAsField(Object obj, JsonGenerator jgen, SerializerProvider prov) throws Exception {
		jgen.writeObjectFieldStart(getName());{
			for(BeanPropertyWriter p: properties) {
				jgen.writeFieldName(p.getName());
				Object propertyValue=p.get(obj);
				if(propertyValue==null) {
					jgen.writeNull();
				} else {
					JsonSerializer<Object> serializer=prov.findValueSerializer(p.getType(), p);
					if(serializer != null) {
					serializer.serialize(propertyValue, jgen, prov);
					} else {
						jgen.writeObject(p.get(obj));
					}
				}
			}		
			}; jgen.writeEndObject();
	}
}

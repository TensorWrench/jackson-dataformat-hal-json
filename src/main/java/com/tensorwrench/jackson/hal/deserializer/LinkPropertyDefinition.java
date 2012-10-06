package com.tensorwrench.jackson.hal.deserializer;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.tensorwrench.jackson.hal.Hal;

public class LinkPropertyDefinition extends BaseHalPropertyDefinition {
	protected LinkPropertyDefinition() {
		super(Hal.LINKS);
	}
	@Override
	protected BaseHalPropertyDefinition copy() {
		return new LinkPropertyDefinition();
	}
	
	@Override
	protected Object onCollectionProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p,Collection<Object> collection) throws JsonParseException, IOException {
		while(jp.nextToken() != JsonToken.END_ARRAY) {
			collection.add(onNormalProperty(jp, ctxt, p));
		}
		return collection;
	}

	protected Object onNormalProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p) throws JsonProcessingException, IOException {
		String href=findHref(jp);
		return ObjectMaker.makeObject(href,ctxt,p.getType());
	};

	

	protected String findHref(JsonParser jp) throws IOException, JsonParseException {
		String href=null;
		while(jp.nextToken() != JsonToken.END_OBJECT) {
			// jp points to field name
			jp.nextToken(); // points to value
			if("href".equalsIgnoreCase(jp.getCurrentName())) {
				href=jp.getText();
			} else  {
				skipValue(jp); 
			}
		}
		return href;
	}
	

}

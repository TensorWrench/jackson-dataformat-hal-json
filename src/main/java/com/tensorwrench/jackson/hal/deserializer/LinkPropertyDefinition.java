package com.tensorwrench.jackson.hal.deserializer;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.tensorwrench.jackson.hal.Hal;
import com.tensorwrench.jackson.hal.HalLinkResolver;
import com.tensorwrench.jackson.hal.util.HalUtils;

public class LinkPropertyDefinition extends BaseHalPropertyDefinition {
	protected HalLinkResolver linkResolver;
	
	protected LinkPropertyDefinition(HalLinkResolver linkResolver) {
		super(Hal.LINKS);
		this.linkResolver=linkResolver;
	}
	@Override
	protected BaseHalPropertyDefinition copy() {
		return new LinkPropertyDefinition(linkResolver);
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
		return linkResolver.makeObject(href,p.getType(),ctxt);
	};

	@Override
	protected void onSelfLink(JsonParser jp, DeserializationContext ctxt, Object instance) throws JsonParseException, IOException
	{
		if(selfProperty == null) {
			throw new JsonMappingException("Class " + instance.getClass().getCanonicalName() + " does not contain a @HalId");
		}
		String idString=linkResolver.extractIdFromHref(findHref(jp),instance.getClass(),ctxt);
		selfProperty.set(instance, HalUtils.valueFromString(selfProperty, idString));
	}

	protected String findHref(JsonParser jp) throws IOException, JsonParseException {
		String href=null;
		expect(jp, JsonToken.START_OBJECT);
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

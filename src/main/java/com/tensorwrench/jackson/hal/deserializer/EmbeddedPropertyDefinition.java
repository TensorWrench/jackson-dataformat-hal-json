package com.tensorwrench.jackson.hal.deserializer;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.tensorwrench.jackson.hal.util.HalUtils;

public class EmbeddedPropertyDefinition extends BaseHalPropertyDefinition {

	protected EmbeddedPropertyDefinition() {
		super("_embedded");
	}

	@Override
	protected BaseHalPropertyDefinition copy() {
		return new EmbeddedPropertyDefinition();
	}
	
	@Override
	protected Object onCollectionProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p, Collection<Object> collection) throws JsonProcessingException, IOException {
		JsonDeserializer<Collection<Object>> deser =
				new CollectionDeserializer(
						p.getType().getContentType(), 
						ctxt.findContextualValueDeserializer(p.getType().getContentType(), p), 
						null, 
						new StdValueInstantiator(ctxt.getConfig(), p.getType().getContentType())
				);
		return deser.deserialize(jp, ctxt, collection);
	}

	@Override
	protected Object onNormalProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p) throws JsonProcessingException, IOException {
		JsonDeserializer<Object> deser=ctxt.findContextualValueDeserializer(p.getType(), p);
		Object object = HalUtils.defaultConstruct(p.getType().getRawClass());
		return deser.deserialize(jp, ctxt, object);
	}
	
}

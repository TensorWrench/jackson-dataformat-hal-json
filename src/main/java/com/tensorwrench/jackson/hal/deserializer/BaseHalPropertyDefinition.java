package com.tensorwrench.jackson.hal.deserializer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.tensorwrench.jackson.hal.util.HalUtils;

public abstract class BaseHalPropertyDefinition extends SettableBeanProperty {
	private final Map<String,SettableBeanProperty> properties=new HashMap<>();

	protected BaseHalPropertyDefinition(String propName) {
		super(propName, TypeFactory.unknownType(), null, new AnnotationMap());
	}

	public void addProp(SettableBeanProperty p) {
		properties.put(p.getName(),p);
	}


	//	@SuppressWarnings("unchecked")
	@Override
	public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
		//		BaseHalPropertyDefinition p=this.copy();
		//		p._valueDeserializer=(JsonDeserializer<Object>) deser;
		return this; // does nothing
	}

	@Override
	public SettableBeanProperty withName(String newName) {
		return this; // can't set the name
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> acls) {
		return null;
	}

	@Override
	public AnnotatedMember getMember() {
		return null;
	}

	@Override
	public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object instance) throws IOException, JsonProcessingException {
		deserializeSetAndReturn(jp, ctxt, instance);
	}

	@Override
	public void set(Object instance, Object value) throws IOException {
		throw new UnsupportedOperationException("Cannot set the reserved HAL properties directly on " + instance + " to " + value); 
	}

	@Override
	public Object setAndReturn(Object instance, Object value) throws IOException {
		throw new UnsupportedOperationException("Cannot set the reserved HAL properties directly on " + instance + " to " + value); 
	}


	@Override
	public Object deserializeSetAndReturn(JsonParser jp, DeserializationContext ctxt, Object instance) throws IOException, JsonProcessingException {
		expect(jp,JsonToken.START_OBJECT);
		while(jp.nextToken() == JsonToken.FIELD_NAME) {
			// while loop moves to the FieldName
			jp.nextToken(); // this points to the value
			SettableBeanProperty p=properties.get(jp.getCurrentName());
			if(p !=null) {
				Object value=deserializeProperty(jp, ctxt, p);
				p.set(instance, value);
			} else {
				skipValue(jp);
			}
		}
		return null;
	}

	protected void expect(JsonParser jp,JsonToken t) throws IOException {
		if(jp.getCurrentToken() != t) {
			throw new IOException("Expected an "+t +", but got " + jp);
		}
	}
	protected void skipValue(JsonParser jp) throws IOException, JsonParseException {
		// skip the value
		if(jp.getCurrentToken() == JsonToken.START_ARRAY || jp.getCurrentToken() == JsonToken.START_OBJECT)
			jp.skipChildren();
		else
			jp.nextToken();
	}


	protected Object deserializeProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p) throws JsonProcessingException, IOException {
		if(jp.getCurrentToken() == JsonToken.VALUE_NULL) {
			return null;
		}
		if(p.getType().isCollectionLikeType()) {
			expect(jp,JsonToken.START_ARRAY);
			Collection<Object> collection=HalUtils.concreteCollection(p.getType());
			return onCollectionProperty(jp, ctxt, p,collection);
		} 
		else if(p.getType().isMapLikeType()) {
			expect(jp,JsonToken.START_OBJECT);
			return onMapProperty(jp, ctxt, p);
		} 
		else if(p.getType().isArrayType()) {
			expect(jp,JsonToken.START_ARRAY);
			return onArrayProperty(jp, ctxt, p);
		} 
		else if(p.getType().isEnumType()) {
			return onEnumProperty(jp,ctxt,p);
		} 
		else { // "normal" types
			return onNormalProperty(jp,ctxt,p);
		}


	}

	protected abstract BaseHalPropertyDefinition copy();

	protected Object onNormalProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p) throws JsonProcessingException, IOException {
		throw new UnsupportedOperationException("Non-collection containers not supported: " + p);
	}

	protected Object onEnumProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p) throws JsonProcessingException, IOException {
		throw new UnsupportedOperationException("Non-collection containers not supported: " + p);
	}

	protected Object onArrayProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p) throws JsonProcessingException, IOException {
		throw new UnsupportedOperationException("Non-collection containers not supported: " + p);
	}

	protected Object onMapProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p) throws JsonProcessingException, IOException {
		throw new UnsupportedOperationException("Non-collection containers not supported: " + p);
	}

	protected Object onCollectionProperty(JsonParser jp, DeserializationContext ctxt, SettableBeanProperty p,Collection<Object> collection) throws JsonProcessingException,IOException {
		throw new UnsupportedOperationException("Non-collection containers not supported: " + p);
	}

}

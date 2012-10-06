package com.tensorwrench.jackson.hal.serializer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;

class EmptyClass {
	
}


public abstract class BaseHalPropertyWriter extends BeanPropertyWriter{
	protected final List<BeanPropertyWriter> properties=new ArrayList<>();
	
	public BaseHalPropertyWriter(String name) {
		super(
				new SimpleBeanPropertyDefinition(createAnnotatedMethod(),name),
				createAnnotatedMethod(),
				new AnnotationMap(), 
				TypeFactory.defaultInstance().constructType(EmptyClass.class), 
				null, 
				null, 
				TypeFactory.defaultInstance().constructType(EmptyClass.class), 
				false, 
				null);
	}
	
	private static AnnotatedMember createAnnotatedMethod() {
		try {
			return new AnnotatedMethod(BaseHalPropertyWriter.class.getMethod("dummyMethod"),	new AnnotationMap(), new AnnotationMap[0]);
		} catch (NoSuchMethodException | SecurityException e) {
			// shouldn't happen... so don't worry about it too much
			return null;
		}
	}

	public void addProp(BeanPropertyWriter p) {
		properties.add(p);
		
	}
	public Object dummyMethod() {
		return null;
	}
	
}

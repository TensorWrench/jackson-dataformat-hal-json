package com.tensorwrench.jackson.hal.deserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.tensorwrench.jackson.hal.util.HalUtils;

public class ObjectMaker {
	private final BeanDescription beanDescription;
	private final BeanPropertyDefinition idProp;
	private final Pattern format;
	
	public ObjectMaker(BeanDescription bean) {
		beanDescription=bean;
		format=Pattern.compile(HalUtils.findIdParser(bean));
		idProp=HalUtils.findIdProperty(bean);
	}
	
	public Object make(String href) {
		Object b=beanDescription.instantiateBean(false);
		Matcher m=format.matcher(href);
		if(m.groupCount() > 1) {
			idProp.getMutator().setValue(b, Long.parseLong(m.group(1)));
		}
		return b;		
	}

	static Map<Class<?>, ObjectMaker> cache=new HashMap<Class<?>, ObjectMaker>();
	
	public static Object makeObject(String href, DeserializationContext ctxt, JavaType javaType) {
		synchronized(cache) {
			ObjectMaker om=cache.get(javaType.getRawClass());
			if(om == null) {			
				BeanDescription bean=ctxt.getConfig().getClassIntrospector().forDeserialization(ctxt.getConfig(), javaType, null);
				cache.put(javaType.getRawClass(), om=new ObjectMaker(bean));
			}
			return om.make(href);
		}
	}
	
}

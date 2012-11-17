package com.tensorwrench.jackson.hal.resolvers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.tensorwrench.jackson.hal.HalLinkResolver;
import com.tensorwrench.jackson.hal.annotations.HalResource;
import com.tensorwrench.jackson.hal.util.HalUtils;

public class NewObjectLinkResolver implements HalLinkResolver{
	Map<Class<?>, ObjectMakerImpl> cache=new HashMap<>();
	
	static class ObjectMakerImpl {
		private final BeanDescription beanDescription;
		private final BeanPropertyDefinition idProp;
		private final Pattern format;

		public ObjectMakerImpl(BeanDescription bean) throws JsonMappingException {
			beanDescription=bean;
			format=Pattern.compile(HalUtils.findIdParser(bean));
			idProp=HalUtils.findIdProperty(bean);
			if(idProp==null)
				throw new JsonMappingException("Class " + bean.getBeanClass().getCanonicalName() + " does not contain a @HalId");
		}
		
		public Object make(String href) {
			Object b=beanDescription.instantiateBean(false);
			Matcher m=format.matcher(href);
			if(m.groupCount() > 1) {
				idProp.getMutator().setValue(b, Long.parseLong(m.group(1)));
			}
			return b;
		}

	}
	
	protected ObjectMakerImpl findImpl(JavaType javaType,DeserializationContext ctxt) throws JsonMappingException{
		synchronized(cache) {
			ObjectMakerImpl om=cache.get(javaType.getRawClass());
			if(om == null) {
				BeanDescription bean=ctxt.getConfig().getClassIntrospector().forDeserialization(ctxt.getConfig(), javaType, null);
				cache.put(javaType.getRawClass(), om=new ObjectMakerImpl(bean));
			}
			return om;
		}
	}
	
	@Override
	public Object makeObject(String href, DeserializationContext ctxt, JavaType javaType) throws JsonMappingException {
		return findImpl(javaType,ctxt).make(href);
	}
	
	@Override
	public String makeIdFromHref(Class<?> javaType,DeserializationContext ctxt,String href) throws JsonMappingException
	{
		HalResource resource=HalUtils.findAnnotation(javaType, HalResource.class);
		Matcher m;
		if(resource!=null) {
			m=Pattern.compile(resource.idRegex()).matcher(href);
		} else {
			m=Pattern.compile(HalResource.DEFAULT_ID_REGEX).matcher(href);
		}
		
		if(m.matches()) {
			return m.group(1);
		}
		
		throw new JsonMappingException("Could not turn " + href + " into an ID using pattern "+ m.pattern().toString());
	}
}

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
	
	/** Given a url and the expected type, return the actual object for that type.
	 * 
	 */
	@Override
	public Object makeObject(String href, JavaType javaType,DeserializationContext ctxt) throws JsonMappingException {
		return findImpl(javaType,ctxt).make(href);
	}
	
	
	/** Accepts an href and pulls the object's ID out of it.  For example, if the ids are numbers, it should return "1234" with no params or other url bits.
	 *   @param resultClass The class of the object that we're getting the ID for
	 *   @param ctxt The deserialization context, in case it's needed
	 *   @param href The href to pull the class from
	 */
	@Override
	public String extractIdFromHref(String href, Class<?> resultClass,DeserializationContext ctxt) throws JsonMappingException
	{
		return HalUtils.extractId(resultClass, href);
	}
}

package com.tensorwrench.jackson.hal.serializer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.tensorwrench.jackson.hal.util.HalUtils;
import com.tensorwrench.jackson.hal.util.UriInfoFilter;

public class LinkBuilder{
	private final BeanDescription beanDescription;
	private final BeanPropertyDefinition idProp;
	private final String format;
	
	public LinkBuilder(BeanDescription bean) {
		beanDescription=bean;
		format=HalUtils.findFormat(bean);
		idProp=HalUtils.findIdProperty(bean);
		idProp.getAccessor().fixAccess();
	}

	public String make(Object object) {
		Object val=idProp.getAccessor().getValue(object);
		
		return format.replace("{id}", val.toString())
							   .replace("{classname}",beanDescription.getBeanClass().getSimpleName())
							   .replace("{classname.lcase}",beanDescription.getBeanClass().getSimpleName().toLowerCase())
							   ;
	}

	private String make(Object val, URI uri) {
		String s=make(val);
		if(uri!=null) {
			if(s.startsWith("/") || uri.getPath().endsWith("/")) {
				return uri.getPath()+s;
			} else {
				return uri.getPath()+"/"+s;
			}
		}
		return s;
	}
	static Map<Class<?>,LinkBuilder> cache=new HashMap<Class<?>, LinkBuilder>();

	public static String makeLink(Object val, SerializerProvider prov) {
		synchronized(cache) {
			LinkBuilder linkBuilder=cache.get(val.getClass());
			if(linkBuilder == null) {
				JavaType javaType=prov.getConfig().getTypeFactory().constructType(val.getClass());
				BeanDescription bean=prov.getConfig().getClassIntrospector().forSerialization(prov.getConfig(), javaType, null);
				cache.put(javaType.getRawClass(), linkBuilder=new LinkBuilder(bean));
			}
			// see if we can get some info about the url
			if(prov.getFilterProvider() != null) {
				UriInfoFilter infoFilter=(UriInfoFilter) prov.getFilterProvider().findFilter(UriInfoFilter.KEY);
				if(infoFilter !=null)
					return linkBuilder.make(val,infoFilter.getUri());
			}
			return linkBuilder.make(val);
		}
	}


}
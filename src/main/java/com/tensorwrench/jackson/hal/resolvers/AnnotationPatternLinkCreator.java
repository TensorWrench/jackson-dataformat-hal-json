package com.tensorwrench.jackson.hal.resolvers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.tensorwrench.jackson.hal.HalLinkCreator;
import com.tensorwrench.jackson.hal.util.HalUtils;

public class AnnotationPatternLinkCreator implements HalLinkCreator{
	
	
	static class LinkBuilderImpl {
		private final BeanDescription beanDescription;
		private final BeanPropertyDefinition idProp;
		private final String format;

		public LinkBuilderImpl(BeanDescription bean) throws JsonMappingException {
			beanDescription=bean;
			format=HalUtils.findFormat(bean);
			idProp=HalUtils.findIdProperty(bean);
			if(idProp==null)
				throw new JsonMappingException("Class " + bean.getBeanClass().getCanonicalName() + " does not contain a @HalId");
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
				return uri.resolve("./"+s).toString();
			}
			return s;
		}
	}
	Map<Class<?>,LinkBuilderImpl> cache=new HashMap<>();

	@Override
	public String makeLink(Object val, SerializerProvider prov) throws JsonMappingException {
		synchronized(cache) {
			LinkBuilderImpl linkBuilder=cache.get(val.getClass());
			if(linkBuilder == null) {
				JavaType javaType=prov.getConfig().getTypeFactory().constructType(val.getClass());
				BeanDescription bean=prov.getConfig().getClassIntrospector().forSerialization(prov.getConfig(), javaType, null);
				cache.put(javaType.getRawClass(), linkBuilder=new LinkBuilderImpl(bean));
			}
			// see if we can get some info about the url from the filter, if it's available
			if(prov.getFilterProvider() != null) {
				UriInfoFilter infoFilter=(UriInfoFilter) prov.getFilterProvider().findFilter(UriInfoFilter.KEY);
				if(infoFilter !=null)
					return linkBuilder.make(val,infoFilter.getUri());
			}
			return linkBuilder.make(val);
		}
	}
	


}
package com.tensorwrench.jackson.hal.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.tensorwrench.jackson.hal.annotations.HalId;
import com.tensorwrench.jackson.hal.annotations.HalLink;
import com.tensorwrench.jackson.hal.annotations.HalResource;

public class HalUtils {
	public static <T extends Annotation> T findAnnotation(final Class<?> myClass, final Class<T> annotationClass) {
		Class<?> currentClass = myClass;
		T anno = null;
		while (currentClass != null) {
			anno = currentClass.getAnnotation(annotationClass);
			if (anno != null) {
				return anno;
			}
			currentClass = currentClass.getSuperclass();
		}
		return null;
	}

	public static <T> T defaultConstruct(final Class<T> c) {
		if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
			try {
				return c.getConstructor().newInstance();

			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// no visible default constructor, so just fall through and best guess
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object> concreteCollection(final JavaType collectionClass) {
		final Class<?> c = collectionClass.getRawClass();
		if (!Collection.class.isAssignableFrom(c)) {
			// not a collection
			throw new UnsupportedOperationException("Type " + collectionClass + " is not a Collection");
		}
		// try to default construct the actual class, first
		final Object o = defaultConstruct(c);
		if (o != null) {
			return (Collection<Object>) o;
		}

		// now we guess...
		if (Set.class.isAssignableFrom(c)) {
			return new HashSet<>();
		}
		if (List.class.isAssignableFrom(c)) {
			return new ArrayList<>();
		}
		if (Deque.class.isAssignableFrom(c)) {
			return new ArrayDeque<>();
		}
		if (Queue.class.isAssignableFrom(c)) {
			return new ArrayDeque<>();
		}
		if (Collection.class.isAssignableFrom(c)) {
			// uhh... ArrayList?
			return new ArrayList<>();
		}
		return null;
	}

	public static BeanPropertyDefinition findIdProperty(final BeanDescription bean) {
		for(final BeanPropertyDefinition p:bean.findProperties())
		{
			if(p.getField().hasAnnotation(HalId.class)) {
				return p;
			}
		}
		return null;
	}

	public static String findFormat(final BeanDescription bean) {
		final HalResource r=HalUtils.findAnnotation(bean.getBeanClass(),HalResource.class);
		if(r!=null) {
			return r.urlFormat();
		} 
		return HalResource.DEFAULT_URL_FORMAT;
	}

	public static String findIdParser(final BeanDescription bean) {
		final HalResource r=HalUtils.findAnnotation(bean.getBeanClass(),HalResource.class);
		if(r!=null) {
			return r.idRegex();
		}
		return HalResource.DEFAULT_ID_REGEX;
	}

	public static boolean isHalResource(final JavaType javaType) {
		if(findAnnotation(javaType.getRawClass(),HalResource.class) !=null) {
			return true;
		}
		final JavaType containedClass=javaType.getContentType();
		if(containedClass != null && findAnnotation(containedClass.getRawClass(),HalResource.class) != null) {
			return true;
		}
		return false;
	}

	public static boolean isHalLink(final BeanPropertyWriter p) {
		return p.getAnnotation(HalLink.class)!=null;
	}
	
	private final static Map<Class<?>,Class<?>> primitiveToBoxedClass = new HashMap<> ();
	static {
		primitiveToBoxedClass.put(boolean.class,Boolean.class);
		primitiveToBoxedClass.put(byte.class, Byte.class);
		primitiveToBoxedClass.put(short.class, Short.class);
		primitiveToBoxedClass.put(char.class, Character.class);
		primitiveToBoxedClass.put(int.class, Integer.class);
		primitiveToBoxedClass.put(long.class, Long.class);
		primitiveToBoxedClass.put(float.class, Float.class);
		primitiveToBoxedClass.put(double.class, Double.class);
	}
	
	public static Object valueFromString(final SettableBeanProperty p,String value) throws JsonMappingException {
		try {
			Class<?> c=p.getType().getRawClass();
			if(primitiveToBoxedClass.containsKey(c)) {
				c=primitiveToBoxedClass.get(c);
			}
			
			Constructor<?> constructor = c.getConstructor(String.class);
			return constructor.newInstance(value);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new JsonMappingException("Failed to convert id of "+value+" to " +p,e);
		}
	}
	
	public static String extractId(Class<?> c,String href) throws JsonMappingException {
		HalResource resource=HalUtils.findAnnotation(c, HalResource.class);
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

package com.tensorwrench.jackson.hal.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
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
			return r.format();
		} else {
			try {
				return HalResource.class.getMethod("format").getDefaultValue().toString();
			} catch (NoSuchMethodException | SecurityException e) {
				// meh, we tried...  fall through for hardcoded
			}
		}
		return "{classname}/{id}";
	}

	public static String findIdParser(final BeanDescription bean) {
		final HalResource r=HalUtils.findAnnotation(bean.getBeanClass(),HalResource.class);
		if(r!=null) {
			return r.idParser();
		} else {
			try {
				return HalResource.class.getMethod("idParser").getDefaultValue().toString();
			} catch (NoSuchMethodException | SecurityException e) {
				// meh, we tried...  fall through for hardcoded
			}
		}
		return ".*/(.*)\\??"; // after the last slash, but before any query params
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
}

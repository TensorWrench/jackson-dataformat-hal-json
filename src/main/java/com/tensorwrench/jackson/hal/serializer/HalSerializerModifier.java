package com.tensorwrench.jackson.hal.serializer;

import java.util.*;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.tensorwrench.jackson.hal.HalLinkCreator;
import com.tensorwrench.jackson.hal.util.HalUtils;

public class HalSerializerModifier extends BeanSerializerModifier {
	HalLinkCreator linkCreator;
	
	public HalSerializerModifier(HalLinkCreator linkCreator)	{
		this.linkCreator=linkCreator;
	}

	@Override
	public BeanSerializerBuilder updateBuilder(final SerializationConfig config, final BeanDescription beanDesc, final BeanSerializerBuilder builder) {
		final LinkPropertyWriter links=new LinkPropertyWriter(linkCreator);
		final EmbeddedPropertyWriter embedded=new EmbeddedPropertyWriter();
		final List<BeanPropertyWriter> remove=new ArrayList<>();

		// pull out all of the HAL _embedded or _links elements
		for(final BeanPropertyWriter p: builder.getProperties()) {
			if(HalUtils.isHalResource(p.getType())) {
				if(HalUtils.isHalLink(p)) {
					links.addProp(p);
				} else {
					embedded.addProp(p);
				}
				remove.add(p);
			}
		}

		// remove all the props we've used
		for(final BeanPropertyWriter p: remove) {
			builder.getProperties().remove(p);
		}

		builder.getProperties().add(0,embedded);
		builder.getProperties().add(0,links);

		return builder;
	}

}

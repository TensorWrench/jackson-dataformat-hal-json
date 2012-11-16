package com.tensorwrench.jackson.hal.deserializer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.tensorwrench.jackson.hal.HalLinkResolver;
import com.tensorwrench.jackson.hal.annotations.HalId;
import com.tensorwrench.jackson.hal.util.HalUtils;

public class HalDeserializerModifier extends com.fasterxml.jackson.databind.deser.BeanDeserializerModifier {
	HalLinkResolver linkResolver;
	public HalDeserializerModifier(HalLinkResolver linkResolver) {
		this.linkResolver=linkResolver;
	}

	@Override
	public BeanDeserializerBuilder updateBuilder(final DeserializationConfig config, final BeanDescription beanDesc, final BeanDeserializerBuilder builder)  {
		final LinkPropertyDefinition links=new LinkPropertyDefinition(linkResolver);
		final EmbeddedPropertyDefinition embedded=new EmbeddedPropertyDefinition();
		final List<SettableBeanProperty> remove=new ArrayList<>();

		// pull out all of the HAL _embedded or _links elements
		final Iterator<SettableBeanProperty> prop=builder.getProperties();
		while(prop.hasNext()) {
			final SettableBeanProperty p=prop.next();
			if(p.getAnnotation(HalId.class) != null) {
				links.setSelfProperty(p);
			} else if(HalUtils.isHalResource(p.getType())) {
				embedded.addProp(p);
				links.addProp(p);
				remove.add(p);
			}
		}

		// remove all the props we've used
		for(final SettableBeanProperty p: remove) {
			builder.removeProperty(p.getName());
		}

		builder.addProperty(embedded);
		builder.addProperty(links);

		return builder;
	}
}

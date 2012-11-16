package com.tensorwrench.jackson.hal;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tensorwrench.jackson.hal.deserializer.HalDeserializerModifier;
import com.tensorwrench.jackson.hal.resolvers.AnnotationPatternLinkCreator;
import com.tensorwrench.jackson.hal.resolvers.NewObjectLinkResolver;
import com.tensorwrench.jackson.hal.serializer.HalSerializerModifier;

public class HalModule extends SimpleModule {
	
	HalLinkCreator linkCreator=new AnnotationPatternLinkCreator();
	HalLinkResolver linkResolver=new NewObjectLinkResolver();
	
	@Override
	public void setupModule(SetupContext context) {
		super.setupModule(context);
		context.addBeanDeserializerModifier(new HalDeserializerModifier(linkResolver));
		context.addBeanSerializerModifier(new HalSerializerModifier(linkCreator));
	}

	public HalModule setLinkCreator(HalLinkCreator linkCreator)
	{
		this.linkCreator = linkCreator;
		return this;
	}

	public HalModule setLinkResolver(HalLinkResolver linkResolver)
	{
		this.linkResolver = linkResolver;
		return this;
	}
	
}

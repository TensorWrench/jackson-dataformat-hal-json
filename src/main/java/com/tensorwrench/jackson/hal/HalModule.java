package com.tensorwrench.jackson.hal;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tensorwrench.jackson.hal.deserializer.HalDeserializerModifier;
import com.tensorwrench.jackson.hal.serializer.HalSerializerModifier;

public class HalModule extends SimpleModule {
	
	@Override
	public void setupModule(SetupContext context) {
		super.setupModule(context);
		context.addBeanDeserializerModifier(new HalDeserializerModifier());
		context.addBeanSerializerModifier(new HalSerializerModifier());
	}
}

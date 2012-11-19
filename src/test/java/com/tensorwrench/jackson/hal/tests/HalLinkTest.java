package com.tensorwrench.jackson.hal.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tensorwrench.jackson.hal.Hal;
import com.tensorwrench.jackson.hal.HalLinkCreator;
import com.tensorwrench.jackson.hal.HalLinkResolver;
import com.tensorwrench.jackson.hal.HalModule;
import com.tensorwrench.jackson.hal.models.LinksSimple;
import com.tensorwrench.jackson.hal.models.Simple;

public class HalLinkTest {
	class TestLinkCreator implements HalLinkCreator {
		String link;
		public TestLinkCreator(String link) 	{ this.link=link;}
		@Override	public String makeLink(Object val, SerializerProvider prov) throws JsonMappingException
		{	return link;}
	}
	class TestLinkResolver implements HalLinkResolver {
		Object obj;
		String id;
		
		public TestLinkResolver(Object obj, String id) 	{ this.obj = obj;	this.id = id;	}
		@Override	public String extractIdFromHref(String href, Class<?> resultClass, DeserializationContext ctxt) throws JsonMappingException
		{ assertNotNull(id); return id; }
		@Override
		public Object makeObject(String href, JavaType javaType, DeserializationContext ctxt) throws JsonMappingException
		{ assertNotNull(obj); return obj; }
	}
	
	@Test
	public void linkCreator() throws Exception {
		final String LINK="/simple/123";
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule().setLinkCreator(new TestLinkCreator(LINK)));
		
		String json=om.writer().writeValueAsString(new LinksSimple(3,new Simple("foo", 3.14,6)));
		JsonNode tree=om.reader().readTree(json);
		
		assertNotNull(tree.get(Hal.LINKS),"The Links element should exist");
		assertEquals(tree.get(Hal.LINKS).get("simple").get("href").asText(),LINK);
	}

	@Test
	public void linkResolver() throws Exception {
		final Simple SIMPLE=new Simple("Test",1.234,123);
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule().setLinkResolver(new TestLinkResolver(SIMPLE, "3")));
		
		String json=om.writer().writeValueAsString(new LinksSimple(3,new Simple("foo", 3.14,6)));
		LinksSimple simple=om.reader().withType(LinksSimple.class).readValue(json);
		
		assertEquals(simple.getSimple(),SIMPLE);
	}
	
	@Test
	public void serializeLinkContiains_Link() throws Exception {
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule());
		
		String json=om.writer().writeValueAsString(new LinksSimple(3,new Simple("foo", 3.14,6)));
		JsonNode tree=om.reader().readTree(json);

		assertNotNull(tree.get(Hal.LINKS),"The Links element should exist");
		assertEquals(tree.get(Hal.LINKS).get("simple").get("href").asText(),"simple/6");
	}
	
	@Test
	public void overrideLinkCreator() throws Exception {
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule());
		
		String json=om.writer().writeValueAsString(new LinksSimple(3,new Simple("foo", 3.14,6)));
		JsonNode tree=om.reader().readTree(json);

		assertNotNull(tree.get(Hal.LINKS),"The Links element should exist");
		assertEquals(tree.get(Hal.LINKS).get("simple").get("href").asText(),"simple/6");
	}
}

package com.tensorwrench.jackson.hal.tests;

import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tensorwrench.jackson.hal.HalModule;
import com.tensorwrench.jackson.hal.models.EmbedsSimple;
import com.tensorwrench.jackson.hal.models.Simple;

public class SimpleRoundtripTest {
	
	@DataProvider(name = "objects")
	public Object[][] createData1() {
		return new Object[][] {
				{Simple.class,new Simple("This is a string",3.14159,42)},
				{EmbedsSimple.class,new EmbedsSimple(123,new Simple("foo",1.2,3))}
	 	};
	}
	
	@Test(dataProvider="objects")
	public void roundtrip(Class<?> c,Object o) throws Exception {
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule());
		
		String json=om.writer().writeValueAsString(o);
		Object parsedObject=om.reader().withType(c).readValue(json);
		assertEquals(parsedObject, o, "Expected roundtrip to produce the same object with intermediate json: " + json);
	}
	
	
}

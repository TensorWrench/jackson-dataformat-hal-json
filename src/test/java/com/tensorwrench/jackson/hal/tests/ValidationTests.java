package com.tensorwrench.jackson.hal.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tensorwrench.jackson.hal.HalModule;
import com.tensorwrench.jackson.hal.models.IdLess;
import com.tensorwrench.jackson.hal.models.Simple;

public class ValidationTests {
	@Test
	public void writerThrowsOnNoHalId() throws Exception {
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule());
		
		try {
			om.writer().writeValueAsString(new IdLess());
			fail("Should have thrown a JSONMappingException");
		} catch (JsonMappingException e) {
			assertEquals(e.getMessage().substring(0, e.getMessage().indexOf("@HalId")+6),
					"Class com.tensorwrench.jackson.hal.models.IdLess does not contain a @HalId");
		} 
	}

	@Test
	public void readerThrowsOnNoHalId() throws Exception {
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule());

		try {
			Object o=om.reader().withType(IdLess.class).readValue("{\"integer\":123, \"_links\": { \"self\": { \"href\": \"/idLess/123\"}}}");
			fail("Should have thrown a JSONMappingException but instead made " + o);
		} catch (JsonMappingException e) {
			assertEquals(e.getMessage().substring(0, e.getMessage().indexOf("@HalId")+6),
					"Class com.tensorwrench.jackson.hal.models.IdLess does not contain a @HalId");
		} 
	}
	
	String[] components={
			"\"_links\":{\"self\":{\"href\":\"simple/42\"}}",
			"\"_embedded\":{}",
			"\"stringValue\":\"This is a string\"",
			"\"doubleValue\":3.14159",
		"\"integerValue\":42"
	};
	List<String[]> permutations=new ArrayList<>(); 
	
	{
		createPermutation(new ArrayList<String>(), Arrays.asList(components));
	}
	
	private void createPermutation(List<String> base, Collection<String> available)
	{
		if(available.size() == 0) {
			String s="{";
			for(int i=0;i<base.size()-1;++i)
				s+=base.get(i)+",";
			s+=base.get(base.size()-1) + "}";
			permutations.add(new String[] {s});
			return;
		}
		for(String n: available) {
			List<String> newBase=new ArrayList<>(base);
			Set<String> newAvailable=new HashSet<>(available);
			newBase.add(n);
			newAvailable.remove(n);
			createPermutation(newBase, newAvailable);
		}
	}
	
	@DataProvider(name="permutations")
	public Object[][] jsonPermutationProvider() {
		return permutations.toArray(new String[0][0]);
	}
	
	@Test(dataProvider="permutations")
	public void permutationsDoNotChangeResult(String json) throws Exception {
		ObjectMapper om=new ObjectMapper();
		om.registerModule(new HalModule());
		Simple simple=om.reader().withType(Simple.class).readValue(json);
		assertEquals(simple.getIntegerValue(),42 ); //"\"_links\":{\"self\":{\"href\":\"simple/42\"}}"
		assertEquals(simple.getStringValue(),"This is a string"); //	"\"stringValue\":\"This is a string\"",
		assertEquals(simple.getDoubleValue(),3.14159); // "\"doubleValue\":3.14159",
	}
	
	
}

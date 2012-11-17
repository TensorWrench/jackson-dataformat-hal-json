package com.tensorwrench.jackson.hal.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tensorwrench.jackson.hal.annotations.HalResource;

import static org.testng.Assert.*;

public class DefaultPatternsTest {
	@DataProvider(name = "urls")
	public Object[][] createData1() {
		return new Object[][] {
				{"simple/1",1},
				{"simple/32?key=value", 32},
				{"simple/32#hashtag", 32},
				{"/api/simple/23", 23},
				{"/deep/path/simple/123", 123},
				{"/deep/path/simple/123?key=value",123},
				{"/deep/path/simple/123#hashtag",123},
				{"/deep/path/with/string_id","string_id"},
				{"/deep/path/with/string_id?key=value","string_id"},
				{"/deep/path/with/string_id#hashtag","string_id"}
	 	};
	}
	
  @Test(dataProvider="urls")
  public void testIdPattern(String url, Object value) {
  	Matcher m=Pattern.compile(HalResource.DEFAULT_ID_REGEX).matcher(url);
  	m.matches();
  	
  	assertEquals(m.group(1),value.toString());
  }
}

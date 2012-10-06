package com.tensorwrench.jackson.hal.util;

import java.io.IOException;
import java.lang.reflect.Field;

import com.fasterxml.jackson.core.*;

public class Debug {
  public static void jgenState(JsonGenerator jgen) {
		try {
			Field buffer = jgen.getClass().getDeclaredField("_writer");
			buffer.setAccessible(true);
			Object writer=buffer.get(jgen);
			buffer=writer.getClass().getDeclaredField("_buffer");
			buffer.setAccessible(true);
			System.err.println("Current jgen: " + buffer.get(writer));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// debugging tools, don't care much if we can't print it
			e.printStackTrace();
		} 
  }
  
  public static void eatObject(JsonParser jp) throws JsonParseException, IOException {
		int depth=1;
		if(jp.getCurrentToken()!=JsonToken.START_OBJECT) {
			throw new IOException("Expected data to start with an object, not " + jp.getCurrentToken() + "=" + jp.getCurrentName());
		}
		while(depth > 0) {
			System.err.println("DEBUG: Eating token " + jp.getCurrentToken() + "=" + jp.getCurrentName() + " at depth " + depth);
			jp.nextToken();
			if(jp.getCurrentToken() == JsonToken.START_OBJECT)
				depth++;
			if(jp.getCurrentToken() == JsonToken.END_OBJECT)
				depth--;
		}
  }
}

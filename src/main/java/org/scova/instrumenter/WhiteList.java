package org.scova.instrumenter;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.InsnList;

public class WhiteList {
	
	public static boolean isAssertMethod(String methodName) {
		
		final Set<String> registeredAsserts = new HashSet<String>();
		registeredAsserts.add("assertEquals");
		registeredAsserts.add("assertNotNull");
		registeredAsserts.add("assertNull");
		registeredAsserts.add("assertTrue");
		registeredAsserts.add("assertFalse");	
		registeredAsserts.add("assertArrayEquals");
		registeredAsserts.add("assertNotSame");
		registeredAsserts.add("assertSame");
		registeredAsserts.add("assertThat");
		
		return registeredAsserts.contains(methodName);
		
	}

	public static boolean isIgnoredField(String field) {
		if (field.equals("this"))
			return true;
		
		String escapedString = ".+";
		escapedString += java.util.regex.Pattern.quote("$");
		escapedString += ".+";
		
		return field.matches(escapedString);
	}

}

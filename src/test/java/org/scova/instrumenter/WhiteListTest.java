package org.scova.instrumenter;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteListTest {

	@Test
	public void test() {
		assertTrue(WhiteList.isIgnoredField("this"));
		assertTrue(WhiteList.isIgnoredField("this$1"));
		assertTrue(WhiteList.isIgnoredField("this$2"));
		assertTrue(WhiteList.isIgnoredField("this$3"));

		assertTrue(WhiteList.isIgnoredField("val$expected"));

		assertFalse(WhiteList.isIgnoredField("this$"));
		assertFalse(WhiteList.isIgnoredField("sajsajo"));
		assertFalse(WhiteList.isIgnoredField("sa09"));
	}

}

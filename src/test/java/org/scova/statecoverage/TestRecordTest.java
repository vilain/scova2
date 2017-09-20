package org.scova.statecoverage;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.scova.statecoverage.TestRecord;

public class TestRecordTest {

	@Test
	public void testAddAssertToTest() {
		
		TestRecord testRecord = new TestRecord();
		testRecord.addAssert("HumanName.IsCelebrity()");
		
		List<String> asserts = testRecord.getAsserts();
		assertEquals(1, asserts.size());
		assertEquals("HumanName.IsCelebrity()", asserts.get(0));
	}
	
	@Test
	public void testAddStateModifications() {
		TestRecord testRecord = new TestRecord();
		testRecord.addModification("HumanName.last");
		
		Set<String> modifications = testRecord.getModifiedStates();
		assertEquals(1, modifications.size());	
		assertTrue(modifications.contains("HumanName.last"));
	}

}

package org.scova.statecoverage;

import java.util.Hashtable;

import org.scova.statecoverage.TestRecord;

public class TestRegistry {
	
	Hashtable<String, TestRecord> tests = new Hashtable<String, TestRecord>();

	public TestRecord addTest(String testName) {
		TestRecord record = new TestRecord();
		tests.put(testName, record);
		return record;
	}

	public boolean hasRegistryFor(String testName) {
		return tests.containsKey(testName);
	}
	
	public TestRecord getRecordFor(String testName) {
		if (!hasRegistryFor(testName))
			addTest(testName);
		
		return tests.get(testName);
	}

}

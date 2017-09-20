package org.scova.statecoverage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.scova.statecoverage.InfluenceMap;
import org.scova.statecoverage.StateCoverage;
import org.scova.statecoverage.StateCoverageResult;
import org.scova.statecoverage.StateCoverageSolver;
import org.scova.statecoverage.TestRecord;
import org.scova.statecoverage.TestRegistry;
import org.scova.statecoverage.Utils;

public class StateCoverage {
	
	// TODO: ver se eh necessario manter o registro de influencias global
	static InfluenceMap globalInfluences = new InfluenceMap();
	static TestRegistry testRegistry = new TestRegistry();
	
	static TestRecord actualTest = null;
	
//	private static String log = new String();	
	
	private static void dump(String str) {
//		log += str;
//		log += "\n";
	}

	public static void AddDependency(String target, String source) {
//		System.out.println("Adding dependency of " + source + " to " + target);
		
		dump(target + " <- " + source);
		
		globalInfluences.addDependency(target, source);
		
		if (verifyActualTest())
			actualTest.addDependency(target, source);
	}
	
	private static boolean verifyActualTest() {
		if (actualTest == null) {
			dump("warning: add modification outside a test");
			return false;
		}
		return true;
	}

	public static void AddTestDependency(String target, String source) {
		
		dump(target + " <- " + source);
		
		if (verifyActualTest()) {
			actualTest.addDependency(target, source);		
			actualTest.ignoreState(target);
		}
	}

	public static void ClearDependenciesOf(String target) {
		
		dump(target + " <- empty");
		
		globalInfluences.clearDependenciesOf(target);
		if (verifyActualTest()) {
			actualTest.clearDependenciesOf(target);
		}
	}
	
	public static void ClearTestDependenciesOf(String target) {
		
		dump(target + " <- empty");
		
		globalInfluences.clearDependenciesOf(target);
		
		if (verifyActualTest()) {
			actualTest.clearDependenciesOf(target);
			actualTest.ignoreState(target);
		}
	}
	
	public static void AddAssert(String assertPredicate) {
		
		dump("add assert : " + assertPredicate);
		
		if (verifyActualTest()) {
			actualTest.addAssert(assertPredicate);
		}
	}

	public static void AddModification(String target) {
		
		dump("add modification : " + target);
		
		if (verifyActualTest()) {
			actualTest.addModification(target);
		}
	}

	public static void BeginTestCapture(String testName) {
		System.out.println("--------------------------------------");
		System.out.println("Begin test capture for : " + testName);
		dump("Beginning test : " + testName);
		actualTest = testRegistry.addTest(testName);
	}
	
	public static void EndTestCapture(String testName) {
		
		dump("Ending test : " + testName);
		//Utils.dumpToFile("c:/sc_output/dump.txt", log);
		actualTest = null;
		StateCoverageResult result = StateCoverage.GetResultFor(testName);
		System.out.println(result.toString());
		
		Utils.dumpToFile(Utils.escape(testName) + ".json", result.toJson());
		
		System.out.println("End test capture for : " + testName);
		System.out.println("--------------------------------------");
		
	}

	public static StateCoverageResult GetResultFor(String test) {
		
		StateCoverageSolver solver = new StateCoverageSolver(testRegistry);
		return solver.computeStateCoverageFor(test, true);
//		return solver.computeStateCoverageFor(test);
	}



}


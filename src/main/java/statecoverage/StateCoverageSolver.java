package statecoverage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import statecoverage.StateCoverageResult;
import statecoverage.TestRecord;
import statecoverage.TestRegistry;


public class StateCoverageSolver {
	
	private TestRegistry registry = null;

	public StateCoverageSolver(TestRegistry registry) {
		this.registry = registry;
	}
	
	private Set<String> getTotalModifiedState(TestRecord testRecord) {
		
		return testRecord.getModifiedStates();
	}
	
	private Set<String> getTotalCoveredState(TestRecord testRecord) {
		
		List<String> asserts = testRecord.getAsserts();
		
		Set<String> totalCoveredState = new HashSet<String>();
		
		for (String assertPred : asserts) {
			
			Set<String> influences = testRecord.getInfluencesOf(assertPred);
			totalCoveredState.addAll(influences);
			
			// adicionamos o pr�prio assert se ele n�o for um dos ignorados
			if (!testRecord.ignores(assertPred))
				totalCoveredState.add(assertPred);
		}
		
		return totalCoveredState;
	}

	public StateCoverageResult computeStateCoverageFor(String testName) {
		
		TestRecord testRecord = registry.getRecordFor(testName);
		
		Set<String> totalModifiedState = getTotalModifiedState(testRecord);
		Set<String> totalCoveredState = getTotalCoveredState(testRecord);
		
		return new StateCoverageResult(testName, totalCoveredState, totalModifiedState);
	}
	
	public StateCoverageResult computeStateCoverageFor(String testName, boolean attributesOnly) {
		
		TestRecord testRecord = registry.getRecordFor(testName);
		
		Set<String> totalModifiedState = getTotalModifiedState(testRecord);
		Set<String> totalCoveredState = getTotalCoveredState(testRecord);
		
		return new StateCoverageResult(testName, totalCoveredState, totalModifiedState, true);
	}

}

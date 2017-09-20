package org.scova.statecoverage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.scova.statecoverage.StateCoverageResult;
import org.scova.statecoverage.StateCoverageSolver;
import org.scova.statecoverage.TestRecord;
import org.scova.statecoverage.TestRegistry;

public class StateCoverageSolverTest {
	
	TestRegistry registry = new TestRegistry();
//	InfluenceMap influenceMap = null;
	TestRecord testRecord = null;
	StateCoverageSolver solver = null;
	
	@Before
	public void setUp() {
		testRecord = registry.getRecordFor("HumanNameTest.test");
//		influenceMap = testRecord.getInfluenceMap();
		solver = new StateCoverageSolver(registry);
	}

	@Test
	public void testFullCoverageShouldResultIn1()
	{
		testRecord.addModification("first");
		testRecord.addAssert("first");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test");
		assertEquals(1, result.getStateCoverageValue(), 0.001);
		
	}
	
	@Test
	public void test() {
		
		testRecord.addModification("HumanName.last");
		testRecord.addDependency("HumanName.last", "HumanName.HumanName().oneNameCelebrity");
		testRecord.addModification("HumanName.first");
		testRecord.clearDependenciesOf("HumanName.first");
		testRecord.addDependency("HumanName.IsCelebrity()", "HumanName.first");
		
		testRecord.addAssert("HumanName.IsCelebrity()");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test", true);
		
		// TODO: criar implementa��es para os tr�s tipos de state coverage
		
		// isso seria de acordo com a defini��o de Koster et al
//		assertEquals(3, result.getTotalModified());
//		assertTrue(result.getModifiedStates().contains("HumanName.last"));
//		assertTrue(result.getModifiedStates().contains("HumanName.first"));
//		assertTrue(result.getModifiedStates().contains("HumanName.IsCelebrity()"));

		assertEquals(2, result.getTotalModified());
		assertTrue(result.getModifiedStates().contains("HumanName.last"));
		assertTrue(result.getModifiedStates().contains("HumanName.first"));
		
		// isso seria de acordo com a defini��o de Koster et al
//		assertEquals(2, result.getTotalCovered());
//		assertTrue(result.getCoveredStates().contains("HumanName.first"));
//		assertTrue(result.getCoveredStates().contains("HumanName.IsCelebrity()"));
		
		assertEquals(1, result.getTotalCovered());
		assertTrue(result.getCoveredStates().contains("HumanName.first"));

		// TODO: ver se eh certo 0.5 ou 0.333 ou 0.66
		// isso seria de acordo com a defini��o de Koster et al
//		assertEquals(0.666, result.getStateCoverageValue(), 0.001);

		assertEquals(0.5, result.getStateCoverageValue(), 0.001);
	}
	
	@Test
	public void testShouldCountCoveredOnlyTheExactMatches()
	{
		testRecord.addDependency("first", "second");
		testRecord.addAssert("assert");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test");
		assertEquals(0, result.getStateCoverageValue(), 0.001);
	}
	
	
	@Test
	public void testShouldBeAbleToComputeOnlyAttributes()
	{
		testRecord.addDependency("IsDependency()", "other");
		testRecord.addModification("last");
		testRecord.addDependency("last", "first");
		
		testRecord.addAssert("last");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test", true);
		assertEquals(1, result.getStateCoverageValue(), 0.001);
	}

}

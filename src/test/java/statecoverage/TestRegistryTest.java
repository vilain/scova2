package statecoverage;

import static org.junit.Assert.*;
import org.junit.Test;

import statecoverage.TestRegistry;

public class TestRegistryTest {

	@Test
	public void testAddTest() {
		
		TestRegistry registry = new TestRegistry();
		registry.addTest("HumanNameTest.test");
		
		assertTrue(registry.hasRegistryFor("HumanNameTest.test"));
	}
	
	
}

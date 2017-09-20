package org.scova.statecoverage;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.scova.statecoverage.InfluenceMap;

public class InfluenceMapTest {

	@Test
	public void TestAddDependency()
	{
		InfluenceMap influence = new InfluenceMap();
		influence.addDependency("attr1", "attr2");

		Set<String> influences = influence.getInfluencesOf("attr1");
		assertEquals(1, influences.size());
		assertTrue(influences.contains("attr2"));
	}

	@Test
	public void TestDependencyIsTransitive()
	{
		InfluenceMap influence = new InfluenceMap();
		influence.addDependency("attr1", "attr2");
		influence.addDependency("attr2", "attr3");

		Set<String> influences = influence.getInfluencesOf("attr1");
		assertEquals(2, influences.size());
		assertTrue(influences.contains("attr2"));
		assertTrue(influences.contains("attr3"));
	}

	@Test
	public void TestThreeLevelsOfDependencies()
	{
		InfluenceMap influence = new InfluenceMap();
		influence.addDependency("attr1", "attr2");
		influence.addDependency("attr2", "attr3");
		influence.addDependency("attr3", "attr4");

		Set<String> influences = influence.getInfluencesOf("attr1");
		assertEquals(3, influences.size());
		assertTrue(influences.contains("attr2"));
		assertTrue(influences.contains("attr3"));
		assertTrue(influences.contains("attr4"));
	}

	@Test
	public void TestMoreComplexDependencyTree()
	{
		InfluenceMap influence = new InfluenceMap();
		influence.addDependency("attr1", "attr2");
		influence.addDependency("attr1", "attr3");
		influence.addDependency("attr3", "attr4");

		Set<String> influences = influence.getInfluencesOf("attr1");
		assertEquals(2, influences.size());
		assertTrue(!influences.contains("attr2"));
		assertTrue(influences.contains("attr3"));
		assertTrue(influences.contains("attr4"));
	}

	@Test
	public void TestOverrideDependenciesOnSecondAssignment()
	{
		InfluenceMap influence = new InfluenceMap();
		influence.addDependency("attr1", "attr2");
		influence.addDependency("attr1", "attr3");

		Set<String> influences = influence.getInfluencesOf("attr1");
		assertEquals(1, influences.size());
		assertTrue(!influences.contains("attr2"));
		assertTrue(influences.contains("attr3"));
	}
	
	@Test
	public void TestShouldBeAbleToClearDependenciesOfAnAttribute()
	{
		InfluenceMap influence = new InfluenceMap();
		influence.addDependency("attr1", "attr2");
		influence.clearDependenciesOf("attr1");

		Set<String> influences = influence.getInfluencesOf("attr1");
		assertTrue(influences.isEmpty());
	}

	// TODO: resolver isso...
	//@Test
	//public void TestCyclicDependency()
	//{
	//    InfluenceMap influence = new InfluenceMap();
	//    influence.AddDependency("attr1", "attr2");
	//    influence.AddDependency("attr2", "attr3");
	//    influence.AddDependency("attr3", "attr1");

	//    HashSet<string> influences = influence.GetInfluencesOf("attr1");
	//    assertThat(influences.Count, Is.EqualTo(3));
	//    assertThat(influences.Contains("attr2"));
	//    assertThat(influences.Contains("attr3"));
	//}

}

package org.scova.statecoverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scova.statecoverage.InfluenceMap;

public class TestRecord {
	
	List<String> asserts = new ArrayList<String>();
	Set<String> modifiedStates = new HashSet<String>();
	InfluenceMap influences = new InfluenceMap();

	public void addAssert(String assertPredicate) {
		asserts.add(assertPredicate);
	}

	public List<String> getAsserts() {
		return asserts;
	}

//	public InfluenceMap getInfluenceMap() {
//		return influences;
//	}

	public void addModification(String modification) {
		modifiedStates.add(modification);
	}

	public Set<String> getModifiedStates() {
		return modifiedStates;
	}

	public void addDependency(String target, String source) {
		influences.addDependency(target, source);
	}

	public void ignoreState(String state) {
		influences.ignoreState(state);
	}

	public void clearDependenciesOf(String target) {
		influences.clearDependenciesOf(target);
	}

	public Set<String> getInfluencesOf(String target) {
		return influences.getInfluencesOf(target);
	}

	public boolean ignores(String target) {
		return influences.ignores(target);
	}

}
package statecoverage;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import statecoverage.Dependencies;

public class InfluenceMap {

	private Hashtable<String, Dependencies> influences = new Hashtable<String, Dependencies>();

	// a set of states to ignore.. mainly because are test local variables.
	// TODO: search for a better place
	private Set<String> statesToIgnore = new HashSet<String>();

	public void addDependency(String target, String source) {
		Dependencies influences = getOrCreateDependencies(target);
		influences.addDependency(source);
	}

	private Dependencies getOrCreateDependencies(String attr) {
		if (!influences.containsKey(attr))
			influences.put(attr, new Dependencies());

		return influences.get(attr);
	}

	public Set<String> getInfluencesOf(String attr) {
		Set<String> result = getOrCreateDependencies(attr).getDependencies();

		Set<String> dependenciesOf = new HashSet<String>(result);
		for (String dep : dependenciesOf) {
			if (!dep.isEmpty() && !dep.equals(attr)) // avoid recursion.. if an
														// identifier influences
														// himself (ex: i+= 1)
				result.addAll(getInfluencesOf(dep));
		}

		return result;
	}

	public Set<String> getAllTargets() {
		Set<String> targets = new HashSet<String>(influences.keySet());
		targets.removeAll(statesToIgnore);
		return targets;
	}

	public void ignoreState(String stateToIgnore) {
		statesToIgnore.add(stateToIgnore);
	}

	public boolean ignores(String state) {
		return statesToIgnore.contains(state);
	}

	public void clearDependenciesOf(String target) {
		Dependencies influences = getOrCreateDependencies(target);
		influences.clearDependencies();
	}
}

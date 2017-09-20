package statecoverage;
import java.util.HashSet;
import java.util.Set;


class Dependencies {
	private Set<String> dependencies = new HashSet<String>();

	public void addDependency(String influence) {
//		dependencies.clear();
		dependencies.add(influence);
	}

	public Set<String> getDependencies() {
		return dependencies;
	}

	public void clearDependencies() {
		dependencies.clear();
	}
}

package statecoverage;

import java.util.HashSet;
import java.util.Set;

public class StateCoverageResult {
	
	private Set<String> statesCovered = null;
	private Set<String> statesModified = null;
	private String testName = null;
	
	public StateCoverageResult(String testName
			, Set<String> statesCovered, Set<String> statesModified) {
		
		this.statesCovered = statesCovered;
		this.statesModified = statesModified;
		this.testName = testName;
	}
	
	public StateCoverageResult(String testName
			, Set<String> statesCovered, Set<String> statesModified, boolean attributesOnly) {
		
		this.statesCovered = new HashSet<String>();
		this.statesModified = new HashSet<String>();
		
		// eliminamos aqui todos as fun��es...
		for (String state : statesCovered) {
			if (!state.contains(")"))
				this.statesCovered.add(state);
		}
		
		for (String state : statesModified) {
			if (!state.contains(")"))
				this.statesModified.add(state);
		}
		
		this.testName = testName;
	}

	public int getTotalModified() {
		return statesModified.size();
	}

	public double getStateCoverageValue() {
		
		Set<String> intersection = getUsefullCoveredStates();
		return getStateCoverageValue(intersection);
	}
	
	private double getStateCoverageValue(Set<String> usefullCoveredStates) {
		if (statesModified.isEmpty())
			return 0.0;		
		
		return (double)usefullCoveredStates.size() / statesModified.size();		
	}

	public int getTotalCovered() {
		return statesCovered.size();
	}

	public Set<String> getModifiedStates() {
		return statesModified;
	}
	
	public Set<String> getCoveredStates() {
		return statesCovered;
	}
	
	public Set<String> getUselessCoveredStates(Set<String> usefullStatesCovered) {
		
		Set<String> diff = new HashSet<String>(statesCovered);
		diff.removeAll(usefullStatesCovered);		
		return diff;
	}
	
	public Set<String> getUsefullCoveredStates() {
		// if the copy here is bad in performance we could use the method proposed here:
				// http://stackoverflow.com/questions/7574311/efficiently-compute-intersection-of-two-sets-in-java
		Set<String> intersection = new HashSet<String>(statesModified);
		intersection.retainAll(statesCovered);
		return intersection;
	}
	
	private String setToString(Set<String> set) {
		String result = new String();
		result += " [ ";
		
		String[] arr = set.toArray(new String[0]);
		for (int i = 0; i < arr.length; i++) {
			result += "\"";
			result += arr[i];
			result += "\"";
			if (i != arr.length-1)
				result += ", ";
		}
		result += " ]";
		return result;
	}
	
	@Override
	public String toString() {
		String result = new String();
		result += "State Coverage Report for ";
		result += testName;
		result += "\n";
		result += "Modified states: ";
		result += statesModified.size();
		result += setToString(statesModified);
		result += "\n";
		result += "Covered states: ";
		result += statesCovered.size();
		result += setToString(statesCovered);
		result += "\n";
		result += "State Coverage: ";
		result += getStateCoverageValue();
		return result;
	}

	public String toJson() {
		
		String result = new String();
		
		Set<String> usefull = getUsefullCoveredStates();
		Set<String> useless = getUselessCoveredStates(usefull);		
		
		result += "{\n";
		result += "  \"test_name\": \"" + testName + "\",\n";
		result += "  \"modified_states\": \"" + statesModified.size() + "\",\n";
		result += "  \"covered_states\": \"" + usefull.size() + "\",\n";
		result += "  \"useless_covered_states\": \"" + useless.size() + "\",\n";		
		result += "  \"modified\": " + setToString(statesModified) + ",\n" ;
		result += "  \"covered\": " + setToString(usefull) + ",\n" ;
		result += "  \"useless\": " + setToString(useless) + ",\n" ;
		result += "  \"state_coverage\": \"" + getStateCoverageValue(usefull) + "\"\n";
		result += "}";
		
		return result;
	}
	
	public String toHtmlRow() {
		String result = new String();
		
		result += "<tr>";
		result += "<td>" + statesModified.size() + "</td>";
		result += "<td>" + statesCovered.size()  + "</td>";
		result += "<td>" + getStateCoverageValue() + "</td>";
		result += "</tr>";
		
		return result;
	}
	

}

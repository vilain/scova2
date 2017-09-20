package org.scova.plugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResult extends Result {
	
	private String projectName;
	
	public TestResult(String testName, String projectName) {
		super(testName, null);
		this.projectName = projectName;
	}

	private Map<String, PackageResult> packageResult = new HashMap<String, PackageResult>();

	public void addField(List<String> info, boolean isCovered) {
		assert (info.size() == 3);
		
		String packageName = info.get(0);
		
		if (!packageResult.containsKey(packageName)) {
			packageResult.put(packageName, new PackageResult(packageName, this));
		}
		
		PackageResult pack = packageResult.get(packageName);
		ClassResult classResult = pack.addAndReturnClass(info.get(1));
		classResult.addField(info.get(2), isCovered);
	}

	public Object[] getPackages() {
		
		return packageResult.values().toArray();
	}

	public String getProjectName() {
		return projectName;
	}

	
}

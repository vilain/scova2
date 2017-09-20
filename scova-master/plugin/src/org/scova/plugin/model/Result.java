package org.scova.plugin.model;

public class Result {
	private Result parent;
	
	private String name;

	public Result(String testName, Result parent) {
		this.setParent(parent);
		name = testName;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Result getParent() {
		return parent;
	}


	public void setParent(Result parent) {
		this.parent = parent;
	}
}

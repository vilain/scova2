package org.scova.plugin.model;

public class FieldResult extends Result {

	private boolean isCovered = false;
	
	public FieldResult(String fieldName, boolean isCovered, Result parent) {
		super(fieldName, parent);
		this.isCovered = isCovered;
	}

	public boolean isCovered() {
		return isCovered;
	}
	
	public void setIsCovered(boolean isCovered) {
		this.isCovered = isCovered;
	}
	
}

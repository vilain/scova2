package org.scova.plugin.model;

import java.util.ArrayList;
import java.util.List;

public class ClassResult extends Result {
	public ClassResult(String className, Result parent) {
		super(className, parent);
	}

	private List<FieldResult> fieldResult = new ArrayList<FieldResult>();

	public void addField(String fieldName, boolean isCovered) {
		fieldResult.add(new FieldResult(fieldName, isCovered, this));
	}

	public Object[] getFields() {
		return fieldResult.toArray();
	}
	
	public String getFullyQualifiedName() {
		return this.getParent().getName() + "." + this.getName();
	}
}

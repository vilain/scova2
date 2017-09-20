package org.scova.plugin.views;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.scova.plugin.model.ClassResult;
import org.scova.plugin.model.PackageResult;
import org.scova.plugin.model.TestResult;

public class ScovaReportContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
	

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List) {
			return ((List)parentElement).toArray();
		}
		if (parentElement instanceof TestResult) {
			return ((TestResult)parentElement).getPackages();
		}
		if (parentElement instanceof PackageResult) {
			return ((PackageResult)parentElement).getClasses();
		}
		if (parentElement instanceof ClassResult) {
			return ((ClassResult)parentElement).getFields();
		}
		
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof TestResult || element instanceof PackageResult || element instanceof ClassResult);
	}

}

package org.scova.plugin.views;

import org.eclipse.core.runtime.IProgressMonitor;

public class ScovaProgressMonitor implements IProgressMonitor{

	@Override
	public void beginTask(String name, int totalWork) {
		// TODO Auto-generated method stub
		System.out.println("Beginnig Scova Execution");
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
		System.out.println("Ending Scova Execution");
		
	}

	@Override
	public void internalWorked(double work) {
		// TODO Auto-generated method stub
		System.out.println("...");
		
	}

	@Override
	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCanceled(boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTaskName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subTask(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void worked(int work) {
		// TODO Auto-generated method stub
		
	}

}

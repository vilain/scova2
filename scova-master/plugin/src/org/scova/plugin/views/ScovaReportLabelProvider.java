package org.scova.plugin.views;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.scova.plugin.model.ClassResult;
import org.scova.plugin.model.FieldResult;
import org.scova.plugin.model.PackageResult;
import org.scova.plugin.model.Result;
import org.scova.plugin.model.TestResult;
import org.eclipse.jdt.ui.JavaUI;

public class ScovaReportLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof TestResult)
			return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_DEFAULT);
		if (element instanceof PackageResult)
			return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
		if (element instanceof ClassResult)
			return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
		if (element instanceof FieldResult) {
			FieldResult field = (FieldResult)element;
			if (field.isCovered())
				return getBundleImage("covered.gif");
			else
				return getBundleImage("not_covered.gif");
		}
			
		return null;
	}
	
	private Image getBundleImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(ScovaReportLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Result) 
			return ((Result)element).getName();
		return "";
	}



}

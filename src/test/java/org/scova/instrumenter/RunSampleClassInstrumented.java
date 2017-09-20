package org.scova.instrumenter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.runner.Result;

public class RunSampleClassInstrumented {
	
	public static void main(String [] args) throws Exception {
		
		instrumentClass("c:/users/aniceto/workspace/scova_new/bin/org/scova/instrumenter/SampleClass$CommandLine.class");
		Class<?> testclass = instrumentAndLoadClass("c:/users/aniceto/workspace/scova_new/bin/org/scova/instrumenter/SampleClass.class");
		
		Result result = org.junit.runner.JUnitCore.runClasses(testclass);
		if (!result.wasSuccessful())
			throw new Exception("Error on test");
	}
	
	private static Class<?> instrumentAndLoadClass(String pathOfClass) {

		instrumentClass(pathOfClass);
		Class<SampleClass> classLoaded = loadTestClass();
		
		return classLoaded;
	}

	public static void instrumentClass(String pathOfClass) {
		StateCoverageAsm stateCoverage = 
				new StateCoverageAsm();
		stateCoverage.instrumentClass(pathOfClass, pathOfClass);
	}
	
	private static Class<SampleClass> loadTestClass() {
		// Create a File object on the root of the directory containing the class file
		File file = new File("c:/users/aniceto/workspace/scova_new/bin/");

		try {
		    // Convert File to a URL
		    URL url = file.toURL();          // file:/c:/myclasses/
		    URL[] urls = new URL[]{url};

		    // Create a new class loader with the directory
		    ClassLoader cl = new URLClassLoader(urls);

		    // Load in the class; MyClass.class should be located in
		    // the directory file:/c:/myclasses/com/mycompany
		    Class cls = cl.loadClass("org.scova.instrumenter.SampleClass");
		    return cls;
		    
		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		}
		
		return null;
		
	}
	

}

package org.scova.instrumenter;
import java.io.IOException;


public class Main {
	
	public static void main(String[] args) {
		
		System.out.println("Usage: sc [input_folder] [output_folder]");
		
//		String inputFolder = "C:/Users/Martim/workspace/table/java";
		String inputFolder = "C:/Users/Aniceto/workspace/commons-logging-1.2-src/commons-logging-1.2-src";
//		String inputFolder = "C:/Users/Aniceto/workspace/statecoverage/externals/tablelize_it/java";
//		String inputFolder = "C:/Users/Aniceto/workspace/tablelize_it/java";
		
		
		String outputFolder = "C:/sc_commons_pela_ide";		
		
		inputFolder = args[0];
		System.out.println("Input folder: " + inputFolder);
		outputFolder = args[1];
		System.out.println("Output folder: " + outputFolder);
		
		StateCoverageAsm instrumenter = new StateCoverageAsm();
		
		if (args.length > 2) {
			String classToInstrument = args[2];
			
			try {
				instrumenter.instrumentClass(classToInstrument);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}
		
		
		
		try {
			instrumenter.instrumentFolder(inputFolder, outputFolder);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

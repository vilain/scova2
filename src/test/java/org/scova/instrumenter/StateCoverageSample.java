package org.scova.instrumenter;
import static org.junit.Assert.*;

import org.junit.Test;
import org.objectweb.asm.util.ASMifier;

import statecoverage.StateCoverage;


public class StateCoverageSample {
	
	int c = 0;
	
	@SuppressWarnings(value = { "unused" })
	public void setA(int a) {
		StateCoverage.AddDependency("target", "source");
		int b = c;
	}
	
	private String get(String a) {
		return "a";
	}
	
	@Test
	public void test() {
		StateCoverage.BeginTestCapture("FixtureTest.testShouldReadExampleFromString()");
		String a = "";
//		get(a);
		assertEquals("e", get(get(a)));
	}
	
	public static void main(String[] args) throws Exception {
		
		ASMifier.main(new String[] {"StateCoverageSample"});
		
		
//		Textifier.main(new String[] {"C:\\sc_output\\bin\\tablelize\\Table.class"});
		
		/*TraceClassVisitor t = new TraceClassVisitor(new PrintWriter(System.out));
		
		ClassReader reader = null;
		try {
			reader = new ClassReader("StateCoverageSample");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		reader.accept(t, ClassReader.SKIP_DEBUG);*/
		
	}

}

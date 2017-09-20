package org.scova.instrumenter;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class SampleClass {

	private int a = 0;
	private int other = 1;

	public void localAssignment(int a) {
		int c = a;
	}

	public void fieldTolocalVarAssignment() {
		int b = this.a;
	}

	public void localVarToFieldAssignment() {
		int local = 0;
		this.a = local;
	}

	public void fieldToFieldAssignement() {
		this.a = this.other;
	}

	public int getA() {
		return this.a;
	}

	public int getLocal() {
		int local = 9;
		return local;
	}

	@Test
	public void testInt() {

		int a = 0;
		assertEquals(0, a);
	}

	@Test
	public void testString() {
		String a = "test";
		assertEquals("test", a);
	}

	private List<String> list = new ArrayList<String>();

	public void testListModification() {
		list.add("aaa");
	}

	@Test
	public void testAssertWithMethodCall() {
		assertEquals(0, getA());
	}

	@Test
	public void testAssertListProperties() {
		list.add("aaa");
		assertEquals(1, getListCount());
		// assertEquals(1, list.size()); // TODO: tratar diretamente a asserção
		// da list
	}

	private int getListCount() {
		return list.size();
	}

	@Test
	public void testPop() {
		// isso deve gerar um pop, pois ninguém armazena o resultado da função
		getListCount();
	}

	private static List<Integer> staticList = new ArrayList<Integer>();

	public static void modifyStaticList() {
		staticList.add(1);
	}

	@Test
	public void testWhileIteratorSample() {

		List<String> myList = new ArrayList<String>();
		myList.add("ola mundo");

		Iterator<String> it = myList.iterator();
		while (it.hasNext()) {
			String n = it.next();
			assertTrue(true); // only to add an assert here
		}
	}

	public void testDupX() {

		int count = 0;
		if (count++ > 0) {
			count = 0;
		}
	}

	@SuppressWarnings("unused")
	private final Properties getConfigurationFile(ClassLoader classLoader,
			String fileName) {
		Properties props = null;
		double priority = 0.0;

		ArrayList<String> urls = new ArrayList<String>();
		Iterator<String> it = urls.iterator();

		while (it.hasNext()) {

			Properties newProps = new Properties();
			if (newProps != null) {
				if (props == null) {
					priority = 0.0;
				} else {
					String newPriorityStr = "";
					double newPriority = 0.0;
					if (newPriorityStr != null) {
						newPriority = 0;
					}

					props = newProps;
					priority = newPriority;
				}

			}
		}

		return props;
	}

	class DSCompiler {
		public int getIdx() {
			return 0;
		}
	}

	class DerivativeStructure {

		/** Compiler for the current dimensions. */
		private transient DSCompiler compiler;

		/** Combined array holding all values. */
		private final double[] data;

		public DerivativeStructure(DSCompiler compiler, double[] data) {
			this.compiler = compiler;
			this.data = data;
		}

		public DerivativeStructure abs() {
			if (Double.doubleToLongBits(data[0]) < 0) {
				// we use the bits representation to also handle -0.0
				return this;
			} else {
				return this;
			}
		}

		public double getPartialDerivative(final int... orders) {
			return data[compiler.getIdx()];
		}
	}

	@Test
	public void testAbs() {

		DerivativeStructure minusOne = new DerivativeStructure(
				new DSCompiler(), new double[] {});
//		Assert.assertEquals(+1.0, minusOne.abs().getPartialDerivative(0),
//				1.0e-15);
		Assert.assertEquals(1, minusOne.abs().getPartialDerivative(0)
				, 0.1);

	}

	class CommandLine {

		private List<String> options = new ArrayList<String>();

		public boolean hasOption(String opt) {
			return options.contains("");
		}
	}

	@Test
	public void testAnt() throws Exception {
		CommandLine line = new CommandLine();

		// check multiple values
//		String[] opts = line.getOptionValues("D");
//		assertEquals("property", opts[0]);

		// check single value
		// esse assert tbm deveria funcionar
//		assertEquals(line.getOptionValue("buildfile"), "mybuild.xml");

		// check option
		assertFalse(line.hasOption("projecthelp"));
	}
	
	// Test ability to lookup and execute single non-delegating command
	@Test
    public void testAssertBesideCatch() {
		
		CommandLine line = new CommandLine();
        try {        	
            assertFalse(line.hasOption("projecthelp"));
        } catch (Exception e) {
        }        
    } 
	
	abstract class Abstract {
		@Test
		public abstract void abstractMethod();
	}
	
	final class TokenMatchers {
		public void matches(final String expectedContent) {
			class Test {
				String getExpected() {
					return expectedContent;
				}
				
			}
		}
		
	}
	
	String argName;
	public boolean hasArgName() {
		return argName.length() > 0;
				//argName != null && argName.length() > 0;
	}


}

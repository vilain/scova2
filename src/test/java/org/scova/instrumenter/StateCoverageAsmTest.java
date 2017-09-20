package org.scova.instrumenter;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


public class StateCoverageAsmTest {
	
	private void instrumentClass(String inputClass) {
		
		StateCoverageAsm stateCoverage = new StateCoverageAsm();
		try {
			stateCoverage.instrumentClass(inputClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ClassNode readClass(String className) {
        ClassReader cr = null;
		try {
			FileInputStream file = new FileInputStream(className + ".class.adapted");
			cr = new ClassReader(file);
			ClassNode cn = new ClassNode();
			cr.accept(cn, ClassReader.SKIP_DEBUG);
			return cn;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	void assertCode(MethodNode method, String expectedCode) {
		assertEquals(expectedCode, DebugUtils.codeToString(method));
	}
	
	private ClassNode result = null;
	@Before
	public void setUp() {
		result = instrumentAndReadClass("bin/org/scova/instrumenter/SampleClass.class");
//		instrumentClass("bin/org/scova/instrumenter/SampleClass.class");
//		result = readClass("bin/org/scova/instrumenter/SampleClass.class");
	}
	
	private ClassNode instrumentAndReadClass(String pathToClass) {
		instrumentClass(pathToClass);
		return readClass(pathToClass);
	}
	
	@Test
	public void testLocalVarToLocalVarAssignment() {
		
		// setA
		MethodNode setter = result.methods.get(2);
		assertEquals("localAssignment", setter.name);
		assertEquals(8, setter.instructions.size());
		
		assertCode(setter, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.localAssignment(I)V.c\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.localAssignment(I)V.c\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.localAssignment(I)V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
						   "    ILOAD 1\n" +
						   "    ISTORE 2\n" +
						   "    RETURN\n");
	}
	
	@Test
	public void testFieldToLocalVarAssignment() {
				
		MethodNode setter = result.methods.get(3);
		assertEquals("fieldTolocalVarAssignment", setter.name);
		assertEquals(9, setter.instructions.size());
		
		assertCode(setter, 
				 		   "    LDC \"org/scova/instrumenter/SampleClass.fieldTolocalVarAssignment()V.b\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.fieldTolocalVarAssignment()V.b\"\n"+
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD org/scova/instrumenter/SampleClass.a : I\n" +
			    		   "    ISTORE 1\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testLocalVarToFieldAssignment() {
				
		MethodNode method = result.methods.get(4);
		assertEquals("localVarToFieldAssignment", method.name);
		assertEquals(15, method.instructions.size());
		
		assertCode(method, 
			    "    LDC \"org/scova/instrumenter/SampleClass.localVarToFieldAssignment()V.local\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    ICONST_0\n" +
			    "    ISTORE 1\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.localVarToFieldAssignment()V.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    PUTFIELD org/scova/instrumenter/SampleClass.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testFieldToFieldAssignment() {
				
		MethodNode method = result.methods.get(5);
		assertEquals("fieldToFieldAssignement", method.name);
		assertEquals(12, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    	       "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    	       "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.other\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD org/scova/instrumenter/SampleClass.other : I\n" +
			    		   "    PUTFIELD org/scova/instrumenter/SampleClass.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testReturnField() {
		
		MethodNode method = result.methods.get(6);
		assertEquals("getA", method.name);
		assertEquals(8, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getA()I\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getA()I\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD org/scova/instrumenter/SampleClass.a : I\n" +
			    		   "    IRETURN\n");
	}
	
	@Test
	public void testReturnLocal() {
		
		MethodNode method = result.methods.get(7);
		assertEquals("getLocal", method.name);
		assertEquals(11, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getLocal()I.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    BIPUSH 9\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getLocal()I\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getLocal()I\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getLocal()I.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ILOAD 1\n" +
			    		   "    IRETURN\n");
	}
	
	@Test
	public void testShouldInstrumentTests_int() {
		
		MethodNode method = result.methods.get(8);
		assertEquals("testInt", method.name);
		assertEquals(15, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testInt()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testInt()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    ICONST_0\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testInt()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LCONST_0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    I2L\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testInt()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentTests_String() {
		
		MethodNode method = result.methods.get(9);
		assertEquals("testString", method.name);
		assertEquals(14, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testString()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testString()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ASTORE 1\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testString()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ALOAD 1\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (Ljava/lang/Object;Ljava/lang/Object;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testString()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentListModifications() {
		
		MethodNode method = result.methods.get(10);
		assertEquals("testListModification", method.name);
		assertEquals(8, method.instructions.size());
		assertCode(method, 
						   "    LDC \"org/scova/instrumenter/SampleClass.list\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
						   "    GETFIELD org/scova/instrumenter/SampleClass.list : Ljava/util/List;\n" + 
			    		   "    LDC \"aaa\"\n" +
			    		   "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    		   "    POP\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testInstrumentAssertWithCalls() {
		
		assertInstrumentation(11, "testAssertWithMethodCall", 14, 
						   "    LDC \"org/scova/instrumenter/SampleClass.testAssertWithMethodCall()V\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
						   "    LDC \"org/scova/instrumenter/SampleClass.getA()I\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
						   "    LDC \"org/scova/instrumenter/SampleClass.testAssertWithMethodCall()V.this\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
						   "    LCONST_0\n" +
						   "    ALOAD 0\n" +
						   "    INVOKEVIRTUAL org/scova/instrumenter/SampleClass.getA ()I\n" +
						   "    I2L\n" +
						   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
						   "    LDC \"org/scova/instrumenter/SampleClass.testAssertWithMethodCall()V\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
						   "    RETURN\n");
	}
	
	private void assertInstrumentation(int methodIndex, String methodName, int instructionsCount, String code) {
		assertInstrumentationOf(result, methodIndex, methodName, instructionsCount, code);
	}
	
	private void assertInstrumentationOf(ClassNode classNode
			, int methodIndex, String methodName, int instructionsCount, String code) {

		MethodNode method = classNode.methods.get(methodIndex);
		assertEquals(methodName, method.name);
		assertEquals(instructionsCount, method.instructions.size());
		assertCode(method, code);
	}
	
	@Test
	public void testShouldAddDependencyOnListProperties() {
		assertInstrumentation(13, "getListCount", 9, 
				
			    "    LDC \"org/scova/instrumenter/SampleClass.getListCount()I\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getListCount()I\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.list\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD org/scova/instrumenter/SampleClass.list : Ljava/util/List;\n" +
			    "    INVOKEINTERFACE java/util/List.size ()I\n" +
			    "    IRETURN\n");
	}
	
	@Test
	public void testAssertListProperties() {
		assertInstrumentation(12, "testAssertListProperties", 21, 

			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.list\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD org/scova/instrumenter/SampleClass.list : Ljava/util/List;\n" +
			    "    LDC \"aaa\"\n" +
			    "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    "    POP\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getListCount()I\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertListProperties()V.this\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    "    LCONST_1\n" +
			    "    ALOAD 0\n" +
			    "    INVOKESPECIAL org/scova/instrumenter/SampleClass.getListCount ()I\n" +
			    "    I2L\n" +
			    "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
			    "    RETURN\n"); 
	}
	
	@Test
	public void testInstrumentPop() {
		
		assertInstrumentation(14, "testPop", 8, 
			    "    LDC \"org/scova/instrumenter/SampleClass.testPop()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
				"    ALOAD 0\n" +
				"    INVOKESPECIAL org/scova/instrumenter/SampleClass.getListCount ()I\n" +
				"    POP\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testPop()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
				"    RETURN\n");
	}
	
	@Test
	public void testAddModificationOnStaticList() {
		
		assertInstrumentation(15, "modifyStaticList", 8, 
			    "    LDC \"staticList\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    "    GETSTATIC org/scova/instrumenter/SampleClass.staticList : Ljava/util/List;\n" +
			    "    ICONST_1\n" +
			    "    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;\n" +
			    "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    "    POP\n" +
			    "    RETURN\n");
		
	}
	
	@Test 
	public void testWhileIterator() {
		
		assertInstrumentation(16, "testWhileIteratorSample", 50,
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.myList\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    NEW java/util/ArrayList\n" +
				"    DUP\n" +
				"    INVOKESPECIAL java/util/ArrayList.<init> ()V\n" +
				"    ASTORE 1\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.myList\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
				"    ALOAD 1\n" +
				"    LDC \"ola mundo\"\n" +
				"    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
				"    POP\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.it\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.it\"\n" +
				"    LDC \"java/util/List.iterator()Ljava/util/Iterator;\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.it\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.myList\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
				"    ALOAD 1\n" +
				"    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator;\n" +
				"    ASTORE 2\n" +
				"    GOTO L0\n" +
				"   L1\n" +
				"   FRAME APPEND [java/util/ArrayList java/util/Iterator]\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.n\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.n\"\n" +
				"    LDC \"java/util/Iterator.next()Ljava/lang/Object;\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.n\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.it\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
				"    ALOAD 2\n" +
				"    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object;\n" +
				"    CHECKCAST java/lang/String\n" +
				"    ASTORE 3\n" +
				"    ICONST_1\n" +
				"    INVOKESTATIC org/junit/Assert.assertTrue (Z)V\n" +
				"   L0\n" +
				"   FRAME SAME\n" +
				"    ALOAD 2\n" +
				"    INVOKEINTERFACE java/util/Iterator.hasNext ()Z\n" +
				"    IFNE L1\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
				"    RETURN\n");
	}
	
	//@Test
	public void testDupX() {
		assertInstrumentation(17, "testDupX", 18, 
			   "    LDC \"org/scova/instrumenter/SampleClass.count\"\n" +  
			   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" + 
			   "    LDC \"org/scova/instrumenter/SampleClass.count\"\n" + 
			   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" + 
			   "    ALOAD 0\n" + 
			   "    DUP\n" + 
			   "    GETFIELD org/scova/instrumenter/SampleClass.count : I\n" +
			   "    DUP_X1\n" + 
			   "    ICONST_1\n" +
			   "    IADD\n" +
			   "    PUTFIELD org/scova/instrumenter/SampleClass.count : I\n" +
			   "    IFLE L0\n" +
			   "    ALOAD 0\n" +
			   "    ICONST_0\n" +
			   "    PUTFIELD org/scova/instrumenter/SampleClass.count : I\n" +
			   "   L0\n" +
			   "   FRAME SAME\n" +			    
			   "    RETURN\n");
	}
	
	@Test
	public void testBugInfiniteLoop() {
		
		assertInstrumentation(18, "getConfigurationFile", 87, 
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.props\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    ACONST_NULL\n" +
			    "    ASTORE 3\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.priority\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 4\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.urls\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    NEW java/util/ArrayList\n" +
			    "    DUP\n" +
			    "    INVOKESPECIAL java/util/ArrayList.<init> ()V\n" +
			    "    ASTORE 6\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.it\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.it\"\n" +
			    "    LDC \"java/util/ArrayList.iterator()Ljava/util/Iterator;\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.it\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.urls\"\n" + 
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 6\n" +
			    "    INVOKEVIRTUAL java/util/ArrayList.iterator ()Ljava/util/Iterator;\n" +
			    "    ASTORE 7\n" +
			    "    GOTO L0\n" +
			    "   L1\n" +
			    "   FRAME FULL [org/scova/instrumenter/SampleClass java/lang/ClassLoader java/lang/String java/util/Properties D java/util/ArrayList java/util/Iterator] []\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newProps\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    NEW java/util/Properties\n" +
			    "    DUP\n" +
			    "    INVOKESPECIAL java/util/Properties.<init> ()V\n" +
			    "    ASTORE 8\n" +
			    "    ALOAD 8\n" +
			    "    IFNULL L0\n" +
			    "    ALOAD 3\n" +
			    "    IFNONNULL L2\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.priority\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 4\n" +
			    "    GOTO L0\n" +
			    "   L2\n" +
			    "   FRAME APPEND [java/util/Properties]\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newPriorityStr\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    LDC \"\"\n" +
			    "    ASTORE 9\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newPriority\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 10\n" +
			    "    ALOAD 9\n" +
			    "    IFNULL L3\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newPriority\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 10\n" +		
			    "   L3\n" +
			    "   FRAME APPEND [java/lang/String D]\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.props\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.props\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newProps\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 8\n" +
			    "    ASTORE 3\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.priority\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.priority\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newPriority\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    DLOAD 10\n" +
			    "    DSTORE 4\n" +
			    "   L0\n" +
			    "   FRAME CHOP 3\n" +
			    "    ALOAD 7\n" +
			    "    INVOKEINTERFACE java/util/Iterator.hasNext ()Z\n" +
			    "    IFNE L1\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.props\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 3\n" +   
			    "    ARETURN\n");
	}
	
	@Test
	public void testBugCommonsMath() {
		
		assertInstrumentation(19, "testAbs", 36, 
				"    LDC \"org/scova/instrumenter/SampleClass.testAbs()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testAbs()V.minusOne\"\n" + 
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    NEW org/scova/instrumenter/SampleClass$DerivativeStructure\n" +
				"    DUP\n" +
				"    ALOAD 0\n" +
				"    NEW org/scova/instrumenter/SampleClass$DSCompiler\n" +
				"    DUP\n" +
				"    ALOAD 0\n" +
				"    INVOKESPECIAL org/scova/instrumenter/SampleClass$DSCompiler.<init> (Lorg/scova/instrumenter/SampleClass;)V\n" +
				"    ICONST_0\n" +
				"    NEWARRAY T_DOUBLE\n" +		    
				"    INVOKESPECIAL org/scova/instrumenter/SampleClass$DerivativeStructure.<init> (Lorg/scova/instrumenter/SampleClass;Lorg/scova/instrumenter/SampleClass$DSCompiler;[D)V\n" +
				"    ASTORE 1\n" +
				"    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.getPartialDerivative([I)D\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.abs()Lorg/scova/instrumenter/SampleClass$DerivativeStructure;\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testAbs()V.minusOne\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
				"    DCONST_1\n" +
				"    ALOAD 1\n" +
				"    INVOKEVIRTUAL org/scova/instrumenter/SampleClass$DerivativeStructure.abs ()Lorg/scova/instrumenter/SampleClass$DerivativeStructure;\n" +
				"    ICONST_1\n" +
				"    NEWARRAY T_INT\n" +
				"    DUP\n" +
				"    ICONST_0\n" +
				"    ICONST_0\n" +
				"    IASTORE\n" +
				"    INVOKEVIRTUAL org/scova/instrumenter/SampleClass$DerivativeStructure.getPartialDerivative ([I)D\n" +
				"    LDC 0.1\n" +
				"    INVOKESTATIC org/junit/Assert.assertEquals (DDD)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testAbs()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
				"    RETURN\n"
			    );
		
	    ClassNode classNode = instrumentAndReadClass("bin/org/scova/instrumenter/SampleClass$DerivativeStructure.class");
	    assertInstrumentationOf(classNode, 2, "getPartialDerivative", 18,
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.getPartialDerivative([I)D\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.getPartialDerivative([I)D\"\n" + 
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.data\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.getPartialDerivative([I)D\"\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DSCompiler.getIdx()I\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.getPartialDerivative([I)D\"\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.compiler\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
	    		"    ALOAD 0\n" +
	    		"    GETFIELD org/scova/instrumenter/SampleClass$DerivativeStructure.data : [D\n" + 
	    	    "    ALOAD 0\n" +
	    	    "    GETFIELD org/scova/instrumenter/SampleClass$DerivativeStructure.compiler : Lorg/scova/instrumenter/SampleClass$DSCompiler;\n" +
	    	    "    INVOKEVIRTUAL org/scova/instrumenter/SampleClass$DSCompiler.getIdx ()I\n" +
	    		"    DALOAD\n" +
	    		"    DRETURN\n");

	}

	@Test
	public void testBugCommonsCli() {
		
		assertInstrumentation(20, "testAnt", 20,
			    "    LDC \"org/scova/instrumenter/SampleClass.testAnt()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAnt()V.line\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
				"    NEW org/scova/instrumenter/SampleClass$CommandLine\n" +
				"    DUP\n" +
				"    ALOAD 0\n" +
				"    INVOKESPECIAL org/scova/instrumenter/SampleClass$CommandLine.<init> (Lorg/scova/instrumenter/SampleClass;)V\n" +
				"    ASTORE 1\n" +
				"    LDC \"org/scova/instrumenter/SampleClass$CommandLine.hasOption(Ljava/lang/String;)Z\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testAnt()V.line\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
				"    ALOAD 1\n" +
				"    LDC \"projecthelp\"\n" + 
				"    INVOKEVIRTUAL org/scova/instrumenter/SampleClass$CommandLine.hasOption (Ljava/lang/String;)Z\n" +
				"    INVOKESTATIC org/junit/Assert.assertFalse (Z)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAnt()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
				"    RETURN\n");

	    ClassNode classNode = instrumentAndReadClass("bin/org/scova/instrumenter/SampleClass$CommandLine.class");
	    assertInstrumentationOf(classNode, 1, "hasOption", 13,
	    	    "    LDC \"org/scova/instrumenter/SampleClass$CommandLine.hasOption(Ljava/lang/String;)Z\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$CommandLine.hasOption(Ljava/lang/String;)Z\"\n" +
	    	    "    LDC \"java/util/List.contains(Ljava/lang/Object;)Z\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$CommandLine.hasOption(Ljava/lang/String;)Z\"\n" +
	    	    "    LDC \"org/scova/instrumenter/SampleClass$CommandLine.options\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
	    	    "    ALOAD 0\n" +
	    	    "    GETFIELD org/scova/instrumenter/SampleClass$CommandLine.options : Ljava/util/List;\n" +
	    	    "    LDC \"\"\n" +
	    	    "    INVOKEINTERFACE java/util/List.contains (Ljava/lang/Object;)Z\n" +
	    	    "    IRETURN\n");
	}

	@Test
	public void testBugCommonsChain() {
		
		assertInstrumentation(21, "testAssertBesideCatch", 30,
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertBesideCatch()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertBesideCatch()V.line\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    NEW org/scova/instrumenter/SampleClass$CommandLine\n" +
			    "    DUP\n" +
			    "    ALOAD 0\n" +
			    "    INVOKESPECIAL org/scova/instrumenter/SampleClass$CommandLine.<init> (Lorg/scova/instrumenter/SampleClass;)V\n" +
			    "    ASTORE 1\n" +
			    "   L0\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass$CommandLine.hasOption(Ljava/lang/String;)Z\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertBesideCatch()V.line\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    "    ALOAD 1\n" +
			    "    LDC \"projecthelp\"\n" +
			    "    INVOKEVIRTUAL org/scova/instrumenter/SampleClass$CommandLine.hasOption (Ljava/lang/String;)Z\n" +
			    "    INVOKESTATIC org/junit/Assert.assertFalse (Z)V\n" +
			    "   L1\n" +
			    "    GOTO L2\n" +
			    "   L3\n" +
			    "   FRAME FULL [org/scova/instrumenter/SampleClass org/scova/instrumenter/SampleClass$CommandLine] [java/lang/Exception]\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertBesideCatch()V.hidden2\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    ASTORE 2\n" +
			    "   L2\n" +
			    "   FRAME SAME\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertBesideCatch()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
			    "    RETURN\n");
	}
    
	@Test
	public void testAbstract() {
		
	    ClassNode classNode = instrumentAndReadClass("bin/org/scova/instrumenter/SampleClass$Abstract.class");
	    assertInstrumentationOf(classNode, 1, "abstractMethod", 0, "");
	}
	
	@Test
	public void testAnonymousInnerClassUsingParameter() {
		
	    ClassNode classNode = instrumentAndReadClass("bin/org/scova/instrumenter/SampleClass$TokenMatchers$1Test.class");
	    assertInstrumentationOf(classNode, 0, "<init>", 9, 
	    "    ALOAD 0\n" +
	    "    ALOAD 1\n" +
	    "    PUTFIELD org/scova/instrumenter/SampleClass$TokenMatchers$1Test.this$1 : Lorg/scova/instrumenter/SampleClass$TokenMatchers;\n" +
	    "    ALOAD 0\n" +
	    "    ALOAD 2\n" +
	    "    PUTFIELD org/scova/instrumenter/SampleClass$TokenMatchers$1Test.val$expectedContent : Ljava/lang/String;\n" +
	    "    ALOAD 0\n" +
	    "    INVOKESPECIAL java/lang/Object.<init> ()V\n" +
	    "    RETURN\n");
	}
	
	@Test
	public void testBooleanReturn() {
		assertInstrumentation(22, "hasArgName", 26,
			    "    ALOAD 0\n" +
				"    GETFIELD org/scova/instrumenter/SampleClass.argName : Ljava/lang/String;\n" +				
				"    INVOKEVIRTUAL java/lang/String.length ()I\n" +
				"    IFLE L0\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.hasArgName()Z\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.hasArgName()Z\"\n" +
			    "    LDC \"java/lang/String.length()I\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.hasArgName()Z\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.argName\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
				"    ICONST_1\n" +
				"    IRETURN\n" +
				"   L0\n" +
				"   FRAME SAME\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.hasArgName()Z\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.ClearDependenciesOf (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.hasArgName()Z\"\n" +
			    "    LDC \"java/lang/String.length()I\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.hasArgName()Z\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.argName\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
				"    ICONST_0\n" +
				"    IRETURN\n");		
	}
}

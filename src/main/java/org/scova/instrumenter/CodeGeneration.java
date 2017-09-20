package org.scova.instrumenter;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.Opcodes;


public class CodeGeneration {

	public static InsnList generateAssertCode(String assertFunc) {

		InsnList il = new InsnList();
		il.add(new LdcInsnNode(assertFunc));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				"statecoverage/StateCoverage", "AddAssert",
				"(Ljava/lang/String;)V"));
		return il;
	}
	public static InsnList generateBeginTestCode(String testName) {

		InsnList il = new InsnList();
		il.add(new LdcInsnNode(testName));
		il.add(new MethodInsnNode(StateCoverageClassAdapter.INVOKESTATIC,
				"statecoverage/StateCoverage", "BeginTestCapture",
				"(Ljava/lang/String;)V"));
		return il;
	}

	public static InsnList generateEndTestCode(String testName) {

		InsnList il = new InsnList();
		il.add(new LdcInsnNode(testName));
		il.add(new MethodInsnNode(StateCoverageClassAdapter.INVOKESTATIC,
				"statecoverage/StateCoverage", "EndTestCapture",
				"(Ljava/lang/String;)V"));
		return il;
	}
	
	public static InsnList generateAddDependencyCode(String target, String source) {
		
		InsnList il = new InsnList();
		il.add(new LdcInsnNode(target));
		il.add(new LdcInsnNode(source));
		il.add(new MethodInsnNode(StateCoverageClassAdapter.INVOKESTATIC,
				"statecoverage/StateCoverage", "AddDependency",
				"(Ljava/lang/String;Ljava/lang/String;)V"));
		return il;
	}

	public static InsnList generateAddModification(String target) {
		assert(!target.isEmpty());
		InsnList il = new InsnList();
		il.add(new LdcInsnNode(target));
		il.add(new MethodInsnNode(StateCoverageClassAdapter.INVOKESTATIC,
				"statecoverage/StateCoverage", "AddModification",
				"(Ljava/lang/String;)V"));
		return il;
	}

	public static InsnList generateClearDependencies(String target) {
		assert(!target.isEmpty());
		InsnList il = new InsnList();
		il.add(new LdcInsnNode(target));
		il.add(new MethodInsnNode(StateCoverageClassAdapter.INVOKESTATIC,
				"statecoverage/StateCoverage", "ClearDependenciesOf",
				"(Ljava/lang/String;)V"));
		return il;
	}

	public static String prepareFullyQualifiedName(String klass, String name, String var) {
		return klass + "." + name + "." + var;
	}

	public static String prepareFullyQualifiedName(String klass, String name) {
		return klass + "." + name;
	}

}

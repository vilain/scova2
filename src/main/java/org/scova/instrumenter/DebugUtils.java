package org.scova.instrumenter;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;


public class DebugUtils {

	public static String codeToString(MethodNode method) {
		
		Textifier t1 = new Textifier();
		MethodVisitor mv = new TraceMethodVisitor(t1);
		for (int j = 0; j < method.instructions.size(); ++j) {
			Object insn = method.instructions.get(j);
			((AbstractInsnNode) insn).accept(mv);
		}
		
		StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
		
		t1.print(printWriter);
		
		return stringWriter.toString();
	}
}

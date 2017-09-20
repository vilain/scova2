package org.scova.instrumenter;
//package instrumenter.core;
//
//import org.objectweb.asm.tree.AbstractInsnNode;
//import org.objectweb.asm.tree.VarInsnNode;
//
//public class XStoreInstrumenter {
//
//	public XStoreInstrumenter() {
//		
//	}
//	
//	public SCValue execute(final AbstractInsnNode insn,
//            final SCValue value) {
//		 
//		if (!value.getName().isEmpty()) { // soh podemos adicionar dependencia se o source do store for um identificador
//
//			String target = extractNameForLoadAndStore((VarInsnNode)insn);
//			this.addInstrumentation(CodeGeneration.generateAddDependencyCode(target, value.getName()), lastStatement);
//		}
//
//		String target = extractNameForLoadAndStore((VarInsnNode)insn);
//		this.addInstrumentation(CodeGeneration.generateAddModification(target), lastStatement);
//		this.lastStatement = insn;
//		return new SCValue(value.getSize(), insn, "");
//	}
//
//}

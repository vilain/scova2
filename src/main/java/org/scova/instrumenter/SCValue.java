package org.scova.instrumenter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.SourceValue;


public class SCValue extends SourceValue {
	
	
//	private String name = "";
	private List<String> identifiers = new ArrayList<String>();

	public SCValue(int size) {
		super(size);
	}

	public SCValue(int size, AbstractInsnNode insn) {
		super(size, insn);
	}

	public SCValue(int size, Set<AbstractInsnNode> insns
			, List<String> identifiers) {
		super(size, insns);
		this.identifiers = identifiers;
	}
	
	public SCValue(int size, AbstractInsnNode insn, List<String> identifiers) {
		super(size, insn);
		this.identifiers = identifiers;
	}


	public List<String> getIdentifiers() {
		return this.identifiers;
	}
	
}

package org.scova.instrumenter;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class SCFrame extends Frame<SCValue> {

	public SCFrame(Frame<? extends SCValue> src) {
		super(src);
	}
	
    public SCFrame(final int nLocals, final int nStack) {
    	super(nLocals, nStack);
    }
    
    @Override
    public void execute(final AbstractInsnNode insn,
            final Interpreter<SCValue> interpreter) throws AnalyzerException {
    	super.execute(insn, interpreter);
    	
//    	if (this.getStackSize() == this.getLocals())
//    		((SCInterpreter)interpreter).setLastInstruction(insn);
    	
//        switch (insn.getOpcode()) {
//        case Opcodes.POP:
//        	//((SCInterpreter)interpreter).pop(insn);
//        	((SCInterpreter)interpreter).setLastInstruction(insn);
//    	}
    }

}

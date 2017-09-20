package org.scova.instrumenter;

import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class SCAnalyzer extends Analyzer<SCValue> {

	private SCInterpreter interpreter;
	public SCAnalyzer(SCInterpreter interpreter) {
		super(interpreter);
		this.interpreter = interpreter;		
	}

    @Override
    protected Frame<SCValue> newFrame(final int nLocals, final int nStack) {
    	return new SCFrame(nLocals, nStack);
    }

    /**
     * Constructs a new frame that is identical to the given frame.
     * 
     * @param src
     *            a frame.
     * @return the created frame.
     */
    @Override
    protected Frame<SCValue> newFrame(final Frame<? extends SCValue> src) {
        return new SCFrame(src);
    }
    
    protected void newControlFlowEdge(final int insn, final int successor) {
    	
    	interpreter.storeJumpInfluences(insn, getFrames()[insn], successor);
    }

    @Override
    protected boolean newControlFlowExceptionEdge(final int insn,
            final int successor) {
        return true;
    }

    @Override
    protected boolean newControlFlowExceptionEdge(final int insn,
            final TryCatchBlockNode tcb) {
        return super.newControlFlowExceptionEdge(insn, tcb);
    }
}

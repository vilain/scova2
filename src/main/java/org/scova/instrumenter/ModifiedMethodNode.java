package org.scova.instrumenter;
import java.util.Iterator;
import java.util.ListIterator;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


final class ModifiedMethodNode extends MethodNode {
		
		/**
		 * 
		 */
		private final StateCoverageClassAdapter stateCoverageClassAdapter;
		private String className = "";
		private ClassVisitor cv;
		
		ModifiedMethodNode(StateCoverageClassAdapter stateCoverageClassAdapter, ClassVisitor cv, int api, int access, String name,
				String desc, String signature, String[] exceptions, String className) {
			super(api, access, name, desc, signature, exceptions);
			this.stateCoverageClassAdapter = stateCoverageClassAdapter;
			this.cv = cv;
			this.className = className;
		}
		

		@Override
		public void visitEnd() {
			// put your transformation code here
			
			boolean isTest = Utils.hasTestAnnotation(this);
			
			System.out.println("Method " + name + " of class " + this.stateCoverageClassAdapter.owner);
			
			Iterator<AbstractInsnNode> it = this.instructions.iterator();
			while (it.hasNext()) {

				AbstractInsnNode instr = it.next();

				if (isLocalVarToLocalVarAssignment(instr))
					handleLocalVarToLocalVarAssignment( ((VarInsnNode) instr).var, instr.getPrevious());
				else if (isFieldToLocalVarAssignement(instr))
					handleFieldToLocalVarAssignment( ((VarInsnNode) instr).var, instr.getPrevious());
				else if (isLocalVarToFieldAssignement(instr))
					handleLocalVarToFieldAssignment(instr);
				else if (isFieldToFieldAssignement(instr)) 
					handleFieldToFieldAssignment(instr);
				else if (isReturn(instr))
					handleReturn(instr);
				else if (isAssert(instr))
					handleAssert(instr);
				// ifs...
				// chamadas de outros métodos
			}
			
			if (isTest) {
				instrumentTest();
			}

			accept(this.cv);
		}
		
		private void handleAssert(AbstractInsnNode instr) {
			
//			MethodParameterExtractor extractor = new MethodParameterExtractor();
//			extractor.extract((MethodInsnNode)instr, null);
			
			/*List<MethodParameterExtractor> extractors = new ArrayList<MethodParameterExtractor>();
			extractors.add(new MethodCallExtractor());
			
			InsnList firstParameterCode = null;
			InsnList secondParameterCode = null;
			
			AbstractInsnNode baseInstr = instr;
			for (MethodParameterExtractor extractor : extractors) {
				firstParameterCode = extractor.extract(baseInstr);
				if (firstParameterCode != null) {
					baseInstr = instr;
					break;
				}
			}
			
			// second parameter
			for (MethodParameterExtractor extractor : extractors) {
				secondParameterCode = extractor.extract(baseInstr);
				if (secondParameterCode != null) {
					baseInstr = instr;
					break;
				}
			}*/
			
//			this.instructions.insert(baseInstr, firstParameterCode);
//			this.instructions.insert(baseInstr, secondParameterCode);
			
//			// chamada de método como parametro do assert
			if (instr.getPrevious().getOpcode() == StateCoverageClassAdapter.INVOKESPECIAL) {
				MethodInsnNode method = (MethodInsnNode)instr.getPrevious();
				String className = method.owner;
				String methodName = method.name;
				
				InsnList il = CodeGeneration.generateAssertCode(CodeGeneration.prepareFullyQualifiedName(className, methodName));
				this.instructions.insert(instr.getPrevious().getPrevious(), il);
			}
			// conversão de int para long.. @see StateCoverageAsmTest.testShouldInstrumentTests_int()
			else if (instr.getPrevious().getOpcode() == StateCoverageClassAdapter.I2L) {
				if (instr.getPrevious().getPrevious().getOpcode() == StateCoverageClassAdapter.ILOAD) {
					VarInsnNode node = (VarInsnNode)instr.getPrevious().getPrevious();
					InsnList il = CodeGeneration.generateAssertCode(CodeGeneration.prepareFullyQualifiedName(this.className, this.name, new Integer(node.var).toString()));
					this.instructions.insert(instr.getPrevious().getPrevious().getPrevious(), il);
				}
			}
				
		}

		private boolean isAssert(AbstractInsnNode instr) {
			if (instr.getOpcode() != StateCoverageClassAdapter.INVOKESTATIC)
				return false;
			
			MethodInsnNode node = (MethodInsnNode)instr;		
			if (node.name.equals("assertEquals"))
				return true;
			
			return false;
		}

		private void instrumentTest() {
			
			InsnList begin = CodeGeneration.generateBeginTestCode(this.getFullyQualifiedMethodName());
			this.instructions.insert(begin);
			InsnList end = CodeGeneration.generateEndTestCode(this.getFullyQualifiedMethodName());
			
			// find the return and insert before
			ListIterator<AbstractInsnNode> it = this.instructions.iterator(this.instructions.size() - 1);
			while (it.hasPrevious()) {
				AbstractInsnNode instr = it.previous();
				if (instr.getOpcode() >= StateCoverageClassAdapter.IRETURN && instr.getOpcode() <= StateCoverageClassAdapter.RETURN) {
					this.instructions.insertBefore(instr, end);		
				}
			}
		}



		private String getMethodName() {
			return name + desc;
		}

		private void handleReturn(AbstractInsnNode instr) {
			
			if (instr.getPrevious().getOpcode() == StateCoverageClassAdapter.GETFIELD) {
				String source = ((FieldInsnNode) instr.getPrevious()).name;

				System.out.println("Find return of " + source
						+ " in method " + name);

				InsnList il = CodeGeneration.generateAddDependencyCode(
						getFullyQualifiedMethodName(),
						CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, source));

				// nesse caso são 3 instruções antes de inserir
				this.instructions.insertBefore(instr.getPrevious()
						.getPrevious(), il);
			}
			
			

			else if (instr.getPrevious().getOpcode() >= StateCoverageClassAdapter.ILOAD
					&& instr.getPrevious().getOpcode() <= StateCoverageClassAdapter.ALOAD) {

				int source = ((VarInsnNode) instr.getPrevious()).var;

				System.out.println("Find return of " + source
						+ " in method " + name);

				InsnList il = CodeGeneration.generateAddDependencyCode(
						getFullyQualifiedMethodName(),
						CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, getMethodName(),
								new Integer(source).toString()));

				// nesse caso são 2 instruções antes de inserir (xRETURN +
				// xLOAD)
				this.instructions.insertBefore(instr.getPrevious(), il);
			}
		}

		private String getFullyQualifiedMethodName() {
			return CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, getMethodName());
		}

		private boolean isReturn(AbstractInsnNode instr) {
			return instr.getOpcode() >= StateCoverageClassAdapter.IRETURN
					&& instr.getOpcode() <= StateCoverageClassAdapter.RETURN;
		}

		private void handleFieldToFieldAssignment(AbstractInsnNode instr) {
			// nesse caso teremos quatro instruções associadas...
			// ALOAD 0 -> this do PUTFIELD
			// ALOAD 0 -> this do GETFIELD
			// GETFIELD -> Resultado de GETFIELD...
			// PUTFIELD -> será o source do PUTFIELD
			String target = ((FieldInsnNode) instr).name;
			String source = ((FieldInsnNode) instr.getPrevious()).name;

			System.out.println("Find assignement of field " + source
					+ " to field " + target);

			InsnList il = CodeGeneration.generateAddDependencyCode(
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, target),
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, source));

			// nesse caso são 4 instruções antes de inserir
			this.instructions.insertBefore(instr.getPrevious()
					.getPrevious().getPrevious(), il);

		}

		private boolean isFieldToFieldAssignement(AbstractInsnNode instr) {
			return instr.getOpcode() == StateCoverageClassAdapter.PUTFIELD
					&& instr.getPrevious().getOpcode() == StateCoverageClassAdapter.GETFIELD;
		}

		private void handleLocalVarToFieldAssignment(AbstractInsnNode instr) {

			String target = ((FieldInsnNode) instr).name;
			int source = ((VarInsnNode) instr.getPrevious()).var;

			System.out.println("Find assignement of var " + source
					+ " to field " + target);

			InsnList il = CodeGeneration.generateAddDependencyCode(
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, target),
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, getMethodName(),
							new Integer(source).toString()));
			// PUTFIELD tem tres instruções: ALOAD 0 para carregar o this,
			// ALOAD x para carregar o parametro e o próprio PUTFIELD
			this.instructions.insertBefore(instr.getPrevious()
					.getPrevious(), il);
		}

		private boolean isLocalVarToFieldAssignement(AbstractInsnNode instr) {
			return instr.getOpcode() == StateCoverageClassAdapter.PUTFIELD
					&& (instr.getPrevious().getOpcode() >= StateCoverageClassAdapter.ILOAD && instr
							.getPrevious().getOpcode() <= StateCoverageClassAdapter.ALOAD);
		}

		private void handleFieldToLocalVarAssignment(int var,
				AbstractInsnNode previous) {

			String source = ((FieldInsnNode) previous).name;
			System.out.println("Find assignement of field " + source
					+ "to var " + var);

			InsnList il = CodeGeneration.generateAddDependencyCode(
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, getMethodName(),
							new Integer(var).toString()),
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, source));
			// GETFIELD tem duas instruções: ALOAD 0 para carregar o this e
			// o próprio GETFIELD
			this.instructions.insertBefore(previous.getPrevious(), il);
		}

		private boolean isFieldToLocalVarAssignement(AbstractInsnNode instr) {

			return (instr.getOpcode() >= StateCoverageClassAdapter.ISTORE && instr.getOpcode() <= StateCoverageClassAdapter.ASTORE)
					&& instr.getPrevious().getOpcode() == StateCoverageClassAdapter.GETFIELD;
		}

		boolean isLocalVarToLocalVarAssignment(AbstractInsnNode instr) {

			return (instr.getOpcode() >= StateCoverageClassAdapter.ISTORE && instr.getOpcode() <= StateCoverageClassAdapter.ASTORE)
					&& (instr.getPrevious().getOpcode() >= StateCoverageClassAdapter.ILOAD && instr
							.getPrevious().getOpcode() <= StateCoverageClassAdapter.ALOAD);

		}

		private void handleLocalVarToLocalVarAssignment(int target,
				AbstractInsnNode previous) {
			int source = ((VarInsnNode) previous).var;
			System.out.println("find store in " + source);

			InsnList il = CodeGeneration.generateAddDependencyCode(
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, getMethodName(),
							new Integer(target).toString()),
					CodeGeneration.prepareFullyQualifiedName(this.stateCoverageClassAdapter.owner, getMethodName(),
							new Integer(source).toString()));
			this.instructions.insertBefore(previous, il);
		}

	}
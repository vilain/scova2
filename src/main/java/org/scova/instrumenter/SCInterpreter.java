package org.scova.instrumenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class SCInterpreter extends Interpreter<SCValue> implements Opcodes {

	private boolean debug = true;
	private Set<String> thirdPartNonConstMethods = new HashSet<String>();
	private Set<String> thirdPartPropertyVerifier = new HashSet<String>();

	private ArrayList<String> actualJumpInfluences = new ArrayList<String>();

	class InstrumenterInfo {

		public InsnList instructionList = null;
		public AbstractInsnNode previousInsn = null;

		public InstrumenterInfo(InsnList instructionList,
				AbstractInsnNode previousInsn) {
			this.instructionList = instructionList;
			this.previousInsn = previousInsn;
		}
	}

	private Map<AbstractInsnNode, List<InstrumenterInfo>> instrumentation = new HashMap<AbstractInsnNode, List<InstrumenterInfo>>();

	private String className;
	private MethodNode methodNode;

	public SCInterpreter(String className, MethodNode methodNode) {
		super(ASM4);
		this.className = className;
		this.methodNode = methodNode;

		initThirdPartNonConst();
		initThirdPartPropertyVerifier();
	}

	private void initThirdPartNonConst() {
		this.thirdPartNonConstMethods
				.add("java/util/List.add(Ljava/lang/Object;)Z");
		this.thirdPartNonConstMethods
				.add("java/util/ArrayList.add(Ljava/lang/Object;)Z");
	}

	private void initThirdPartPropertyVerifier() {
		this.thirdPartPropertyVerifier.add("java/util/List.size()I");
		this.thirdPartPropertyVerifier
				.add("java/util/Map.get(Ljava/lang/Object;)Ljava/lang/Object;");
		this.thirdPartPropertyVerifier
				.add("java/util/Hashtable.get(Ljava/lang/Object;)Ljava/lang/Object;");
		this.thirdPartPropertyVerifier
				.add("java/util/List.get(I)Ljava/lang/Object;");
		this.thirdPartPropertyVerifier.add("java/util/Map.size()I");

	}

	public InsnList generateBeginTestCode() {
		return CodeGeneration.generateBeginTestCode(
				CodeGeneration.prepareFullyQualifiedName(
						className, this.getMethodName()));
	}

	public InsnList generatedEndCode() {
		return CodeGeneration.generateEndTestCode(
				CodeGeneration.prepareFullyQualifiedName(
						className, this.getMethodName()));
	}

	@Override
	public SCValue newValue(final Type type) {
		if (type == Type.VOID_TYPE) {
			return null;
		}
		return new SCValue(type == null ? 1 : type.getSize());
	}

	@Override
	public SCValue newOperation(final AbstractInsnNode insn) {
		int size;
		ArrayList<String> identifiers = new ArrayList<String>();
		switch (insn.getOpcode()) {
		case NEW:
			size = 1;
			break;
		case LCONST_0:
		case LCONST_1:
		case DCONST_0:
		case DCONST_1:
			size = 2;
			break;
		case LDC:
			Object cst = ((LdcInsnNode) insn).cst;
			size = cst instanceof Long || cst instanceof Double ? 2 : 1;
			break;
		case GETSTATIC:
			size = Type.getType(((FieldInsnNode) insn).desc).getSize();
			String name = ((FieldInsnNode) insn).name;
			identifiers.add(name);
			break;
		default:
			size = 1;
		}
		return new SCValue(size, insn, identifiers); // TODO
	}

	private String getMethodName() {
		return methodNode.name + methodNode.desc;
	}

	private String extractNameForLoadAndStore(VarInsnNode insn) {

		VarInsnNode varNode = (VarInsnNode) insn;
		String target = "";
		if (debug) { // in debug we have variable names

			String varName = getLocalVariableName(varNode);
			// // há casos aonde o número de variáveis locais contidos em
			// 'localVariables' é menor do que o maxLocals
			// // isso acontece por exemplo, quando há variáveis escondidas no
			// bytecode, por exemplo em um foreach
			// if (varNode.var >= this.methodNode.localVariables.size()) {
			// varName = "hidden" + new Integer(varNode.var).toString();
			// } else {
			// varName = this.methodNode.localVariables.get(varNode.var).name;
			// }
			target = CodeGeneration.prepareFullyQualifiedName(className,
					this.getMethodName(), varName);
		} else {
			target = CodeGeneration.prepareFullyQualifiedName(className,
					this.getMethodName(), new Integer(varNode.var).toString());
		}

		return target;

	}

	String getLocalVariableName(VarInsnNode varNode) {
		for (LocalVariableNode localVar : this.methodNode.localVariables) {
			if (localVar.index == varNode.var)
				return localVar.name;
		}
		// há casos aonde o número de variáveis locais contidos em
		// 'localVariables' é menor do que o maxLocals
		// isso acontece por exemplo, quando há variáveis escondidas no
		// bytecode, por exemplo em um foreach
		return "hidden" + new Integer(varNode.var).toString();
	}

	@Override
	public SCValue copyOperation(final AbstractInsnNode insn,
			final SCValue value) {

		switch (insn.getOpcode()) {
		case ILOAD:
		case LLOAD:
		case FLOAD:
		case DLOAD:
		case ALOAD:
			HashSet<AbstractInsnNode> union = new HashSet<AbstractInsnNode>();
			union.add(insn);
			union.addAll(value.insns);
			String name = extractNameForLoadAndStore((VarInsnNode) insn);
			List<String> identifiers = new ArrayList<String>();
			identifiers.add(name);
			return new SCValue(union.size(), union, identifiers);
		case ISTORE:
		case LSTORE:
		case FSTORE:
		case DSTORE:
		case ASTORE:

			String target = extractNameForLoadAndStore((VarInsnNode) insn);

			// we have to generate clear dependency even if target doesnt have
			// dependencies
			// cause: any assignment clear all dependencies
			// example:
			// int a = 0;
			// int b = a;
			// b = 0; <- b deps are empty after this
			generateClearDependency(target, insn);

			for (String identifier : value.getIdentifiers()) {

				System.out.println("Found dependency: " + target + " <- "
						+ identifier);
				this.addInstrumentation(CodeGeneration
						.generateAddDependencyCode(target, identifier),
						insn);
			}

			return new SCValue(value.getSize(), insn);
		}

		return new SCValue(value.getSize(), insn); // TODO
	}

	@Override
	public SCValue unaryOperation(final AbstractInsnNode insn,
			final SCValue value) {

		// repassamos para a próxima instrução os identificadores que chegaram
		// até aqui
		List<String> identifiers = value.getIdentifiers();
		int size;
		switch (insn.getOpcode()) {
		case LNEG:
		case DNEG:
		case I2L:
		case I2D:
		case L2D:
		case F2L:
		case F2D:
		case D2L:
			size = 2;
			break;
		case GETFIELD:
			FieldInsnNode fieldInsn = (FieldInsnNode) insn;
			String name = CodeGeneration.prepareFullyQualifiedName(
					fieldInsn.owner, fieldInsn.name);
			identifiers.clear();
			identifiers.add(name);
			size = Type.getType(fieldInsn.desc).getSize();
			break;
		default:
			size = 1;
		}
		return new SCValue(size, insn, identifiers);
	}

	private void generateClearDependency(String identifier, AbstractInsnNode insn) {
		System.out.println("Generate ClearDependenciesOf: " + identifier);
		this.addInstrumentation(
				CodeGeneration.generateClearDependencies(identifier),
				insn);
	}

	@Override
	public SCValue binaryOperation(final AbstractInsnNode insn,
			final SCValue value1, final SCValue value2) {

		int size = 1;
		switch (insn.getOpcode()) {
		case PUTFIELD:
			size = 2;
			FieldInsnNode fieldInsn = (FieldInsnNode) insn;

			if (!WhiteList.isIgnoredField(fieldInsn.name)) {

				String fullyQualifiedName = CodeGeneration
						.prepareFullyQualifiedName(fieldInsn.owner,
								fieldInsn.name);

				generateClearDependency(fullyQualifiedName, insn);

				for (String identifier : value2.getIdentifiers()) {
					this.addInstrumentation(CodeGeneration
							.generateAddDependencyCode(fullyQualifiedName,
									identifier)
									, insn);
					System.out
							.println("PUTFIELD found for field " + identifier);
				}

				this.addInstrumentation(CodeGeneration
						.generateAddModification(CodeGeneration
								.prepareFullyQualifiedName(fieldInsn.owner,
										fieldInsn.name))
										, insn);
			}
			return new SCValue(size, insn);
		case LALOAD:
		case DALOAD:
		case LADD:
		case DADD:
		case LSUB:
		case DSUB:
		case LMUL:
		case DMUL:
		case LDIV:
		case DDIV:
		case LREM:
		case DREM:
		case LSHL:
		case LSHR:
		case LUSHR:
		case LAND:
		case LOR:
		case LXOR:
			size = 2;
			break;

		default:
			size = 1;
		}
		ArrayList<String> identifiers = new ArrayList<String>();
		identifiers.addAll(value1.getIdentifiers());
		identifiers.addAll(value2.getIdentifiers());
		return new SCValue(size, insn, identifiers);
	}

	@Override
	public SCValue ternaryOperation(final AbstractInsnNode insn,
			final SCValue value1, final SCValue value2, final SCValue value3) {

		return new SCValue(1, insn); // TODO
	}

	@Override
	public SCValue naryOperation(final AbstractInsnNode insn,
			final List<? extends SCValue> values) {

		// TODO: refactor!!!
		List<String> identifiers = new ArrayList<String>();

		int size;
		// String name = "";
		int opcode = insn.getOpcode();
		if (opcode == MULTIANEWARRAY) {
			size = 1;
		} else {
			String methodName = (opcode == INVOKEDYNAMIC) ? ((InvokeDynamicInsnNode) insn).name
					: ((MethodInsnNode) insn).name;

			String desc = (opcode == INVOKEDYNAMIC) ? ((InvokeDynamicInsnNode) insn).desc
					: ((MethodInsnNode) insn).desc;

			size = Type.getReturnType(desc).getSize();

			// System.out.println("Calling method " + methodName + " of class "
			// + this.className);
			boolean isProcedure = Type.getReturnType(desc) == Type.VOID_TYPE;
			if (!isProcedure) {

				String fullName = getMethodCallName(insn);

				// se o método não for um property verifier iremos adicionar o
				// nome do método como identifier
				// se for um property verifier apenas repassa os identificadores
				// de todos os parâmetros até agora
				if (!methodCallIsPropertyVerifier(fullName)) {
					identifiers.add(fullName);
				}

				for (SCValue value : values) {
					identifiers.addAll(value.getIdentifiers());
				}

			}

			if (methodIsAssert(methodName)) {
				System.out.println("Found assert : " + methodName);

				for (SCValue value : values) {

					for (String identifier : value.getIdentifiers())
						addInstrumentation(
								CodeGeneration.generateAssertCode(identifier),
								insn);
				}
			}

			instrumentThirdPart(insn, values);
		}

		return new SCValue(size, insn, identifiers);
	}

	private boolean methodIsAssert(String methodName) {
		return WhiteList.isAssertMethod(methodName);
	}

	private String getMethodCallName(AbstractInsnNode insn) {

		assert (insn instanceof MethodInsnNode);
		MethodInsnNode methodInsn = (MethodInsnNode) insn;
		return CodeGeneration.prepareFullyQualifiedName(methodInsn.owner,
				methodInsn.name + methodInsn.desc);
	}

	private void instrumentThirdPart(AbstractInsnNode insn,
			final List<? extends SCValue> values) {

		if (!(insn.getOpcode() == Opcodes.INVOKEINTERFACE
				|| insn.getOpcode() == Opcodes.INVOKESPECIAL || insn
					.getOpcode() == Opcodes.INVOKEVIRTUAL))
			return;

		String fullName = getMethodCallName(insn);

		if (methodCallIsNonConst(fullName)) {

			assert (values.size() >= 1);
			assert (!values.get(0).getIdentifiers().isEmpty());
			// TODO: não sei se isso funciona...
			addInstrumentation(
					CodeGeneration.generateAddModification(values.get(0)
							.getIdentifiers().get(0)),
					insn);
		}
	}

	private boolean methodCallIsPropertyVerifier(String fullName) {
		return this.thirdPartPropertyVerifier.contains(fullName);
	}

	/**
	 * Return true if the method call passed modifies the object
	 * 
	 * @param insn
	 * @return
	 */
	private boolean methodCallIsNonConst(String methodName) {
		return thirdPartNonConstMethods.contains(methodName);
	}

	@Override
	public void returnOperation(final AbstractInsnNode insn,
			final SCValue value, final SCValue expected) {

		if (value.getIdentifiers().isEmpty()
				&& this.actualJumpInfluences.isEmpty())
			return;

		String target = CodeGeneration.prepareFullyQualifiedName(className,
				this.getMethodName());

		generateClearDependency(target, insn);

		for (String identifier : value.getIdentifiers()) {
			String source = identifier;
			addInstrumentation(
					CodeGeneration.generateAddDependencyCode(target, source),
					insn);
		}

		for (String identifier : this.actualJumpInfluences) {
			String source = identifier;
			addInstrumentation(
					CodeGeneration.generateAddDependencyCode(target, source),
					insn);
		}
	}

	@Override
	public SCValue merge(final SCValue d, final SCValue w) {

		return new SCValue(d.getSize(), d.insns, d.getIdentifiers());
	}

	public void addInstrumentation(InsnList insnList,
			AbstractInsnNode previousNode) {
		if (!instrumentation.containsKey(previousNode))
			instrumentation
					.put(previousNode, new ArrayList<InstrumenterInfo>());

		this.instrumentation.get(previousNode).add(
				new InstrumenterInfo(insnList, previousNode));
	}

	public boolean hasInstrumentationAt(AbstractInsnNode insn) {
		return this.instrumentation.containsKey(insn);
	}

	public InsnList getInstrumentationFor(AbstractInsnNode insn) {
		InsnList result = new InsnList();
		List<InstrumenterInfo> instrumented = this.instrumentation.get(insn);
		for (InstrumenterInfo insnInfo : instrumented) {
			result.add(insnInfo.instructionList);
		}
		return result;
	}

	public void storeJumpInfluences(int jumpInsn, Frame<SCValue> jumpFrame,
			int successor) {
		AbstractInsnNode node = this.methodNode.instructions.get(jumpInsn);
		if (node instanceof JumpInsnNode) {
			SCValue jumpTop = jumpFrame.getStack(jumpFrame.getStackSize() - 1);
			actualJumpInfluences.clear();
			actualJumpInfluences.addAll(jumpTop.getIdentifiers());
		}
	}

}

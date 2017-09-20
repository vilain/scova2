package org.scova.instrumenter;

import java.io.File;


import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class StateCoverageAsm {

	/**
	 * Instrument a class with the state coverage logger
	 * 
	 * @param inputClass
	 *            the absolute path of the input class
	 * @param outputClass
	 *            the absolute path of the instrumented output class
	 */
	public void instrumentClass(String inputClass, String outputClass) {

		byte[] b = null;

		// adapts the class on the fly
		try {
			InputStream inputStream = new FileInputStream(inputClass);
			ClassReader cr = new ClassReader(inputStream);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			ClassVisitor cv = new StateCoverageClassAdapter(cw, true);

			System.out.println("BEGINNING INSTRUMENTATION OF " + inputClass);
			cr.accept(cv, 0);
			b = cw.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileOutputStream fos = new FileOutputStream(outputClass);
			fos.write(b);
			fos.close();
		} catch (IOException e) {
		}

	}

	public void instrumentClass(String className) throws ClassNotFoundException {

		instrumentClass(className, className + ".class.adapted");
	}

	private String extractRelativeTo(String root, String absolute) {
		root = root.replace("\\", "/");
		absolute = absolute.replace("\\", "/");
		return absolute.replace(root, "");
	}

	private void clearFolder(String folderPath) {
		File folder = new File(folderPath);
		if (folder.exists()) {
			System.out.println("deleting folder " + folderPath);
			try {

				FileUtils.deleteDirectory(new File(folderPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error ao deletar folder" + folderPath);
				e.printStackTrace();
			}
			// folder.delete();
		}

	}

	private void instrumentRecursivelyFolder(File folder, String inputRoot,
			String outputRoot) throws ClassNotFoundException, IOException {

		File[] files = folder.listFiles();

		String outputFolder = outputRoot
				+ extractRelativeTo(inputRoot, folder.getAbsolutePath());
		Files.createDirectories(new File(outputFolder).toPath());

		for (File file : files) {
			if (file.isDirectory()) {
				// create folder...
				instrumentRecursivelyFolder(file, inputRoot, outputRoot);
			} else {

				String absolutePath = file.getAbsolutePath();
				String outputPath = outputRoot
						+ extractRelativeTo(inputRoot, absolutePath);

				if (absolutePath.endsWith(".class")) {
					instrumentClass(absolutePath, outputPath);
				} else {
					Files.copy(file.toPath(), new File(outputPath).toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				}

			}
		}
	}

	public void instrumentFolder(String inputFolder, String outputFolder)
			throws ClassNotFoundException, IOException {

		this.clearFolder(outputFolder);
		this.instrumentRecursivelyFolder(new File(inputFolder), inputFolder,
				outputFolder);
	}
}

class StateCoverageClassAdapter extends ClassVisitor implements Opcodes {

	String owner;
	private boolean useCoreApi;

	public StateCoverageClassAdapter(final ClassVisitor cv, boolean useCoreApi) {
		super(Opcodes.ASM4, cv);
		this.useCoreApi = useCoreApi;
	}

	@Override
	public void visit(final int version, final int access, final String name,
			final String signature, final String superName,
			final String[] interfaces) {
		owner = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name,
			final String desc, final String signature, final String[] exceptions) {

		if (!useCoreApi) {
			return new ModifiedMethodNode(this, this.cv, ASM4, access, name,
					desc, signature, exceptions, owner);
		} else {

			return new MethodNode(ASM4, access, name, desc, signature,
					exceptions) {

				@Override
				public void visitEnd() {

					super.visitEnd();

					boolean debug = false;
					if (debug) { // debug

						System.out.println("Bytecode of method " + this.name);
						System.out.println(DebugUtils.codeToString(this));

					}

					SCInterpreter interpreter = new SCInterpreter(owner, this);
					SCAnalyzer a = new SCAnalyzer(interpreter);

					try {

						System.out.println("Analysing method " + this.name);
						a.analyze(owner, this);

						boolean isTestMethod = Utils.isTestMethod(this, owner);

						InsnList instrumented = instructions;
						AbstractInsnNode lastInsn = null;

						AbstractInsnNode[] insns = instructions.toArray();
						
						if (isTestMethod && instrumented.size() > 0)
							instrumented.insert(interpreter.generateBeginTestCode());

//						for (int i = 0; i < instructions.size(); ++i) {
//							AbstractInsnNode insn = instructions.get(i);
						
//						Iterator<AbstractInsnNode> i = insns.iterator();
//						while (i.hasNext()) {
							
						for (int i = 0; i < insns.length; ++i) {

//							AbstractInsnNode insn = i.next();
							AbstractInsnNode insn = insns[i];
							Frame<SCValue> frame = a.getFrames()[i];
							
							if (isGoodToLastInsn(insn, frame))
								lastInsn = insn;

							if (interpreter.hasInstrumentationAt(insn)) {

								instrumented
										.insert(lastInsn, interpreter
												.getInstrumentationFor(insn));
							}
							
							if (isReturn(insn) && isTestMethod) {
								instrumented
										.insert(lastInsn, interpreter.generatedEndCode());
								
							}

						}

					} catch (AnalyzerException e) {
						e.printStackTrace();
					}

					accept(cv);

				}

				private boolean isReturn(AbstractInsnNode insn) {
					int insnOpcode = insn.getOpcode();
					return (insnOpcode == ATHROW 
							|| (insnOpcode >= IRETURN && insnOpcode <= RETURN));
				}

				private boolean isGoodToLastInsn(AbstractInsnNode insn
						, Frame<SCValue> insnFrame) {
					if (insn instanceof JumpInsnNode) {

						int insnOpcode = insn.getOpcode();
                        if (insnOpcode != GOTO && insnOpcode != JSR) {
                        	return true;
                        }
					}

					int insnType = insn.getType();
					// only sets insn as lastInstruction if stack is empty
					// and if insn is a FRAME or LABEL
					if (
							// TODO: isso deve ser levado em conta?
							(insnFrame != null && insnFrame.getStackSize() == 0) && 
							(insnType == AbstractInsnNode.LABEL
									|| insnType == AbstractInsnNode.LINE || insnType == AbstractInsnNode.FRAME))
						return true;

					return false;
				}

			};

			// MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
			// exceptions);
			// return mv == null ? null : new StateCoverageCodeAdapter(mv,
			// owner);

		}

	}

}
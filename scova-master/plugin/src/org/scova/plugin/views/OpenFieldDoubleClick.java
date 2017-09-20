package org.scova.plugin.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.scova.plugin.model.ClassResult;
import org.scova.plugin.model.FieldResult;
import org.scova.plugin.model.PackageResult;
import org.scova.plugin.model.TestResult;


public class OpenFieldDoubleClick implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof FieldResult) {
			FieldResult fieldResult = (FieldResult) element;
			ClassResult classResult = (ClassResult) fieldResult.getParent();
			PackageResult packageResult = (PackageResult) classResult
					.getParent();
			TestResult testResult = (TestResult)packageResult.getParent();
			
			new FieldSelectionJob(testResult.getProjectName(), classResult.getFullyQualifiedName(), 1).schedule();

		}

	}

	private static final class FieldSelectionJob extends UIJob {

		private final String projectName;
		private final String className;
		private final int lineNumber;

		public FieldSelectionJob(String projectName, String className, int lineNumber) {
			super("Open java source");
			this.projectName = projectName;
			this.className = className;
			this.lineNumber = lineNumber;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor arg0) {
			IFile classFile = findClass(projectName, className);
			if (classFile != null) {
				return openEditorAtLine(classFile);
			}

			return org.eclipse.core.runtime.Status.CANCEL_STATUS;

		}

		private IStatus openEditorAtLine(IFile classFile) {
			IWorkbenchWindow workbench = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (workbench != null) {
				IWorkbenchWindow wb = workbench;
				try {
					tryToOpen(wb, classFile);
				} catch (CoreException e) {
					return org.eclipse.core.runtime.Status.CANCEL_STATUS;
				}
			}
			return org.eclipse.core.runtime.Status.OK_STATUS;
		}

		private void tryToOpen(IWorkbenchWindow workbench, final IFile file)
				throws PartInitException, CoreException {
			IEditorPart editorPart = IDE.openEditor(workbench.getActivePage(), file);
			if (editorPart instanceof org.eclipse.ui.texteditor.ITextEditor && lineNumber >= 0) {
				ITextEditor textEditor = (ITextEditor) editorPart;
				IEditorInput editorInput = textEditor.getEditorInput();
				openEditorAtLine(textEditor, editorInput);
			}
		}

		private void openEditorAtLine(ITextEditor textEditor,
				IEditorInput editorInput) throws CoreException {
			IDocumentProvider provider = textEditor.getDocumentProvider();
			provider.connect(editorInput);
//			try {
////				org.eclipse.jface.text.IDocument document = provider.getDocument(editorInput);
////				IRegion line = document.getLineInformation(lineNumber);
////				textEditor.selectAndReveal(line.getOffset(), line.getLength());
//			} catch (BadLocationException e) {
//			} finally {
//				provider.disconnect(editorInput);
//			}
		}

		private IFile findClass(final String projectName, final String className) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (IProject project : root.getProjects()) {
				if (project.getName().equals(projectName) && project.isOpen()) {
					IJavaProject javaProject = JavaCore.create(project);
					if (javaProject != null) {
						try {
							IType type = javaProject.findType(className);
							return root.getFile(type.getPath());
						} catch (JavaModelException e) {
						}
					}
				}
			}
			return null;
		}

	}

}

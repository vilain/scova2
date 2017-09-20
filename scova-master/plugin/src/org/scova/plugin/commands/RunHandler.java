package org.scova.plugin.commands;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.*;
import org.eclipse.ui.ide.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ant.core.AntRunner;
import org.scova.plugin.Activator;
import org.scova.plugin.preferences.ScovaPreferencePage;
import org.scova.plugin.views.ScovaProgressMonitor;
import org.scova.plugin.views.ScovaReportView;
import org.scova.plugin.views.ScovaSummaryView;


public class RunHandler extends AbstractHandler {

	private String instrumentationSourceFolder;
	private String instrumentationTargetFolder;
	private String instrumentationSourceClasspath;
	private String instrumentationTargetClasspath;
	private String projectOutputTestFolder;

	private void instrumentClass(IJavaElement javaElement, IJavaProject project) {

		resolvePaths(project);
		
		//String outputPath = findOutputPathOf(javaElement);
		//executeSingleClassInstrumentation(outputPath);

	}

	/*private void executeSingleClassInstrumentation(String classFile) {

		try {
			executeAnt("C:/Program Files/ant/bin/ant.bat",
					"all-single-class", "-Dproject.input.folder="
							+ this.instrumentationSourceFolder,
					"-Dproject.output.folder="
							+ this.instrumentationTargetFolder,
					"-Dproject.input.classpath="
							+ this.instrumentationSourceClasspath,
					"-Dproject.output.classpath="
							+ this.instrumentationTargetClasspath,
					"-Dtest.home=" + projectOutputTestFolder, "-Dtarget.class="
							+ classFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/
	
	private IJavaProject javaProject = null;
	
	private void switchSelection(ExecutionEvent event) {
		IWorkbenchWindow workbenchWindow = HandlerUtil
				.getActiveWorkbenchWindow(event);

		ISelectionService selectionService = workbenchWindow
				.getSelectionService();

		ISelection selection = selectionService.getSelection();

		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection)
					.getFirstElement();

			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
				javaProject = JavaCore.create(project);
				instrumentProject(javaProject);
			} else if (element instanceof IJavaElement) {
				IJavaElement javaElement = (IJavaElement) element;
				javaProject = javaElement.getJavaProject();
				instrumentClass(javaElement, javaProject);
			}

		}

	}

	private void instrumentProject(IJavaProject project) {
		
		resolvePaths(project);
		executeInstrumentation();
		executeReport(instrumentationTargetFolder);

	}

	private void resolvePaths(IJavaProject project) {

		instrumentationSourceFolder = getProjectInputFolder(project
				.getProject());
		instrumentationTargetFolder = getProjectOutputFolder(project
				.getProject());

		Set<String> projectOutputFolders = getOutputFolders(project);

		instrumentationSourceClasspath = getProjectInputClasspath(project,
				projectOutputFolders);

		instrumentationTargetClasspath = instrumentationSourceClasspath;

		for (String folder : projectOutputFolders) {
			String sourceOutputFolder = instrumentationSourceFolder + "/"
					+ folder;
			instrumentationSourceClasspath += ";" + sourceOutputFolder;

			String targetOutputFolder = instrumentationTargetFolder + "/"
					+ folder;
			instrumentationTargetClasspath += ";" + targetOutputFolder;
		}

		projectOutputTestFolder = getProjectOutputTestFolder(project
				.getProject());
		
		dumpPaths();
	}

	private void dumpPaths() {
		System.out.println("project.output.classpath=" + this.instrumentationTargetClasspath);
		System.out.println("project.input.folder=" + this.instrumentationSourceFolder);
		System.out.println("project.output.folder=" + this.instrumentationTargetFolder);
		System.out.println("project.input.classpath=" + this.instrumentationSourceClasspath);
		System.out.println("test.home=" + this.projectOutputTestFolder);
				
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				switchSelection(event);
			}
		}).start();
		

		return null;

//		IProject project = getCurrentProject(HandlerUtil
//				.getActiveWorkbenchWindow(event));
//
//		IJavaProject javaProject = JavaCore.create(project);
//
//		String instrumentationSourceFolder = getProjectInputFolder(project);
//		String instrumentationTargetFolder = getProjectOutputFolder(project);
//
//		Set<String> projectOutputFolders = getOutputFolders(javaProject);
//
//		String instrumenationSourceClasspath = getProjectInputClasspath(
//				javaProject, projectOutputFolders);
//
//		String instrumentationTargetClasspath = instrumenationSourceClasspath;
//
//		for (String folder : projectOutputFolders) {
//			String sourceOutputFolder = instrumentationSourceFolder + "/"
//					+ folder;
//			instrumenationSourceClasspath += ";" + sourceOutputFolder;
//
//			String targetOutputFolder = instrumentationTargetFolder + "/"
//					+ folder;
//			instrumentationTargetClasspath += ";" + targetOutputFolder;
//		}
//
//		String projectOutputTestFolder = getProjectOutputTestFolder();
//
//		try {
//			executeInstrumentation(
//					instrumentationSourceFolder,
//					instrumentationTargetFolder
//					// , classpath //projectInputClasspath
//					, instrumenationSourceClasspath,
//					instrumentationTargetClasspath// , projectOutputClasspath
//					, projectOutputTestFolder);
//
//			executeReport(instrumentationTargetFolder);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		return null;
	}

	public String getProjectOutputTestFolder(IProject project) {
//		return "C:/sc/commons-chain/src/test";
		//return "c:/sc/douglas+picon/src";
		//return "C:/sc/commons-csv/src/test/java";
		return "C:/sc/"+ project.getName() +"/src";		
	}

	public String getProjectOutputFolder(IProject project) {
		return "C:/sc/" + project.getName();
	}

	public String getProjectInputFolder(IProject project) {
		return project.getLocation().toString();
	}

	private void executeAnt(final String... args) throws Exception {
		
//		new Thread(new Runnable() {
//			public void run() {
				AntRunner runner = new AntRunner();
				
				
				//runner.setBuildFileLocation("C:/Users/Rafaela/Documents/GitHub/scova/build/build.xml");
				runner.setBuildFileLocation(Activator.getDefault().getPreferenceStore().getString(ScovaPreferencePage.SCOVA_PATH) + "/build/build.xml");
				
				runner.setArguments(args);
				try {
					runner.run();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
//			}
//		}).join();
		
		
		
		
		//runner.setExecutionTargets(executionTargets);
		/*ProcessBuilder pb = new ProcessBuilder(args

		);
		pb.redirectError();

		File scovaFolder = new File(
				"C:/Users/Rafaela/Documents/GitHub/scova/build");
		if (!scovaFolder.exists())
			throw new Exception("cant find scova path");

		// This should point to where your build.xml file is...
		pb.directory(scovaFolder);
		try {
			Process p = pb.start();
			InputStream is = p.getInputStream();
			int in = -1;
			while ((in = is.read()) != -1) {
				System.out.print((char) in);
			}
			int exitValue = p.waitFor();
			System.out.println("Exited with " + exitValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/

	}

	private void executeReport(final String outputFolder) {
		
		try {
			//executeAnt("C:/Program Files/ant/bin/ant.bat", "report",
				//	"-Dproject.output.folder=" + outputFolder);
			executeAnt("report",
						"-Dproject.output.folder=" + outputFolder);
				
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File fileToOpen = new File(outputFolder + "/report.html");
		

		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					fileToOpen.toURI());
			//IWorkbenchPage page = PlatformUI.getWorkbench()
				//	.getActiveWorkbenchWindow().getActivePage();
			
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					IWorkbenchWindow workbench = 
							PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = workbench.getActivePage();
					try {
						ScovaSummaryView part =
								(ScovaSummaryView) page.showView("scovaeclipse.views.StateCoverage");
						part.setUrl(outputFolder + "/report.html");
						
						ScovaReportView reportView =
								(ScovaReportView) page.showView("scovaeclipse.views.ScovaReportView");
						reportView.showContents(instrumentationTargetFolder, javaProject.getProject().getName());
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}		
					
				}
			});
		

			/*try {
				IDE.openEditorOnFileStore(page, fileStore);
				
			} catch (PartInitException e) {
				// Put your exception handler here if you wish to
			}*/
		} else {
			// Do something if the file does not exist
		}

		//
		// String filePath = "..." ;
		// final IFile inputFile =
		// ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(filePath));
		// if (inputFile != null) {
		// IWorkbenchPage page =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//
		// IEditorDescriptor desc = PlatformUI.getWorkbench().
		// getEditorRegistry().getDefaultEditor(inputFile.getName());
		// page.openEditor(new FileEditorInput(inputFile), desc.getId());
		// // IEditorPart openEditor = IDE.openEditor(page, inputFile);
		// }

		// IWorkbenchPage page = ...;
		// IFile file = ...;
		// IEditorDescriptor desc = PlatformUI.getWorkbench().
		// getEditorRegistry().getDefaultEditor(file.getName());
		// page.openEditor(new FileEditorInput(file), desc.getId());
	}

	public String getProjectInputClasspath(IJavaProject javaProject,
			Set<String> projectOutputFolders) {

		String projectClassPath = "";
		try {
			projectClassPath = getClasspathInfo(javaProject);
			System.out.println(projectClassPath);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// append outputFolders to projectClassPath
		for (String outputFolder : projectOutputFolders) {
			projectClassPath += ";" + outputFolder;
		}

		return projectClassPath.replace("\"", "");
	}

	private void executeInstrumentation() {

		try {
			//executeAnt("C:/Program Files/ant/bin/ant.bat", "all",
			
			executeAnt("all",
					"-Dproject.input.folder="
							+ this.instrumentationSourceFolder,
					"-Dproject.output.folder="
							+ this.instrumentationTargetFolder,
					"-Dproject.input.classpath="
							+ this.instrumentationSourceClasspath,
					"-Dproject.output.classpath="
							+ this.instrumentationTargetClasspath,
					"-Dtest.home=" + projectOutputTestFolder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getClasspathInfo(IJavaProject project) throws Exception {
		String result = "";
		try {
			IClasspathEntry[] entries = project.getResolvedClasspath(false);
			for (int i = 0; i < entries.length; i++) {
				System.out.println(entries[i].getPath().toString());
				if (entries[i].getContentKind() == IPackageFragmentRoot.K_BINARY
						&& entries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					result += convertEntryToSystemPath(project, entries[i]);
				} else if (entries[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
					IJavaProject referredProject = convertEntryToProject(entries[i]);
					result += getClasspathInfo(referredProject);
				} else if (entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					// System.out.println(entries[i].getPath().toString());
				}
			}
		} catch (JavaModelException e) {
			throw new Exception("Cannot setup classpath: " + e.toString());
		}
		return result;
	}

	private String convertEntryToSystemPath(IJavaProject project,
			IClasspathEntry entry) {
		String strPath = null;
		if (entry.getPath().toString().endsWith(".jar")
				|| entry.getPath().toString().endsWith(".zip")) {
			strPath = entry.getPath().toString();
		} else {
			IProject project2 = project.getProject();
			IPath tmpPath = entry.getPath().removeFirstSegments(1);
			IFolder folder = project2.getFolder(tmpPath);
			strPath = folder.getLocation().toString();
		}
		return "\"" + strPath + "\"" + ";";
	}

	private IJavaProject convertEntryToProject(IClasspathEntry entry) {
		String name = entry.getPath().toString().substring(1);
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(name);

		IJavaProject javaProject = JavaCore.create(project);
		return javaProject;
	}	
	
//	private String findOutputPathOf(IJavaElement javaElement) {
		
//		return "C:\\Users\\Aniceto\\workspace\\commons-chain-1.2-src\\target\\classes\\org\\apache\\commons\\chain\\CatalogFactory.class";
		
//		Set<String> outputFolders = getOutputFolders(javaElement.getJavaProject());
//		for (String outputFolder : outputFolders) {
//			
//			IResource resource;
//			try {
//				resource = javaElement.getCorrespondingResource();
//				
//				if (resource != null) {
//					String possiblePath = outputFolder + resource.getProjectRelativePath();
//					File file = new File(possiblePath);
//					if (file.exists())
//						return possiblePath;
//					
//				}
//				
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		//return "";
//	}

	private Set<String> getOutputFolders(IJavaProject javaProject) {
		final Set<String> classpathEntries = new HashSet<String>();
		try {
			for (IClasspathEntry entry : javaProject.getResolvedClasspath(true)) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					final IPath output = entry.getOutputLocation();
					if (output != null) {
						classpathEntries.add(output.removeFirstSegments(1)
								.toString());
					}
				}
			}
			final IPath output = javaProject.getOutputLocation();
			classpathEntries.add(output.removeFirstSegments(1).toString());
		} catch (JavaModelException e) {
			e.printStackTrace();
			// AcceleoCommonPlugin.log(e,false);
		}
		return classpathEntries;
	}

}

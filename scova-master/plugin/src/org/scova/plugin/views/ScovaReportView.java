package org.scova.plugin.views;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.scova.plugin.model.TestResult;


public class ScovaReportView extends ViewPart {

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(new ScovaReportContentProvider());
		viewer.setLabelProvider(new ScovaReportLabelProvider());
		viewer.addDoubleClickListener(new OpenFieldDoubleClick());
		
		viewer.setInput(null);
		viewer.expandAll();

	}
	
	public void showContents(String outputFolder, String projectName) {
		
		List<TestResult> model = new ArrayList<TestResult>();
		
		File[] files = new File(outputFolder).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".json");
			}
		});

		for (File file : files) {

			try {
				FileReader reader = new FileReader(file);

				JSONObject json = (JSONObject) JSONValue.parse(reader);

				assert (json.containsKey("test_name"));
				assert (json.containsKey("modified_states"));
				assert (json.containsKey("covered_states"));
				assert (json.containsKey("useless_covered_states"));
				assert (json.containsKey("state_coverage"));
				assert (json.containsKey("modified"));
				assert (json.containsKey("covered"));
				assert (json.containsKey("useless"));

				String testName = (String)json.get("test_name");
				TestResult testResult = new TestResult(testName, projectName);
				model.add(testResult);
				
				JSONArray modified = (JSONArray) json.get("modified");
				JSONArray covered = (JSONArray) json.get("covered");

				
				for (Object state : modified) {

					String jsonObject = (String) state;

					List<String> info = splitInstanceInfo(jsonObject);
					
					testResult.addField(info, covered.contains(jsonObject));
				}

				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		viewer.setInput(model);
	}
	
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		viewer.getControl().setFocus();
		
	}
	
	public List<String> splitInstanceInfo(String jsonObject) throws Exception {
		List<String> column = new ArrayList<String>();

		String[] instanceInfo = jsonObject.split("\\.");
		// instanceInfo should contains the class name and field name at
		// position 0 and 1, respectively
		if (instanceInfo.length != 2) {
			throw new Exception();
		}
		String packageClass = instanceInfo[0].replace("/", ".");
		String packageName = packageClass.substring(0, packageClass.lastIndexOf('.'));
		String className = packageClass.substring(packageClass.lastIndexOf('.') + 1);
		String fieldName = instanceInfo[1];
		
		column.add(packageName);
		column.add(className);
		column.add(fieldName);
		return column;
	}

}

package org.scova.reporter.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.scova.instrumenter.Utils;

public class HtmlReport {

	private String folder;
	private Set<String> globalModified = new HashSet<String>();
	private Set<String> globalCovered = new HashSet<String>();

	private enum Colors {
		GREEN, RED, YELLOW
	}

	public HtmlReport(String folder) {
		this.folder = folder;
	}

	private void appendTests(StringBuilder builder) throws Exception {

		builder.append("<h3>State Coverage for each test</h3>\n");

		File[] files = new File(folder).listFiles(new FilenameFilter() {

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

				builder.append("<table border=\"1\">\n").append("<tr>\n")
						.append("<td>Test name</td>\n")
						.append("<td>Modified States</td>\n")
						.append("<td>Covered States</td>\n")
						.append("<td>State Coverage</td>\n");

				builder.append("<tr>").append("<td>")
						.append(json.get("test_name")).append("</td>")
						.append("<td>").append(json.get("modified_states"))
						.append("</td>").append("<td>")
						.append(json.get("covered_states")).append("</td>")
						.append("<td>").append(json.get("state_coverage"))
						.append("</td>").append("</tr>").append("</table>");

				builder.append("<table border=\"1\">\n");

				JSONArray modified = (JSONArray) json.get("modified");
				JSONArray covered = (JSONArray) json.get("covered");

				if (!modified.isEmpty())
					appendFieldHeader(builder);

				for (Object state : modified) {

					String jsonObject = (String) state;

					List<String> column = splitInstanceInfo(jsonObject);

					builder.append(generateColoredRow(column, covered
							.contains(jsonObject) ? Colors.GREEN : Colors.RED));

					globalModified.add(jsonObject);
					if (covered.contains(jsonObject))
						globalCovered.add(jsonObject);
				}

				JSONArray useless = (JSONArray) json.get("useless");
				for (Object state : useless) {

					String jsonObject = (String) state;

					// TODO: extract method
					List<String> column = new ArrayList<String>();
					column.add(jsonObject);
					String content = generateColoredRow(column, Colors.YELLOW);
					builder.append(content);
				}

				builder.append("</table>").append("</table></br>\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void appendFieldHeader(StringBuilder builder) {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Class");
		headers.add("Field");

		builder.append(this.generateRow(headers));
	}

	public List<String> splitInstanceInfo(String jsonObject) throws Exception {
		List<String> column = new ArrayList<String>();

		String[] instanceInfo = jsonObject.split("\\.");
		// instanceInfo should contains the class name and field name at
		// position 0 and 1, respectively
		if (instanceInfo.length != 2) {
			throw new Exception();
		}
		column.add(instanceInfo[0].replace("/", "."));
		column.add(instanceInfo[1]);
		return column;
	}

	private List<String> getGlobalResultRow(int totalModified, int totalCovered) {
		List<String> row = new ArrayList<String>();
		row.add(new Integer(totalModified).toString());
		row.add(new Integer(totalCovered).toString());
		row.add(new Double(totalCovered / (double) totalModified).toString());
		return row;
	}

	private List<String> getGlobalResultHeader() {
		List<String> header = new ArrayList<String>();
		header.add("Total Modified");
		header.add("Total Covered");
		header.add("Total State Coverage");
		return header;
	}

	private String generateColoredRow(List<String> columns, Colors color)
			throws Exception {

		switch (color) {
		case GREEN:
			return generateRow(columns, "covered");
		case RED:
			return generateRow(columns, "uncovered");
		case YELLOW:
			return generateRow(columns, "useless");
		}

		throw new Exception("cant reach");
	}

	private String generateRow(List<String> columns) {
		return generateRow(columns, "");
	}

	private String generateRow(List<String> columns, String cssClass) {
		String result = new String();
		result += "<tr class=\"";
		result += cssClass;
		result += "\">";

		for (String column : columns) {
			result += "<td>";
			result += column;
			result += "</td>";
		}
		result += "</tr>";
		return result;
	}

	public void generateHtml() throws Exception {

		StringBuilder testsBuilder = new StringBuilder();
		appendTests(testsBuilder);

		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html>\n")
				.append("<head>\n")
				.append("<title>State Coverage Report</title>\n")
				.append("<!DOCTYPE html>\n")
				.append("<style type=\"text/css\">\n")
				.append(".covered {background-color:#c0ffc0;}\n")
				.append(".uncovered {background-color:#ffa0a0;}\n")
				.append(".useless {background-color:#ffffa5;}\n")
				.append("</style>")

				.append("</head>\n")
				.append("<body>\n")
				.append("<h2>State Coverage Report</h2>\n")

				.append("<h3>Total State Coverage</h3>\n")
				.append("<table border=\"1\">\n")
				.append(generateRow(getGlobalResultHeader()))
				.append(generateRow(getGlobalResultRow(globalModified.size(),
						globalCovered.size()))).append("</table>\n")

				.append("<h3>Global report</h3>\n")
				.append("<table border=\"1\">\n")
				.append(getGlobalResultStates()).append("</table>\n");

		builder.append(testsBuilder);

		// appendTests(builder);

		builder.append("</body>\n").append("</html>\n");

		Utils.dumpToFile(folder + "/report.html", builder.toString());

	}

	private StringBuilder getGlobalResultStates() throws Exception {

		StringBuilder builder = new StringBuilder();
		
		if (!globalModified.isEmpty())
			this.appendFieldHeader(builder);

		for (Object state : globalModified) {

			String jsonObject = (String) state;

			List<String> column = splitInstanceInfo(jsonObject);

			builder.append(generateColoredRow(column, globalCovered
					.contains(jsonObject) ? Colors.GREEN : Colors.RED));

		}
		return builder;
	}

	public static void main(String[] args) {

		// String reportFolder = "c:/users/aniceto/workspace/scova/build";

		if (args.length < 2) {
			System.out.println("Usage: HtmlReport <reportFolder>");
		}

		String reportFolder = args[0];
		System.out.println("Reporting to " + reportFolder);

		HtmlReport report = new HtmlReport(reportFolder);
		try {
			report.generateHtml();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Html report generated at " + reportFolder
				+ "/report.html");

	}

}

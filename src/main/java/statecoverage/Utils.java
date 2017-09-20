package statecoverage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
	
	public static String escape(String str) {
		
		return str.replace("/", "_").replace(".", "_").replace("(", "_").replace(")", "_");
		
	}
	
	public static void dumpToFile(String filename, String content) {
		
		String path = System.getProperty("user.dir");
		
		File file = new File(path + "/" + filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

}

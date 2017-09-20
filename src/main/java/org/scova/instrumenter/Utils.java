package org.scova.instrumenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

public class Utils {

	public static boolean isTestMethod(MethodNode methodNode, String className) {
		
		boolean starts = methodNode.name.startsWith("test");
		
		boolean extend = Utils.extendsClass(className, "junit/framework/TestCase"); // for junit < 4 
		
        				
		return Utils.hasTestAnnotation(methodNode) // for junit4
        		 || (starts && extend); 
	}
    
	public static boolean hasTestAnnotation(MethodNode methodNode) {
		if (methodNode.visibleAnnotations == null)
			return false;

		for (AnnotationNode annotation : methodNode.visibleAnnotations) {

			if (annotation.desc.equals("Lorg/junit/Test;"))
				return true;

		}
		return false;
	}

	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	// TODO: remove dup
	public static void dumpToFile(String filename, String content) {
		File file = new File(filename);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public static boolean extendsClass(String derived, String base) {
		
		Class<?> baseClass, derivedClass;
		
		ClassLoader classLoader = Utils.class.getClassLoader();
        try {
            baseClass = Class.forName(base.replace('/', '.'), false, classLoader);
            derivedClass = Class.forName(derived.replace('/', '.'), false, classLoader);
        } catch (Exception e) {
        	
        	//System.out.println(derived.replace('/', '.'));
            throw new RuntimeException(e.toString());
        }
        
//        return (derivedClass.isAssignableFrom(baseClass));
        
        Class<?> C = derivedClass;
        while (C != null) {
          //System.out.println(C.getName());
          
          if (C.equals(baseClass))
        	  return true;
          
          C = C.getSuperclass();
          
        }
        
        return false;
        
        //return derivedClass.isInstance(baseClass);
	}


}

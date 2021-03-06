package hageldave.generator;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

public class FloatVersionGenerator {

	static final String skipfileDirective = "//#FLOATGEN_SKIPFILE";
	static final String ignoreDirective = "//#FLOATGEN_IGNORE";
	static final String keeplineDirective = "//#FLOATGEN_KEEPLINE";

	public static void main(String[] args) {
		File mainpath_dp = new File("src/main/java/hageldave/ezfftw/dp");
		File mainpath_fp = new File("src/main/java/hageldave/ezfftw/fp");
		File testpath_dp = new File("src/test/java/hageldave/ezfftw/dp");
		File testpath_fp = new File("src/test/java/hageldave/ezfftw/fp");

		if(!mainpath_fp.exists()){
			mainpath_fp.mkdir();
			System.out.println("Created dir " + mainpath_fp.getPath());
		}
		if(!testpath_fp.exists()){
			testpath_fp.mkdir();
			System.out.println("Created dir " + testpath_fp.getPath());
		}
		for(File classFile : listRecursiveFiles(mainpath_dp, (file)->file.getName().endsWith(".java"))){
			int filepathindex = mainpath_dp.getAbsolutePath().length();
			String classFileName = classFile.getAbsolutePath().substring(filepathindex);
			genFloatClass(new File(mainpath_dp,classFileName), new File(mainpath_fp, classFileName));
		}

		for(File classFile : listRecursiveFiles(testpath_dp, (file)->file.getName().endsWith(".java"))){
			int filepathindex = testpath_dp.getAbsolutePath().length();
			String classFileName = classFile.getAbsolutePath().substring(filepathindex);
			genFloatClass(new File(testpath_dp,classFileName), new File(testpath_fp, classFileName));
		}
//		for(String classfile : new String[]{
//				"FFTW_GuruTest.java",
//				"NativeRealArrayTest.java",
//				"PrecisionDependentUtilsTest.java",
//				"FFTTest.java"
//		}){
//			genFloatClass(new File(testpath_dp,classfile), new File(testpath_fp, classfile));
//		}
	}

	static void genFloatClass(File doubleClass, File floatClass){
		System.out.println("Processing " + doubleClass.getAbsolutePath());
		if(!floatClass.getParentFile().exists()){
			floatClass.getParentFile().mkdirs();
			System.out.println("Created dir " + floatClass.getParentFile().getPath());
		}

		String doubleclassname = doubleClass.getName();
		doubleclassname = doubleclassname.substring(0,doubleclassname.length()-5);
		String floatclassname = floatClass.getName();
		floatclassname = floatclassname.substring(0,floatclassname.length()-5);
		
		boolean skipFile = false;
		try(
				Scanner sc = new Scanner(doubleClass);
				FileWriter wr = new FileWriter(floatClass);
				)
		{
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				String keptLine = "";
				if(line.contains(skipfileDirective)){
					skipFile = true;
					break;
				}
				if(!line.contains(ignoreDirective)){
					if(line.contains(keeplineDirective)){
						keptLine = line;
					}
					if(line.contains("class "+doubleclassname) || line.contains("interface " + doubleclassname)){
						String donotmodifynotice = 
								"/* THIS CLASS WAS AUTOMATICALLY GENERATED FROM" + System.lineSeparator() + 
								" * ITS DOUBLE PRECISION VERSION, DO NOT MODIFY" + System.lineSeparator() +
								" */";
						if(line.contains("{")){
							line = line.replace("{", System.lineSeparator()+"{");
						}
						line = line.replaceAll(doubleclassname, floatclassname + System.lineSeparator() + donotmodifynotice);
					} else {
						line = line
								.replace("DOUBLE PRECISION", "FLOAT (SINGLE) PRECISION")
								.replace("double precision", "float (single) precision")
								.replace("Double", "Float")
								.replace("double", "float")
								.replace("fftw_", "fftwf_")
								.replace("_D", "_F")
								.replace(".0", ".0f")
								.replace("ezfftw.dp", "ezfftw.fp")
								;
					}
				}
				if(!keptLine.isEmpty()){
					line = keptLine + System.lineSeparator() + line;
				}
				wr.write(line);
				wr.write(System.lineSeparator());
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		if(skipFile){
			floatClass.delete();
			System.out.println("Skipped " + floatClass.getPath());
		} else {
			System.out.println("Done creating " + floatClass.getPath());
		}
	}

	static Collection<File> listRecursiveFiles(File baseDir, FileFilter filter) {
		File[] files = baseDir.listFiles((file)->file.isFile() && filter.accept(file));
		LinkedList<File> fileList = new LinkedList<>();
		fileList.addAll(Arrays.asList(files));
		// recurse down into folders
		for(File dir : baseDir.listFiles((file)->file.isDirectory())){
			fileList.addAll(listRecursiveFiles(dir, filter));
		}
		return fileList;
	}
	
}

package hageldave.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FloatVersionGenerator {

	public static void main(String[] args) {
		File path = new File("src/main/java/hageldave/ezfftw/");
		File natdoublarr = new File(path,"NativeDoubleArray.java");
		File natfloatarr = new File(path,"NativeFloatArray.java");
		genFloatClass(natdoublarr, natfloatarr);
		File fftw_guru = new File(path,"FFTW_Guru.java");
		File fftw_guru_f = new File(path,"FFTWf_Guru.java");
		genFloatClass(fftw_guru, fftw_guru_f);
	}

	static void genFloatClass(File doubleClass, File floatClass){
		String doubleclassname = doubleClass.getName();
		doubleclassname = doubleclassname.substring(0,doubleclassname.length()-5);
		String floatclassname = floatClass.getName();
		floatclassname = floatclassname.substring(0,floatclassname.length()-5);
		try(
				Scanner sc = new Scanner(doubleClass);
				FileWriter wr = new FileWriter(floatClass);
				)
		{
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				if(line.contains("class "+doubleclassname)){
					line = line.replaceAll(doubleclassname, floatclassname);
				} else {
					line = line
							.replaceAll("Double", "Float")
							.replaceAll("double", "float")
							.replaceAll("fftw_", "fftwf_");
				}
				wr.write(line);
				wr.write('\n');
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}

}

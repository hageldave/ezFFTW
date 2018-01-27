package hageldave.fftw.ezfftw;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.fftw3;

public class FFTW_Initializer {

	private static boolean setupDone = false;
	public static void initFFTW(){
		if(!setupDone){
			synchronized (FFTW_Guru.class) {
				if(!setupDone){
					String loadedlib = Loader.load(fftw3.class);
					setupDone = true;
					System.out.format("Loaded FFTW library [%s]%n",loadedlib);
				}
			}
		}
	}
	
}

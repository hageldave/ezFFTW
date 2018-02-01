package hageldave.fftw.ezfftw;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.fftw3;
import org.bytedeco.javacpp.fftw3.fftw_plan;

public final class FFTW_Initializer {

	private static boolean setupDone = false;

	/**
	 * Loads the native fftw3 library.
	 * This method has to be called before using any of the bindings to fftw or cpp
	 * (i.e. {@link DoublePointer} or {@link fftw_plan} and the like).
	 */
	public static void initFFTW(){
		if(!setupDone){
			synchronized (FFTW_Initializer.class) {
				if(!setupDone){
					String loadedlib = Loader.load(fftw3.class);
					setupDone = true;
					System.out.format("Loaded FFTW library [%s]%n",loadedlib);
				}
			}
		}
	}

}

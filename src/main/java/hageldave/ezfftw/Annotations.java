package hageldave.ezfftw;

import java.lang.annotation.Documented;

public final class Annotations {

	/**
	 * The native FFTW API offers double and float precision methods and datatypes (fftw and fftwf).
	 * To offer both through ezfftw this API is split into two packages dp (double precision) and fp
	 * (float or single precision).
	 * The float precision package is generated from the double precision package.
	 * <p>
	 * This annotation serves as a label for classes and methods of the dp package and is intended 
	 * to signalize developers that they are using something related to double precision.
	 * 
	 * @author hageldave
	 */
	@Documented
	public @interface DoublePrecisionVersion {/* */}
	
	/**
	 * The native FFTW API offers double and float precision methods and datatypes (fftw and fftwf).
	 * To offer both through ezfftw this API is split into two packages dp (double precision) and fp
	 * (float or single precision).
	 * The float precision package is generated from the double precision package.
	 * <p>
	 * This annotation serves as a label for classes and methods of the fp package and is intended 
	 * to signalize developers that they are using something related to single (float) precision.
	 * 
	 * @author hageldave
	 */
	@Documented
	public @interface FloatPrecisionVersion {/* */}
	
}

/*
 * ezFFTW - Copyright 2018 David Haegele
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package hageldave.ezfftw;

import java.lang.annotation.Documented;

/**
 * A place for annotation definitions.
 * @author hageldave
 */
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

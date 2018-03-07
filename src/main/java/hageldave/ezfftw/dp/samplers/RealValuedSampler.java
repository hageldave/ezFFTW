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

package hageldave.ezfftw.dp.samplers;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;
import hageldave.ezfftw.dp.writers.RealValuedWriter;

/**
 * The RealValuedSampler interface is a single method interface which provides the
 * {@link #getValueAt(long...)} method for sampling an arbitrary dimensional
 * real domain.
 * <p>
 * It also offers a default convenience method for adding an imaginary component
 * which yields a {@link ComplexValuedSampler}.
 * 
 * @author hageldave
 * 
 * @see RealValuedWriter
 *
 */
@DoublePrecisionVersion
public interface RealValuedSampler {
	
	/**
	 * Returns the value at the specified coordinates.
	 * @param coordinates each value corresponds to one dimension
	 * @return value at specified coords
	 */
	double getValueAt(long... coordinates);

	/**
	 * Combines this RealValuedSampler with another to form a new {@link ComplexValuedSampler}.
	 * This sampler will return the real parts of the complex values, the argument
	 * sampler will return the imaginary parts.
	 * @param imaginarySampler responsible for returning the imaginary values
	 * @return ComplexValuedSampler consisting of this and the argument sampler
	 */
	default ComplexValuedSampler combineToComplexSampler(RealValuedSampler imaginarySampler){
		RealValuedSampler self = this;
		return (imaginary, coordinates) -> {
			if(imaginary) return imaginarySampler.getValueAt(coordinates);
			else return self.getValueAt(coordinates);
		};
	}

}
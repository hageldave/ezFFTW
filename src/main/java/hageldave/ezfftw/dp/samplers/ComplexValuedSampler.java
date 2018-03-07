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
import hageldave.ezfftw.dp.writers.ComplexValuedWriter;

/**
 * The ComplexValuedSampler interface is a single method interface which provides the
 * {@link #getValueAt(boolean, long...)} method for sampling an arbitrary dimensional
 * complex domain.
 * <p>
 * It also offers default convenience methods for obtaining a sampler with swapped real
 * and imaginary parts {@link #getRealImaginarySwappedSampler()} <br>
 * and samplers for only real or imaginary parts {@link #getPartSampler(boolean)}.
 * 
 * @author hageldave
 *
 * @see ComplexValuedWriter
 */
@DoublePrecisionVersion
public interface ComplexValuedSampler {
	
	/**
	 * Returns the real or imaginary part of the value at the specified coordinates.
	 * @param imaginary if true, the imaginary part is returned, else the real part
	 * @param coordinates each value corresponds to one dimension
	 * @return real or imaginary value at specified coords
	 */
	double getValueAt(boolean imaginary, long...coordinates);

	/**
	 * Returns a new sampler based on this with swapped real and imaginary part.<br>
	 * (Will return imaginary when real is requested and vice versa)
	 * @return sampler with swapped parts
	 */
	default ComplexValuedSampler getRealImaginarySwappedSampler(){
		ComplexValuedSampler self = this;
		return (imaginary, coordinates) -> self.getValueAt(!imaginary, coordinates);
	}

	/**
	 * Returns a {@link RealValuedSampler} for the requested part of this sampler. <br>
	 * (Notice for mathematicians: imaginary part is just a real value multiplied by i so this is perfectly fine)
	 * @param imaginary when true, imaginary part sampler is returned, else real part
	 * @return sampler for requested part only
	 */
	default RealValuedSampler getPartSampler(boolean imaginary){
		ComplexValuedSampler self = this;
		return (coordinates)->self.getValueAt(imaginary, coordinates);
	}
}
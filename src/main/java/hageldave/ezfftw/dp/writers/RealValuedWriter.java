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

package hageldave.ezfftw.dp.writers;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;
import hageldave.ezfftw.dp.samplers.RealValuedSampler;

/**
 * The RealValuedWriter interface is a single method interface which provides
 * the {@link #setValueAt(double, long...)} method for writing data of an 
 * arbitrary dimensional real valued domain.
 * <p>
 * It offers a default convenience method for adding an imaginary component
 * which yields a {@link ComplexValuedWriter}.
 * 
 * @author hageldave
 * 
 * @see RealValuedSampler
 * 
 */
@DoublePrecisionVersion
public interface RealValuedWriter {
	
	/**
	 * Sets the value at the specified coordinates.
	 * @param value to be set
	 * @param coordinates each value corresponds to one dimension
	 */
	void setValueAt(double value, long... coordinates);

	/**
	 * Combines this RealValuedWriter with another to form a new {@link ComplexValuedWriter}.
	 * This writer will be responsible for writing the real parts of the complex values,
	 * the argument writer will write the imaginary parts.
	 * @param imaginaryWriter responsible for writing imaginary values
	 * @return {@link ComplexValuedWriter} consisting of this and the argument writer.
	 */
	default ComplexValuedWriter combineToComplexWriter(RealValuedWriter imaginaryWriter){
		RealValuedWriter self = this;
		return (value, imaginary, coordinates) -> {
			if(imaginary)
				imaginaryWriter.setValueAt(value, coordinates);
			else
				self.setValueAt(value, coordinates);
		};
	}
}
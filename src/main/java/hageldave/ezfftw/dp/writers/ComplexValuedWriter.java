package hageldave.ezfftw.dp.writers;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;
import hageldave.ezfftw.dp.samplers.ComplexValuedSampler;

/**
 * The ComplexValuedWriter interface is a single method interface which provides the
 * {@link #setValueAt(value, boolean, long...)} method for writing data of an 
 * arbitrary dimensional complex valued domain.
 * <p>
 * It also offers default convenience methods for obtaining a writer with swapped real
 * and imaginary parts {@link #getRealImaginarySwappedWriter()} <br>
 * and writers for only real or imaginary parts {@link #getPartWriter(boolean)}.
 * 
 * @author hageldave
 * 
 * @see ComplexValuedSampler
 *
 */
@DoublePrecisionVersion
public interface ComplexValuedWriter {
	
	/**
	 * Sets the real or imaginary value at the specified coordinates.
	 * @param value to be set
	 * @param imaginary if true, the value is imaginary, else, the value is real
	 * @param coordinates each value corresponds to one dimension
	 */
	void setValueAt(double value, boolean imaginary, long... coordinates);

	/**
	 * Returns a new writer based on this with swapped real and imaginary part.<br>
	 * (Will write imaginary when real is requested and vice versa)
	 * @return writer with swapped parts
	 */
	default ComplexValuedWriter getRealImaginarySwappedWriter(){
		ComplexValuedWriter self = this;
		return (value, imaginary, coordinates) -> self.setValueAt(value, !imaginary, coordinates);
	}

	/**
	 * Returns a {@link RealValuedWriter} for the requested part of this writer. <br>
	 * (Notice for mathematicians: imaginary part is just a real value multiplied by i so this is perfectly fine)
	 * @param imaginary when true, imaginary part writer is returned, else real part
	 * @return writer for requested part only
	 */
	default RealValuedWriter getPartWriter(boolean imaginary){
		ComplexValuedWriter self = this;
		return (value, coordinates) -> self.setValueAt(value, imaginary, coordinates);
	}
}
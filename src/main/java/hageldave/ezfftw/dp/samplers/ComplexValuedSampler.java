package hageldave.ezfftw.dp.samplers;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;

/**
 * The ComplexValuedSampler interface is a single method interface which provides the
 * {@link #getValueAt(boolean, long...)} method for sampling an arbitrary dimensional
 * complex domain.
 * <p>
 * It also offers default convenience methods for obtaining a sampler with swapped real
 * and imaginary parts {@link #swapRealImaginary()} <br>
 * and samplers for only real or imaginary parts {@link #getPartSampler(boolean)}.
 * 
 * @author hageldave
 *
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
	default ComplexValuedSampler swapRealImaginary(){
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
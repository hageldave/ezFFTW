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
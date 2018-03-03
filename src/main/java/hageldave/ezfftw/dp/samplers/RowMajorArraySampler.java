package hageldave.ezfftw.dp.samplers;

import hageldave.ezfftw.GeneralUtils;
import hageldave.ezfftw.dp.PrecisionDependentUtils;
import hageldave.ezfftw.dp.writers.RowMajorArrayWriter;

import java.util.Arrays;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;

/**
 * The RowMajorArraySampler implements the {@link RealValuedSampler} interface
 * for sampling arbitrary dimensional data that is stored in a double[] in
 * row major order.
 * <p>
 * This class mainly exists to serve as an example implementation for the
 * RealValuedSampler interface.
 * <p>
 * Please note that for performance reasons, no checks are made to the fitness
 * of the coordinates passed to {@link #getValueAt(long...)}. When using this
 * class, it has to be made sure that only appropriate coordinates will be
 * used with it (correct dimensions here: {@link #getDimensions()}).
 * 
 * @author hageldave
 * 
 * @see RowMajorArrayWriter
 *
 */
@DoublePrecisionVersion
public class RowMajorArraySampler implements RealValuedSampler {

	/** the values for sampling */
	public final double[] array;
	private final long[] dimensions;

	/**
	 * Creates a new RowMajorArraySampler that uses the specified double[] assuming
	 * row major order and the specified dimensions.
	 * 
	 * @param array of values in row major order
	 * @param dimensions of the value array
	 * 
	 * @throws IllegalArgumentException <br>
	 * when no dimensions were provided <br>
	 * when one of the dimensions is not positive <br>
	 * when the length of the supplied arrays do not match the number of elements resulting from specified dimensions
	 * @throws NullPointerException if any of the specified arrays is null.
	 */
	public RowMajorArraySampler(double[] array, long... dimensions) {
		GeneralUtils.requirePositive(dimensions.length, ()->"No dimensions were specified, need to pass at least 1 dimension");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(array, numElements, "double[]");
		
		this.array = array;
		this.dimensions = dimensions;
	}

	/**
	 * Calculates the row major index for the specified coordinates using the dimensions of this sampler,
	 * and gets the value.
	 */
	@Override
	public double getValueAt(long... coordinates) {
		return array[(int)GeneralUtils.indexFromCoordinates(coordinates, dimensions)];
	}
	
	/**
	 * Returns a copy of this RowMajorArraySamplers dimensions.
	 * @return dimensions
	 */
	public long[] getDimensions() {
		return Arrays.copyOf(dimensions, dimensions.length);
	}
}
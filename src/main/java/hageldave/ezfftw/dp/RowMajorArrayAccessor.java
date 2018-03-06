package hageldave.ezfftw.dp;

import java.util.Arrays;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;
import hageldave.ezfftw.GeneralUtils;
import hageldave.ezfftw.dp.samplers.RealValuedSampler;
import hageldave.ezfftw.dp.writers.RealValuedWriter;

/**
 * The RowMajorArrayAccessor implements the {@link RealValuedSampler} and 
 * {@link RealValuedWriter} interfaces for sampling and writing arbitrary 
 * dimensional data that is stored in a double[] in row major order.
 * <p>
 * This class mainly exists to serve as an example implementation for the
 * two interfaces interface.
 * <p>
 * Please note that for performance reasons, no checks are made to the fitness
 * of the coordinates passed to {@link #getValueAt(long...)} or {@link #setValueAt(double, long...)}. 
 * When using this class, it has to be made sure that only appropriate coordinates will be
 * used with it (correct dimensions here: {@link #getDimensions()}).
 * 
 * @author hageldave
 *
 */
@DoublePrecisionVersion
public class RowMajorArrayAccessor implements RealValuedSampler, RealValuedWriter {

	/** the values for sampling */
	public final double[] array;
	private final long[] dimensions;

	/**
	 * Creates a new {@link RowMajorArrayAccessor} that uses the specified double[] assuming
	 * row major order and the specified dimensions.
	 * 
	 * @param array of values in row major order
	 * @param dimensions of the value array
	 * 
	 * @throws IllegalArgumentException <br>
	 * when no dimensions were provided <br>
	 * when one of the dimensions is not positive <br>
	 * when the length of the supplied arrays do not match the number of elements resulting from specified dimensions
	 * @throws NullPointerException if <tt>array</tt> is null.
	 */
	public RowMajorArrayAccessor(double[] array, long... dimensions) {
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
	 * First coordinate is least significant (most frequently changing when iterating over array elements).
	 */
	@Override
	public double getValueAt(long... coordinates) {
		return array[(int)GeneralUtils.indexFromCoordinates(coordinates, dimensions)];
	}
	
	/**
	 * Calculates the row major index for the specified coordinates using the dimensions of this writer,
	 * and sets the value.
	 * First coordinate is least significant (most frequently changing when iterating over array elements).
	 */
	@Override
	public void setValueAt(double val, long... coordinates) {
		array[(int)GeneralUtils.indexFromCoordinates(coordinates, dimensions)] = val;
	}

	/**
	 * Returns a copy of this RowMajorArrayAccessors dimensions.
	 * @return dimensions
	 */
	public long[] getDimensions() {
		return Arrays.copyOf(dimensions, dimensions.length);
	}
	
}
package hageldave.ezfftw;

import java.util.Objects;
import java.util.function.Supplier;

import hageldave.ezfftw.samplers.RealValuedSampler;
import hageldave.ezfftw.writers.RealValuedWriter;

/**
 * Class that offers methods for
 * <ul>
 * <li>sanity checks of method arguments </li>
 * <li>handling dimensions and coordinates of long[] type </li>
 * <li>reading and filling {@link NativeDoubleArray}s using 
 * {@link RealValuedSampler} and {@link RealValuedWriter} </li>
 * </ul>
 * 
 * @author hageldave
 *
 */
public class Utils {

	/**
	 * Checks if specified number is greater than zero and throws an
	 * {@link IllegalArgumentException} with the message from the
	 * specified supplier if not.
	 * @param n number to check
	 * @param errmsg message to put in exception
	 * @throws IllegalArgumentException if zero or negative
	 */
	public static void requirePositive(long n, Supplier<String> errmsg){
		if(n < 1){
			throw new IllegalArgumentException(errmsg.get());
		}
	}

	/**
	 * Checks whether all specified dimensions are positive. Throws an
	 * {@link IllegalArgumentException} if not. The error message
	 * will inform about which element of the dimensions array is
	 * not positive.
	 * @param dimensions to check
	 * @throws IllegalArgumentException if one of the dimensions is not positive
	 */
	public static void requirePosititveDimensions(long... dimensions){
		for(int i = 0; i < dimensions.length; i++){
			final int i_ = i;
			requirePositive(dimensions[i], ()->"All dimensions need to be positive, but dimension number "+i_+" is "+dimensions[i_]+".");
		}
	}

	/**
	 * Checks whether the specified objects are equal in the sense of == or {@link Object#equals(Object)}
	 * and will throw an {@link IllegalArgumentException} with the specified error message
	 * if not.
	 * @param o1 object to test
	 * @param o2 object to test
	 * @param errmsg that will be put into exception.
	 * @throws IllegalArgumentException if == and .equals() are false
	 */
	public static void requireEqual(Object o1, Object o2, Supplier<String> errmsg){
		if(!(o1 == o2||o1.equals(o2)) ){
			throw new IllegalArgumentException(errmsg.get());
		}
	}

	/**
	 * Throws NullpointerException or IllegalArgumentException when array is null or does not match
	 * expected length.
	 * @param array to check
	 * @param expectedLength of array
	 * @param arrayIdentifier name of the array argument to provide informal errors.
	 * @throws NullPointerException when array is null
	 * @throws IllegalArgumentException when array length does not equal expected length
	 */
	public static void sanityCheckArray(double[] array, long expectedLength, String arrayIdentifier) {
		Objects.requireNonNull(array, ()->arrayIdentifier + " array cannot be null");
		if(expectedLength != array.length) {
			throw new IllegalArgumentException(
					"Number of elements determined from dimensions does not match array length of "
					+ arrayIdentifier + ". "
					+ expectedLength + " expected but is of length " + array.length);
		}
	}

	/**
	 * Calculates the number of elements from specified dimensions.
	 * Example: given dimensions [16,8,4] this will result in
	 * 16*8*4 = 512 elements.
	 * @param dimensions from which num elements will be calculated
	 * @return number of elements from specified dimensions
	 */
	public static long numElementsFromDimensions(long[] dimensions){
		long numElements = dimensions.length == 0 ? 0:1;
		for(long d:dimensions){
			numElements *= d;
		}
		return numElements;
	}

	/**
	 * Calculates the row major index for the specified coordinates and dimensions.
	 * Example: given dimensions [16,8,4] and coordinates [3,0,2] this will
	 * return 3+0*16+2*16*8 = 259.
	 * @param coordinates for which index is to be calculated
	 * @param dimensions for the space of coordinates
	 * @return row major index for given coordinates and dimensions
	 */
	public static long indexFromCoordinates(long[] coordinates, long[] dimensions) {
		long index = 0;
		long stride = 1;
		for(int i = 0; i < dimensions.length; i++){
			index += coordinates[i]*stride;
			stride *= dimensions[i];
		}
		return index;
	}

	/**
	 * Increments the specified coordinates by 1. This increments the least significant
	 * coordinate first and cascades increments to next coordinate on overflow.
	 * Overflow in this case means that a coordinate gets larger than its specified
	 * corresponding dimension. For dimensions [2,2,2,2] this is like incrementing a
	 * binary number of 4 bits.
	 * When the most significant coordinate would overflow, the coordinates will not
	 * become 0 again but instead the most significant coordinate will be
	 * equal to its corresponding dimension to signalize the maximum coordinate.
	 * This way the you can check for the last increment when coords[n]==dims[n]
	 * with n = dims.length.
	 * @param coordinates to increment by one
	 * @param dimensions for the space of coordinates
	 */
	public static void incrementCoords(long[] coordinates, long[] dimensions){
		incrementCoords(0, coordinates, dimensions);
	}


	/* increment as in binary counter but with arbitrary limits for each position */
	private static void incrementCoords(int i, long[] coordinates, long[] dims){
		coordinates[i]++;
		if(coordinates[i] >= dims[i]){
			coordinates[i] -= dims[i];
			if(i < dims.length-1)
				incrementCoords(i+1,coordinates,dims);
			else
				coordinates[i] = dims[i]; // highest possible number, dont overflow to signalize end
		}
	}

	/**
	 * Fills the specified {@link NativeDoubleArray} in row major order using 
	 * the specified {@link RealValuedSampler} with the specified dimensions.
	 * @param array to be filled
	 * @param sampler to read values from
	 * @param dimensions to be used for sampling
	 * @throws IllegalArgumentException </br>
	 * when no dimensions are specified </br>
	 * when a dimension is not positive </br>
	 * when the number of elements determined from the dimensions is not equal to the length of the array.
	 * @see #readNativeArrayToWriter(NativeDoubleArray, RealValuedWriter, long...)
	 */
	public static void fillNativeArrayFromSampler(NativeDoubleArray array, RealValuedSampler sampler, long... dimensions){
		requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		requirePosititveDimensions(dimensions);
		requireEqual(numElementsFromDimensions(dimensions), array.length,
				()->"number of elements determined from dimensions do not match the number of elements in specified NativeDoubleArray. "
						+ "From dimensions:" + numElementsFromDimensions(dimensions) + " array:" + array.length);
		long index = 0;
		long[] coordinates = new long[dimensions.length];
		while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = sampler.getValueAt(coordinates);
			array.set(index++, val);
			incrementCoords(coordinates, dimensions);
		}
	}

	/**
	 * Reads the values from the specified {@link NativeDoubleArray} 
	 * in row major order and writes them to the specified {@link RealValuedWriter}
	 * assuming the specified dimensions.
	 * @param array to read from
	 * @param writer to write values to
	 * @param dimensions to be used for writing
	 * @throws IllegalArgumentException </br>
	 * when no dimensions are specified </br>
	 * when a dimension is not positive </br>
	 * when the number of elements determined from the dimensions is not equal to the length of the array.
	 * @see #fillNativeArrayFromSampler(NativeDoubleArray, RealValuedSampler, long...)
	 */
	public static void readNativeArrayToWriter(NativeDoubleArray array, RealValuedWriter writer, long... dimensions){
		requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		requirePosititveDimensions(dimensions);
		requireEqual(numElementsFromDimensions(dimensions), array.length,
				()->"number of elements determined from dimensions do not match the number of elements in specified NativeDoubleArray. "
						+ "From dimensions:" + numElementsFromDimensions(dimensions) + " array:" + array.length);
		long index = 0;
		long[] coordinates = new long[dimensions.length];
		while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = array.get(index++);
			writer.setValueAt(val, coordinates);
			incrementCoords(coordinates, dimensions);
		}
	}


}

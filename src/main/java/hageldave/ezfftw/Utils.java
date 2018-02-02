package hageldave.ezfftw;

import java.util.Objects;
import java.util.function.Supplier;

import hageldave.ezfftw.samplers.RealValuedSampler;
import hageldave.ezfftw.writers.RealValuedWriter;

public class Utils {

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
	 * Throws NullpointerException or IllegalArgumentException when array is null or does not match
	 * expected length.
	 * @param array to check
	 * @param expectedLength of array
	 * @param arrayIdentifier name of the array argument to provide informal errors.
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
	 * Checks if specified number is greater than zero and throws an
	 * {@link IllegalArgumentException} with the message from the
	 * specified supplier if not.
	 * @param n number to check
	 * @param errmsg message to put in exception
	 */
	public static void requirePositive(long n, Supplier<String> errmsg){
		if(n < 1){
			throw new IllegalArgumentException(errmsg.get());
		}
	}

	/**
	 * Checks whether all specified dimensions are positive. Throws an
	 * {@link IllegalArgumentException} if not.
	 * @param dimensions to check
	 */
	public static void requirePosititveDimensions(long... dimensions){
		for(int i = 0; i < dimensions.length; i++){
			final int i_ = i;
			requirePositive(dimensions[i], ()->"All dimensions need to be positive, but dimension number "+i_+" is "+dimensions[i_]+".");
		}
	}

	public static void requireEqual(Object o1, Object o2, Supplier<String> errmsg){
		if(!(o1 == o2||o1.equals(o2)) ){
			throw new IllegalArgumentException(errmsg.get());
		}
	}

	public static void fillNativeArrayFromSampler(NativeDoubleArray a, RealValuedSampler sampler, long... dimensions){
		requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		requirePosititveDimensions(dimensions);
		requireEqual(numElementsFromDimensions(dimensions), a.length,
				()->"number of elements determined from dimensions do not match the number of elements in specified NativeDoubleArray. "
						+ "From dimensions:" + numElementsFromDimensions(dimensions) + " array:" + a.length);
		long index = 0;
		long[] coordinates = new long[dimensions.length];
		while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = sampler.getValueAt(coordinates);
			a.set(index++, val);
			incrementCoords(coordinates, dimensions);
		}
	}

	public static void readNativeArrayToWriter(NativeDoubleArray a, RealValuedWriter writer, long... dimensions){
		requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		requirePosititveDimensions(dimensions);
		requireEqual(numElementsFromDimensions(dimensions), a.length,
				()->"number of elements determined from dimensions do not match the number of elements in specified NativeDoubleArray. "
						+ "From dimensions:" + numElementsFromDimensions(dimensions) + " array:" + a.length);
		long index = 0;
		long[] coordinates = new long[dimensions.length];
		while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = a.get(index++);
			writer.setValueAt(val, coordinates);
			incrementCoords(coordinates, dimensions);
		}
	}


}

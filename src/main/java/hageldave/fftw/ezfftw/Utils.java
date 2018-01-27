package hageldave.fftw.ezfftw;

import java.util.Objects;
import java.util.function.Supplier;

public class Utils {

	public static int indexFromCoordinates(int[] coordinates, int[] dimensions) {
		int index = 0;
		int stride = 1;
		for(int i = 0; i < dimensions.length; i++){
			index += coordinates[i]*stride;
			stride *= dimensions[i];
		}
		return index;
	}
	
	public static void incrementCoords(int[] coordinates, int[] dims){
		incrementCoords(0, coordinates, dims);
	}
	
	private static void incrementCoords(int i, int[] coordinates, int[] dims){
		coordinates[i]++;
		if(coordinates[i] >= dims[i]){
			coordinates[i] -= dims[i];
			if(i < dims.length-1)
				incrementCoords(i+1,coordinates,dims);
			else
				coordinates[i] = dims[i]; // highest possible number, dont overflow to signalize end
		}
	}
	
	public static long numElementsFromDimensions(int[] dimensions){
		long numElements = dimensions.length == 0 ? 0:1;
		for(int d:dimensions){
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
	
	public static void requirePosititveDimensions(int... dimensions){
		for(int i = 0; i < dimensions.length; i++){
			final int i_ = i;
			requirePositive(dimensions[i], ()->"All dimensions need to be positive, but dimension number "+i_+" is "+dimensions[i_]+".");
		}
	}
	
	public static void requirePositive(int n, Supplier<String> errmsg){
		if(n < 1){
			throw new IllegalArgumentException(errmsg.get());
		}
	}
}

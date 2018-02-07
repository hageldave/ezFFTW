package hageldave.ezfftw;

import org.bytedeco.javacpp.DoublePointer;

/**
 * The NativeDoubleArray class is a wrapper around a C/C++ array and is used
 * for the native fftw bindings which expect C/C++ double pointers (arrays).
 * For convenience, this class offers methods to read and write values from
 * and to the native array in a more java stylish way, hiding the somewhat
 * cumbersome use of the {@link DoublePointer} class.
 * <p>
 * This class implements the {@link AutoCloseable} interface to enable the use
 * of try-with-resources statement for easy management of the native resources.
 * In case you are too lazy to manage memory in this way, the garbage collector
 * can eventually take care of this object and close it on finalization.
 * <p>
 * Note that native arrays are capable of storing way more data than java arrays
 * when used in a 64-bit environment. While java arrays are limited to
 * {@link Integer#MAX_VALUE} number of elements, native arrays can store more
 * elements than addressable in 32-bit address space.
 * <p>
 * Example Code:
 * <pre>
 * {@code
 * // suppose you have lots of data in 3D space.
 * // So much it won't fit in a single java array.
 * // 2048^3 > 2^32 --> 2048^3 * 8bytes = 64GB of memory
 * double[][][] my3DArray = new double[2048][2048][2048];
 * NativeDoubleArray natArray = new NativeDoubleArray(2048L*2048L*2048L);
 * // lets populate natArray
 * for(long i = 0; i < natArray.length; i++){
 *    double value = i*1.0/natArray.length;
 *    natArray.set(i, value);
 * }
 * // lets read everything to my3DArray
 * for(int i = 0; i < 2048; i++){
 *    for(int j = 0; j < 2048; j++){
 *       double[] row_ij = my3DArray[i][j];
 *       long stride = i*2048L*2048L + j*2048L;
 *       // read as much data as fits into row_ij starting from stride
 *       natArray.get(stride, row_ij);
 *    }
 * }
 * }</pre>
 *
 * @author hageldave
 *
 */
public class NativeDoubleArray implements AutoCloseable {

	/** length of the array (number of elements) */
	public final long length;
	private final DoublePointer pointer;

	/**
	 * Creates a new NativeDoubleArray of specified length.
	 * Throws an {@link IllegalArgumentException} when length is not
	 * positive (length zero is not allowed as well).
	 * @param length number of elements in array
	 * @throws IllegalArgumentException when length is not positive
	 */
	public NativeDoubleArray(long length) {
		FFTW_Initializer.initFFTW();
		Utils.requirePositive(length, ()->"Provided length is not positive");
		this.pointer = new DoublePointer(length);
		this.length = length;
	}

	/**
	 * Sets specified value at specified index.
	 * <pre>
	 * {@code nativeArray[i] = v; }
	 * </pre>
	 * @param i index
	 * @param v value
	 * @return this for chaining
	 */
	public NativeDoubleArray set(long i, double v){
		pointer.position(0).put(i, v);
		return this;
	}

	/**
	 * Copies the specified values starting at index 0
	 * <pre>
	 * {@code for(i=0;i<values.length;i++) nativeArray[i]=values[i]; }
	 * </pre>
	 * @param values to be set
	 * @return this for chaining
	 */
	public NativeDoubleArray set(double... values){
		return set(0, values);
	}

	/**
	 * Copies the specified values starting at specified index
	 * <pre>
	 * {@code for(j=0;j<values.length;j++) nativeArray[i+j]=values[j]; }
	 * </pre>
	 * @param i index to start in native array
	 * @param values to be set
	 * @return this for chaining
	 */
	public NativeDoubleArray set(long i, double... values){
		return set(i, values.length, 0, values);
	}

	/**
	 * Reads <i>length</i> number of elements from the specified
	 * array starting at <i>offset</i> and puts them into this
	 * native array starting at <i>i</i>.
	 * <pre>
	 * {@code for(j=0;j<length;j++) nativeArray[i+j]=values[offset+j]; }
	 * </pre>
	 * @param i offset into this native array
	 * @param length number of values to be copied
	 * @param offset into the specified array
	 * @param values the array from which values will be copied
	 * @return this for chaining
	 */
	public NativeDoubleArray set(long i, int length, int offset, double[] values){
		pointer.position(i).put(values, offset, length);
		return this;
	}

	/**
	 * Sets all elements of this array to the specified value
	 * @param v value for all elements
	 * @return this for chaining
	 */
	public NativeDoubleArray fill(double v){
		for(long i = 0; i < this.length; i++)
			set(i, v);
		return this;
	}

	/**
	 * Returns the value at specified index.
	 * <pre>
	 * {@code return nativeArray[i]; }
	 * </pre>
	 * @param i index
	 * @return value at i
	 */
	public double get(long i){
		return pointer.position(0).get(i);
	}

	/**
	 * Returns <i>length</i> number of values as java array from starting at <i>i</i>.
	 * <pre>
	 * {@code
	 * javArray=new double[length];
	 * for(j=0;j<length;j++) javArray[j]=nativeArray[i+j];
	 * return javArray;
	 * }
	 * </pre>
	 * @param i
	 * @param length
	 * @return array of values
	 */
	public double[] get(long i, int length){
		return get(i, length, 0, new double[length]);
	}

	/**
	 * Copies as many values from native array starting at <i>i</i> into <i>destination</i>
	 * as fit in.
	 * <pre>
	 * {@code
	 * for(j=0;j<destination.length;j++) destination[j]=nativeArray[i+j];
	 * return destination;
	 * }
	 * </pre>
	 * @param i offset into this native array
	 * @param destination array to put values into
	 * @return destination (the argument array)
	 */
	public double[] get(long i, double[] destination) {
		return get(i, destination.length, 0, destination);
	}

	/**
	 * Reads <i>length</i> values from native array starting at <i>i</i> and puts them
	 * into <i>destination</i> starting at <i>offset</i>.
	 * <pre>
	 * {@code
	 * for(j=0;j<length;j++) destination[offset+j]=nativeArray[i+j];
	 * return destination;
	 * }
	 * </pre>
	 * @param i offset into this native array
	 * @param length number of values to copy
	 * @param offset into destination array
	 * @param destination array to put values into
	 * @return destination (the argument array)
	 */
	public double[] get(long i, int length, int offset, double[] destination){
		pointer.position(i).get(destination,offset,length);
		return destination;
	}

	/**
	 * Returns the native DoublePointer of this array, pointing to the position 0.
	 * @return pointer to this array.
	 */
	public DoublePointer getPointer() {
		return pointer.position(0);
	}

	@Override
	public void close() {
		pointer.close();
	}

	/**
	 * Closes this NativeDoubleArray on finalization.
	 */
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}


	@SuppressWarnings({ "resource", "unused" })
	private static void exampleCode() {
		// suppose you have lots of data in 3D space.
		// So much it won't fit in a single java array.
		// 2048^3 > 2^32 --> 2048^3 * 8bytes = 64GB of memory
		double[][][] my3DArray = new double[2048][2048][2048];
		NativeDoubleArray natArray = new NativeDoubleArray(2048L*2048L*2048L);
		// lets populate natArray
		for(long i = 0; i < natArray.length; i++){
			double value = i*1.0/natArray.length;
			natArray.set(i, value);
		}
		// lets read everything to my3DArray
		for(int i = 0; i < 2048; i++){
			for(int j = 0; j < 2048; j++){
				double[] row_ij = my3DArray[i][j];
				long stride = i*2048L*2048L + j*2048L;
				// read as much data as fits into row_ij starting from stride
				natArray.get(stride, row_ij);
			}
		}
	}

}

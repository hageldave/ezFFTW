package hageldave.ezfftw.dp;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import hageldave.ezfftw.GeneralUtils;
import hageldave.ezfftw.dp.samplers.ComplexValuedSampler;
import hageldave.ezfftw.dp.samplers.RealValuedSampler;
import hageldave.ezfftw.dp.writers.ComplexValuedWriter;
import hageldave.ezfftw.dp.writers.RealValuedWriter;

/* --- DOUBLE PRECISION VERSION --- */
public class FFT {


	public static void fft(RealValuedSampler realIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ri, realIn, dimensions);
			return ri;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			PrecisionDependentUtils.readNativeArrayToWriter(real, complexOut.getPartWriter(false), dimensions);
			PrecisionDependentUtils.readNativeArrayToWriter(imag, complexOut.getPartWriter(true), dimensions);
		};
		fft(r_input, c_output, dimensions);
	}

	public static void fft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ri, complexIn.getPartSampler(false), dimensions);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ii, complexIn.getPartSampler(true), dimensions);
			return ii;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			PrecisionDependentUtils.readNativeArrayToWriter(real, complexOut.getPartWriter(false), dimensions);
			PrecisionDependentUtils.readNativeArrayToWriter(imag, complexOut.getPartWriter(true), dimensions);
		};
		fft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		fft(complexIn.swapRealImaginary(), complexOut.swapRealImaginary(), dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, RealValuedWriter realOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ri, complexIn.getPartSampler(false), dimensions);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ii, complexIn.getPartSampler(true), dimensions);
			return ii;
		};
		Consumer<NativeRealArray> r_output = (real) -> {
			PrecisionDependentUtils.readNativeArrayToWriter(real, realOut, dimensions);
		};
		ifft(r_input, i_input, r_output, dimensions);
	}

	public static void fft(double[] realIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		PrecisionDependentUtils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		fft(r_input, c_output, dimensions);
	}

	public static void fft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		PrecisionDependentUtils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		fft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		PrecisionDependentUtils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		ifft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		Consumer<NativeRealArray> r_output = (real) -> {
			real.get(0, realOut);
		};
		ifft(r_input, i_input, r_output, dimensions);
	}

	/**
	 * Calculates a Fast Fourier Transform of the {@link NativeRealArray} provided by the specified {@link Supplier}.
	 * The supplied array is assumed to be in row major order and its dimensionality is specified by the dimensions
	 * argument.
	 * The array will also be closed when this method returns.
	 * The resulting transform will be passed to the specified {@link BiConsumer} with separate real and imaginary
	 * array arguments, which are supposed to be read and will be closed when this method returns.
	 * <p>
	 * The inverse counter part to this method is {@link #ifft(Supplier, Supplier, Consumer, long...)}.
	 * Please note that the FFT and subsequent inverse FFT restores the original signal scaled by the number of
	 * values in the input.
	 * See also <a href="http://fftw.org/faq/section3.html#whyscaled">FFTW FAQ</a> on that topic.
	 *
	 * @param realIn supplier that provides the real valued signal to be transformed in row major order
	 * @param complexOut consumer that reads the resulting transform in split complex format (realPart, imaginaryPart)
	 * and row major order
	 * @param dimensions of the input (e.g. {10,20,30} for 3 dimensions of width=10, height=20 and depth=30)
	 *
	 * @throws IllegalArgumentException </br>
	 * when no dimensions were provided </br>
	 * when one of the dimensions is not positive </br>
	 * when the length of the supplied array does not match the number of elements resulting from specified dimensions
	 *
	 * @see #fft(double[], double[], double[], long...)
	 * @see #fft(RealValuedSampler, ComplexValuedWriter, long...)
	 */
	public static void fft(
			Supplier<NativeRealArray> realIn,
			BiConsumer<NativeRealArray,NativeRealArray> complexOut,
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
				/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = new NativeRealArray(numElements);
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_r2c(a1, a1, a2, dimensions);
			complexOut.accept(a1, a2);
		}
	}

	/**
	 * Calculates a Fast Fourier Transform of the {@link NativeRealArray}s provided by the specified {@link Supplier}s.
	 * The supplied arrays are assumed to be in row major order and their dimensionality is specified by the dimensions
	 * argument.
	 * Also the arrays represent the split complex format where real and imaginary parts are stored in separate arrays.
	 * The arrays will also be closed when this method returns.
	 * The resulting transform will be passed to the specified {@link BiConsumer} with separate real and imaginary
	 * array arguments, which are supposed to be read and will be closed when this method returns.
	 * <p>
	 * The inverse counter part to this method is {@link #ifft(Supplier, Supplier, BiConsumer, long...)}.
	 * Please note that the FFT and subsequent inverse FFT restores the original signal scaled by the number of
	 * (complex) values in the input.
	 * See also <a href="http://fftw.org/faq/section3.html#whyscaled">FFTW FAQ</a> on that topic.
	 *
	 * @param realIn supplier that provides the real valued signal to be transformed in row major order
	 * @param imagIn supplier that provides the imaginary valued signal to be transformed in row major order
	 * @param complexOut consumer that reads the resulting transform in split complex format (realPart, imaginaryPart)
	 * and row major order
	 * @param dimensions of the input (e.g. {10,20,30} for 3 dimensions of width=10, height=20 and depth=30)
	 *
	 * @throws IllegalArgumentException </br>
	 * when no dimensions were provided </br>
	 * when one of the dimensions is not positive </br>
	 * when the length of the supplied arrays do not match the number of elements resulting from specified dimensions
	 *
	 * @see #fft(double[], double[], double[], double[], long...)
	 * @see #fft(ComplexValuedSampler, ComplexValuedWriter, long...)
	 */
	public static void fft(
			Supplier<NativeRealArray> realIn,
			Supplier<NativeRealArray> imagIn,
			BiConsumer<NativeRealArray,NativeRealArray> complexOut,
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = imagIn.get();
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			GeneralUtils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a2.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a1, a2, a1, a2, dimensions);
			complexOut.accept(a1, a2);
		}
	}


	/**
	 * Calculates an inverse Fast Fourier Transform of the {@link NativeRealArray}s provided by the specified
	 * {@link Supplier}s.
	 * The supplied arrays are assumed to be in row major order and their dimensionality is specified by the dimensions
	 * argument.
	 * Also the arrays represent the split complex format where real and imaginary parts are stored in separate arrays.
	 * The arrays will also be closed when this method returns.
	 * The resulting transform will be passed to the specified {@link BiConsumer} with separate real and imaginary
	 * array arguments, which are supposed to be read and will be closed when this method returns.
	 * <p>
	 * The (forward) counter part to this method is {@link #fft(Supplier, Supplier, BiConsumer, long...)}.
	 * Please note that the FFT and subsequent inverse FFT restores the original signal scaled by the number of
	 * (complex) values in the input.
	 * See also <a href="http://fftw.org/faq/section3.html#whyscaled">FFTW FAQ</a> on that topic.
	 *
	 * @param realIn supplier that provides the real valued signal to be transformed in row major order
	 * @param imagIn supplier that provides the imaginary valued signal to be transformed in row major order
	 * @param complexOut consumer that reads the resulting transform in split complex format (realPart, imaginaryPart)
	 * and row major order
	 * @param dimensions of the input (e.g. {10,20,30} for 3 dimensions of width=10, height=20 and depth=30)
	 *
	 * @throws IllegalArgumentException </br>
	 * when no dimensions were provided </br>
	 * when one of the dimensions is not positive </br>
	 * when the length of the supplied arrays do not match the number of elements resulting from specified dimensions
	 *
	 * @see #ifft(double[], double[], double[], double[], long...)
	 * @see #ifft(ComplexValuedSampler, ComplexValuedWriter, long...)
	 */
	public static void ifft(
			Supplier<NativeRealArray> realIn,
			Supplier<NativeRealArray> imagIn,
			BiConsumer<NativeRealArray,NativeRealArray> complexOut,
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = imagIn.get();
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			GeneralUtils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a2.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a2, a1, a2, a1, dimensions);// swapped arguments
			complexOut.accept(a1, a2);
		}
	}

	/**
	 * Calculates an inverse Fast Fourier Transform of the {@link NativeRealArray}s provided by the specified
	 * {@link Supplier}s.
	 * The supplied arrays are assumed to be in row major order and their dimensionality is specified by the dimensions
	 * argument.
	 * Also the arrays represent the split complex format where real and imaginary parts are stored in separate arrays.
	 * The arrays will also be closed when this method returns.
	 * The resulting (purely real valued) transform will be passed to the specified {@link Consumer}, that is supposed
	 * to be read the array which will be closed when this method returns.
	 * <p>
	 * The (forward) counter part to this method is {@link #fft(Supplier, Supplier, BiConsumer, long...)}.
	 * Please note that the FFT and subsequent inverse FFT restores the original signal scaled by the number of
	 * (complex) values in the input.
	 * See also <a href="http://fftw.org/faq/section3.html#whyscaled">FFTW FAQ</a> on that topic.
	 *
	 * @param realIn supplier that provides the real valued signal to be transformed in row major order
	 * @param imagIn supplier that provides the imaginary valued signal to be transformed in row major order
	 * @param realOut consumer that reads the resulting transform in split complex format (realPart, imaginaryPart)
	 * and row major order
	 * @param dimensions of the input (e.g. {10,20,30} for 3 dimensions of width=10, height=20 and depth=30)
	 *
	 * @throws IllegalArgumentException </br>
	 * when no dimensions were provided </br>
	 * when one of the dimensions is not positive </br>
	 * when the length of the supplied arrays do not match the number of elements resulting from specified dimensions
	 *
	 * @see #ifft(double[], double[], double[], double[], long...)
	 * @see #ifft(ComplexValuedSampler, ComplexValuedWriter, long...)
	 */
	public static void ifft(
			Supplier<NativeRealArray> realIn,
			Supplier<NativeRealArray> imagIn,
			Consumer<NativeRealArray> realOut,
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = imagIn.get();
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			GeneralUtils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a2.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2r(a1, a2, a1, dimensions);
			realOut.accept(a1);
		}
	}

}



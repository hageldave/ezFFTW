package hageldave.ezfftw;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import hageldave.ezfftw.samplers.ComplexValuedSampler;
import hageldave.ezfftw.samplers.RealValuedSampler;
import hageldave.ezfftw.writers.ComplexValuedWriter;
import hageldave.ezfftw.writers.RealValuedWriter;

public class FFT {

	public static void fft(RealValuedSampler realIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Supplier<NativeDoubleArray> r_input = ()-> {
			NativeDoubleArray ri = new NativeDoubleArray(numElements);
			Utils.fillNativeArrayFromSampler(ri, realIn, dimensions);
			return ri;
		};
		BiConsumer<NativeDoubleArray, NativeDoubleArray> c_output = (real,imag) -> {
			Utils.readNativeArrayToWriter(real, complexOut.getPartWriter(false), dimensions);
			Utils.readNativeArrayToWriter(imag, complexOut.getPartWriter(true), dimensions);
		};
		fft(r_input, c_output, dimensions);
	}

	public static void fft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Supplier<NativeDoubleArray> r_input = ()-> {
			NativeDoubleArray ri = new NativeDoubleArray(numElements);
			Utils.fillNativeArrayFromSampler(ri, complexIn.getPartSampler(false), dimensions);
			return ri;
		};
		Supplier<NativeDoubleArray> i_input = ()-> {
			NativeDoubleArray ii = new NativeDoubleArray(numElements);
			Utils.fillNativeArrayFromSampler(ii, complexIn.getPartSampler(true), dimensions);
			return ii;
		};
		BiConsumer<NativeDoubleArray, NativeDoubleArray> c_output = (real,imag) -> {
			Utils.readNativeArrayToWriter(real, complexOut.getPartWriter(false), dimensions);
			Utils.readNativeArrayToWriter(imag, complexOut.getPartWriter(true), dimensions);
		};
		fft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		fft(complexIn.swapRealImaginary(), complexOut.swapRealImaginary(), dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, RealValuedWriter realOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Supplier<NativeDoubleArray> r_input = ()-> {
			NativeDoubleArray ri = new NativeDoubleArray(numElements);
			Utils.fillNativeArrayFromSampler(ri, complexIn.getPartSampler(false), dimensions);
			return ri;
		};
		Supplier<NativeDoubleArray> i_input = ()-> {
			NativeDoubleArray ii = new NativeDoubleArray(numElements);
			Utils.fillNativeArrayFromSampler(ii, complexIn.getPartSampler(true), dimensions);
			return ii;
		};
		Consumer<NativeDoubleArray> r_output = (real) -> {
			Utils.readNativeArrayToWriter(real, realOut, dimensions);
		};
		ifft(r_input, i_input, r_output, dimensions);
	}

	public static void fft(double[] realIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeDoubleArray> r_input = ()-> {
			NativeDoubleArray ri = new NativeDoubleArray(numElements);
			ri.set(realIn);
			return ri;
		};
		BiConsumer<NativeDoubleArray, NativeDoubleArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		fft(r_input, c_output, dimensions);
	}

	public static void fft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeDoubleArray> r_input = ()-> {
			NativeDoubleArray ri = new NativeDoubleArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeDoubleArray> i_input = ()-> {
			NativeDoubleArray ii = new NativeDoubleArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		BiConsumer<NativeDoubleArray, NativeDoubleArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		fft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeDoubleArray> r_input = ()-> {
			NativeDoubleArray ri = new NativeDoubleArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeDoubleArray> i_input = ()-> {
			NativeDoubleArray ii = new NativeDoubleArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		BiConsumer<NativeDoubleArray, NativeDoubleArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		ifft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		/* setup argument lambdas */
		Supplier<NativeDoubleArray> r_input = ()-> {
			NativeDoubleArray ri = new NativeDoubleArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeDoubleArray> i_input = ()-> {
			NativeDoubleArray ii = new NativeDoubleArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		Consumer<NativeDoubleArray> r_output = (real) -> {
			real.get(0, realOut);
		};
		ifft(r_input, i_input, r_output, dimensions);
	}

	public static void fft(
			Supplier<NativeDoubleArray> realIn, 
			BiConsumer<NativeDoubleArray,NativeDoubleArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		try(
				/* allocate native resources */
			NativeDoubleArray a1 = realIn.get();
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			Utils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_r2c(a1, a1, a2, dimensions);
			complexOut.accept(a1, a2);
		}
	}

	public static void fft(
			Supplier<NativeDoubleArray> realIn,
			Supplier<NativeDoubleArray> imagIn,
			BiConsumer<NativeDoubleArray,NativeDoubleArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = realIn.get();
			NativeDoubleArray a2 = imagIn.get();
		){
			Utils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			Utils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a1, a2, a1, a2, dimensions);
			complexOut.accept(a1, a2);
		}
	}
	
	public static void ifft(
			Supplier<NativeDoubleArray> realIn,
			Supplier<NativeDoubleArray> imagIn,
			BiConsumer<NativeDoubleArray,NativeDoubleArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = realIn.get();
			NativeDoubleArray a2 = imagIn.get();
		){
			Utils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			Utils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a2, a1, a2, a1, dimensions);// swapped arguments
			complexOut.accept(a1, a2);
		}
	}
	
	public static void ifft(
			Supplier<NativeDoubleArray> realIn,
			Supplier<NativeDoubleArray> imagIn,
			Consumer<NativeDoubleArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = realIn.get();
			NativeDoubleArray a2 = imagIn.get();
		){
			Utils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			Utils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2r(a1, a2, a1, dimensions);// swapped arguments
			complexOut.accept(a1);
		}
	}

}


